package edu.cmu.ri.createlab.speech;

import java.io.ByteArrayOutputStream;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import edu.cmu.ri.createlab.util.runtime.ShutdownHook;
import edu.cmu.ri.createlab.util.runtime.Shutdownable;
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
   private static final String DEFAULT_VOICE_NAME = "kevin";

   private final Voice voice;

   /** Creates an <code>AbstractMouth</code> using the default voice */
   public AbstractMouth()
   {
   this(DEFAULT_VOICE_NAME);
   }

   /** Creates an <code>AbstractMouth</code> using the specified voice */
   public AbstractMouth(final String voiceName)
   {
   // register this class with the Runtime as a shutdownable class so we can perform some cleanup
   Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));

   final VoiceManager voiceManager = VoiceManager.getInstance();

   // list all available voices
   if (LOG.isInfoEnabled())
      {
      LOG.info("All available voices");
      final Voice[] voices = voiceManager.getVoices();
      for (int i = 0; i < voices.length; i++)
         {
         LOG.info("    " + voices[i].getName() + " (" + voices[i].getDomain() + " domain)");
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

   protected final Voice getVoice()
      {
      return voice;
      }

   /**
    * Warning: this method will sometimes throw NPEs if you make a lot of calls to it in quick succession.  My guess is
    * that the Voice isn't thread safe, but I don't have time at the moment to delve more deeply in to it.  It's
    * probably better to call {@link #getSpeech getSpeech()} instead and then play the resulting WAV file.
    *
    * @deprecated
    */
   public final void speak(final String words)
   {
   // todo: is this the best way to make the mouth speak asynchronously?
   final Runnable runnable =
         new Runnable()
         {
         public void run()
            {
            voice.speak(words);
            }
         };
   new Thread(runnable).start();
   }

   /** Converts the given text into speech, and returns the resulting WAV sound clip as a byte array. */
   public byte[] getSpeech(final String textToSpeak)
   {
   // remember the current audio player
   final AudioPlayer currentAudioPlayer = getVoice().getAudioPlayer();

   // create an audio player that can save the speech in a byte array
   final ByteArrayOutputStream stream = new ByteArrayOutputStream();
   final AudioPlayer audioPlayer = new ByteArrayOutputAudioPlayer(stream);
   voice.setAudioPlayer(audioPlayer);
   voice.speak(textToSpeak);

   // closing the player causes the sound data to be written to the byte array
   audioPlayer.close();

   // revert the audio player
   voice.setAudioPlayer(currentAudioPlayer);

   // fetch the byte array from the stream, and return it
   return stream.toByteArray();
   }

   public final void shutdown()
      {
      LOG.info("Shutting down the mouth.");
      if (voice != null)
         {
         voice.deallocate();
         }
      }
   }
