package edu.cmu.ri.createlab;

import java.io.File;

/**
 * <p>
 * <code>CreateLabConstants</code> defines various constants common to all CREATE Lab applications.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CreateLabConstants
   {
   /**
    * File path constants common to CREATE Lab applications.  The CREATE Lab home directory defaults to a directory
    * named <code>CREATELab</code> under the user's home directory, but can be overridden by defining the
    * <code>CreateLabHomeDirectory</code> system property and setting it to the desired directory's absolute path.
    */
   public static final class FilePaths
      {
      public static final String CREATE_LAB_HOME_SYSTEM_PROPERTY_NAME = "CreateLabHomeDirectory";
      private static final String DEFAULT_CREATE_LAB_HOME_PATH = System.getProperty("user.home") + File.separator + "CREATELab" + File.separator;
      private static final String CREATE_LAB_HOME_PATH = System.getProperty(CREATE_LAB_HOME_SYSTEM_PROPERTY_NAME, DEFAULT_CREATE_LAB_HOME_PATH);
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
