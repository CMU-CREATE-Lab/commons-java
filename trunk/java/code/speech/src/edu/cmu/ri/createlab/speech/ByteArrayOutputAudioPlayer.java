package edu.cmu.ri.createlab.speech;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import com.sun.speech.freetts.audio.AudioPlayer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * <code>ByteArrayOutputAudioPlayer</code> directs all generated speech to be written to the given {@link ByteArrayOutputStream}.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
// I pretty much stole all this code from FreeTTS's SingleFileAudioPlayer
final class ByteArrayOutputAudioPlayer implements AudioPlayer
   {
   private static final Log LOG = LogFactory.getLog(ByteArrayOutputAudioPlayer.class);

   private AudioFormat currentFormat = null;
   private byte[] outputData;
   private int curIndex = 0;
   private int totBytes = 0;
   private final AudioFileFormat.Type outputType;
   private final List<ByteArrayInputStream> outputList;
   private final ByteArrayOutputStream byteArrayOutputStream;

   /**
    * Creates an audio player for the given {@link AudioFileFormat.Type AudioFileFormat type}  which, instead of playing
    * the sound, writes it to the given <code>byteArrayOutputStream</code>.
    *
    * @param type the type of audio output
    */
   ByteArrayOutputAudioPlayer(final ByteArrayOutputStream byteArrayOutputStream, final AudioFileFormat.Type type)
      {
      this.byteArrayOutputStream = byteArrayOutputStream;
      this.outputType = type;

      outputList = new ArrayList<ByteArrayInputStream>();
      }

   /**
    * Creates an audio player for an AudioFileFormat of type {@link AudioFileFormat.Type#WAVE WAVE} which, instead of
    * playing the sound, writes it to the given <code>byteArrayOutputStream</code>.
    */
   ByteArrayOutputAudioPlayer(final ByteArrayOutputStream byteArrayOutputStream)
      {
      this(byteArrayOutputStream, AudioFileFormat.Type.WAVE);
      }

   /**
    * Sets the audio format for this player
    *
    * @param format the audio format
    */
   public synchronized void setAudioFormat(final AudioFormat format)
      {
      currentFormat = format;
      }

   /**
    * Gets the audio format for this player
    *
    * @return format the audio format
    */
   public synchronized AudioFormat getAudioFormat()
      {
      return currentFormat;
      }

   /** Not supported, has no effect. */
   public void pause()
      {
      }

   /** Not supported, has no effect. */
   public synchronized void resume()
      {
      }

   /** Not supported, has no effect. */
   public synchronized void cancel()
      {
      }

   /** Not supported, has no effect. */
   public synchronized void reset()
      {
      }

   /** Not supported, has no effect. */
   public void startFirstSampleTimer()
      {
      }

   /** Closes this audio player, which causes the sound data to be written to the {@link ByteArrayOutputStream}. */
   public synchronized void close()
      {
      try
         {
         final InputStream is = new SequenceInputStream(Collections.enumeration(outputList));
         final AudioInputStream ais = new AudioInputStream(is, currentFormat, totBytes / currentFormat.getFrameSize());
         AudioSystem.write(ais, outputType, byteArrayOutputStream);
         }
      catch (IOException ioe)
         {
         LOG.error("Can't write audio to byte array output stream", ioe);
         }
      catch (IllegalArgumentException iae)
         {
         LOG.error("Can't write audio type " + outputType, iae);
         }
      }

   /** Returns the current volume which, for this implementation is always 1. */
   public float getVolume()
      {
      return 1.0f;
      }

   /** Not supported, has no effect. */
   public void setVolume(final float volume)
      {
      }

   /**
    *  Starts the output of a set of data. Audio data for a single
    *  utterance should be grouped between begin/end pairs.
    *
    * @param size the size of data between now and the end
    */
   public synchronized void begin(final int size)
      {
      outputData = new byte[size];
      curIndex = 0;
      }

   /**
    *  Marks the end of a set of data. Audio data for a single utterance should be groupd between begin/end pairs.
    *
    *  @return true if the audio was output properly, false if the output was cancelled or interrupted.
    */
   public synchronized boolean end()
      {
      outputList.add(new ByteArrayInputStream(outputData));
      totBytes += outputData.length;
      return true;
      }

   /**
    * Should wait for all queued audio to be played and then return <code>true</code> if the audio played to completion
    * and <code>false</code> if the audio was stopped, but this implementation always returns true.
    */
   public boolean drain()
      {
      return true;
      }

   /** Should return the amount of played since the last mark, but this implementation always returns -1. */
   public synchronized long getTime()
      {
      return -1L;
      }

   /** Not supported, has no effect. */
   public synchronized void resetTime()
      {
      }

   /**
    * Writes the given bytes to the audio stream
    *
    * @param audioData audio data to write to the device
    *
    * @return <code>true</code> of the write completed successfully, <code> false </code>if the write was cancelled.
    */
   public boolean write(final byte[] audioData)
      {
      return write(audioData, 0, audioData.length);
      }

   /**
    * Writes the given bytes to the audio stream
    *
    * @param bytes audio data to write to the device
    * @param offset the offset into the buffer
    * @param size the size into the buffer
    *
    * @return <code>true</code> of the write completed successfully, <code>false</code>if the write was cancelled.
    */
   public synchronized boolean write(final byte[] bytes, final int offset, final int size)
      {
      System.arraycopy(bytes, offset, outputData, curIndex, size);
      curIndex += size;
      return true;
      }

   /** Returns the name of this audioplayer */
   public String toString()
      {
      return "ByteArrayOutputAudioPlayer";
      }

   /** Not supported, has no effect. */
   public void showMetrics()
      {
      }
   }
