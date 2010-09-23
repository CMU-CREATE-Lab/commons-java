package edu.cmu.ri.createlab.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>AudioHelper</code> plays tones and clips.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AudioHelper
   {
   private static final Logger LOG = Logger.getLogger(AudioHelper.class);

   private static final int BYTES_PER_SAMPLE = 2;
   private static final int SAMPLE_BUFFER_LENGTH = 4096;
   private static final int SAMPLE_RATE = 44100;
   private static final int MONO = 1;
   public static final int MAX_AMPLITUDE = 10;
   public static final int MIN_AMPLITUDE = 1;
   public static final int DEFAULT_AMPLITUDE = 5;

   /** Plays a tone having the given <code>frequency</code>, <code>amplitude</code>, and <code>duration</code>. */
   public static void playTone(final int frequency, final int amplitude, final int duration)
   {
   if (LOG.isDebugEnabled())
      {
      LOG.debug("AudioHelper.playTone(" + frequency + "," + amplitude + "," + duration + ")");
      }

   final AudioFormat af = new AudioFormat((float)SAMPLE_RATE, 8 * BYTES_PER_SAMPLE, MONO, true, false);
   final SourceDataLine sdl;

   try
      {
      sdl = (SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, af));
      sdl.open(af, SAMPLE_BUFFER_LENGTH * BYTES_PER_SAMPLE);
      sdl.start();
      }
   catch (LineUnavailableException e)
      {
      LOG.debug("Could not open audio device.", e);
      return;
      }

   final int length = (int)(SAMPLE_RATE * ((double)duration / 1000.0));

   int bp = 0;
   final byte[] buffer = new byte[SAMPLE_BUFFER_LENGTH * BYTES_PER_SAMPLE];
   for (int i = 0; i < length; i++)
      {
      int cappedAmplitude = amplitude;
      if (cappedAmplitude > MAX_AMPLITUDE)
         {
         cappedAmplitude = MAX_AMPLITUDE;
         }
      final short multiplier = (short)(cappedAmplitude * Short.MAX_VALUE / MAX_AMPLITUDE);
      final double wave = Math.cos(2 * Math.PI * i * frequency / SAMPLE_RATE);
      final short sample = (short)(multiplier * wave);
      buffer[bp++] = (byte)sample;
      buffer[bp++] = (byte)(sample >> 8);

      if (bp >= buffer.length)
         {
         sdl.write(buffer, 0, buffer.length);
         bp = 0;
         }
      }
   sdl.drain();
   sdl.close();
   }

   /** Plays the sound clip contained in the given <code>byte</code> array. */
   public static void playClip(final byte[] data)
   {
   if (LOG.isDebugEnabled())
      {
      LOG.debug("AudioHelper.playClip(num bytes=" + (data == null ? "null" : data.length) + ")");
      }

   // got this sound-playing code from: http://www.onjava.com/onjava/excerpt/jenut3_ch17/examples/SoundPlayer.java
   try
      {
      final AudioInputStream ain = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
      try
         {
         final DataLine.Info info = new DataLine.Info(Clip.class, ain.getFormat());
         final Clip clip = (Clip)AudioSystem.getLine(info);
         clip.open(ain);
         clip.start();
         }
      catch (LineUnavailableException e)
         {
         LOG.error("LineUnavailableException while playing the sound", e);
         }
      finally
         {
         try
            {
            ain.close();
            }
         catch (IOException e)
            {
            LOG.warn("Ignoring IOException while trying to close the AudioInputStream", e);
            }
         }
      }
   catch (UnsupportedAudioFileException e)
      {
      LOG.error("UnsupportedAudioFileException while playing the sound", e);
      }
   catch (IOException e)
      {
      LOG.error("IOException while playing the sound", e);
      }
   }

   private AudioHelper()
      {
      // private to prevent instantiation
      }
   }
