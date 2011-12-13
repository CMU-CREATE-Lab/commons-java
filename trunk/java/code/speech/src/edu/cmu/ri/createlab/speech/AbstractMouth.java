package edu.cmu.ri.createlab.speech;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import edu.cmu.ri.createlab.util.runtime.ShutdownHook;
import edu.cmu.ri.createlab.util.runtime.Shutdownable;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>AbstractMouth</code> provides methods for speech.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractMouth implements Shutdownable
   {
   private static final Logger LOG = Logger.getLogger(AbstractMouth.class);
   public static final String DEFAULT_VOICE_NAME = "kevin";

   private final Voice voice;
   private final Lock lock = new ReentrantLock();
   private final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory(this.getClass().getName() + ".executor"));

   /** Creates an <code>AbstractMouth</code> using the {@link #DEFAULT_VOICE_NAME default voice} */
   protected AbstractMouth()
      {
      this(DEFAULT_VOICE_NAME);
      }

   /** Creates an <code>AbstractMouth</code> using the specified voice */
   protected AbstractMouth(final String voiceName)
      {
      // register this class with the Runtime as a shutdownable class so we can perform some cleanup
      Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));

      final VoiceManager voiceManager = VoiceManager.getInstance();

      // list all available voices
      if (LOG.isInfoEnabled())
         {
         LOG.info("All available voices");
         final Voice[] voices = voiceManager.getVoices();
         for (final Voice voice1 : voices)
            {
            LOG.info("    " + voice1.getName() + " (" + voice1.getDomain() + " domain)");
            }
         LOG.info("Using voice: " + voiceName);
         }

      voice = voiceManager.getVoice(voiceName);
      if (voice == null)
         {
         LOG.error("Cannot find a voice named " + voiceName + ".  Please specify a different voice.");
         System.exit(1);
         }

      // Allocates the resources for the voice
      voice.allocate();
      }

   /**
    * Converts the given text into speech and plays the resulting audio asynchronously in a separate, dedicated thread.
    * This method is thread safe--multiple calls to it from different threads will be executed (i.e. spoken) in the
    * order received.
    */
   public final void speak(final String words)
      {
      executor.submit(
            new Runnable()
            {
            public void run()
               {
               lock.lock();  // block until condition holds
               try
                  {
                  voice.speak(words);
                  }
               finally
                  {
                  lock.unlock();
                  }
               }
            });
      }

   /** Converts the given text into speech, and returns the resulting WAV sound clip as a byte array. */
   public byte[] getSpeech(final String textToSpeak)
      {
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();

      lock.lock();  // block until condition holds
      try
         {
         // remember the current audio player
         final AudioPlayer currentAudioPlayer = voice.getAudioPlayer();

         // create an audio player that can save the speech in a byte array
         final AudioPlayer audioPlayer = new ByteArrayOutputAudioPlayer(stream);
         voice.setAudioPlayer(audioPlayer);
         voice.speak(textToSpeak);

         // closing the player causes the sound data to be written to the byte array
         audioPlayer.close();

         // revert the audio player
         voice.setAudioPlayer(currentAudioPlayer);
         }
      catch (Exception e)
         {
         LOG.error("AbstractMouth.getSpeech(): Exception while trying to convert the text to speech", e);
         }
      finally
         {
         lock.unlock();
         }

      // fetch the byte array from the stream, and return it
      return stream.toByteArray();
      }

   public final void shutdown()
      {
      LOG.info("Shutting down the mouth.");
      lock.lock();  // block until condition holds
      try
         {
         if (voice != null)
            {
            voice.deallocate();
            }
         }
      finally
         {
         lock.unlock();
         }

      executor.shutdownNow();
      }
   }
