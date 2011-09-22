package edu.cmu.ri.createlab;

import java.io.File;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>CreateLabConstants</code> defines various constants common to all CREATE Lab applications.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CreateLabConstants
   {
   private static final Logger LOG = Logger.getLogger(CreateLabConstants.class);

   public static final class FilePaths
      {
      private static final String CREATE_LAB_HOME_PATH = System.getProperty("user.home") + File.separator + "CREATELab" + File.separator;
      public static final File CREATE_LAB_HOME_DIR = new File(CREATE_LAB_HOME_PATH);
      public static final File AUDIO_DIR = new File(CREATE_LAB_HOME_DIR, "Audio");

      private FilePaths()
         {
         // private to prevent instantiation
         }
      }

   private CreateLabConstants()
      {
      // private to prevent instantiation
      }
   }
