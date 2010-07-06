package edu.cmu.ri.createlab.speech;

/**
 * <p>
 * <code>Mouth</code> provides methods for speech.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Mouth extends AbstractMouth
   {
   private static final String DEFAULT_VOICE_NAME = "kevin";
   private static final Mouth INSTANCE = new Mouth();

   public static Mouth getInstance()
      {
      return INSTANCE;
      }

   private Mouth()
      {
      super(DEFAULT_VOICE_NAME);
      }
   }
