package edu.cmu.ri.createlab.userinterface;

import java.awt.Color;
import java.awt.Font;

/**
 * <p>
 * <code>GUIConstants</code> defines constants common to GUI applications
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GUIConstants
   {
   public static final String FONT_NAME = "Verdana";
   public static final String MONOSPACED_FONT_NAME = "Courier";

   private static final int LARGE_FONT_SIZE = 20;
   private static final int MEDIUM_LARGE_FONT_SIZE = 17;
   private static final int MEDIUM_FONT_SIZE = 14;
   private static final int NORMAL_FONT_SIZE = 11;
   private static final int TINY_FONT_SIZE = 9;

   public static final Font FONT_LARGE = new Font(FONT_NAME, 0, LARGE_FONT_SIZE);
   public static final Font FONT_MEDIUM_LARGE = new Font(FONT_NAME, 0, MEDIUM_LARGE_FONT_SIZE);
   public static final Font FONT_MEDIUM = new Font(FONT_NAME, 0, MEDIUM_FONT_SIZE);
   public static final Font FONT_NORMAL = new Font(FONT_NAME, 0, NORMAL_FONT_SIZE);
   public static final Font FONT_TINY = new Font(FONT_NAME, 0, TINY_FONT_SIZE);

   public static final Font FONT_MEDIUM_BOLD = new Font(FONT_NAME, Font.BOLD, MEDIUM_FONT_SIZE);

   public static final Font MONOSPACED_FONT_LARGE = new Font(MONOSPACED_FONT_NAME, 0, LARGE_FONT_SIZE);
   public static final Font MONOSPACED_FONT_MEDIUM_LARGE = new Font(MONOSPACED_FONT_NAME, 0, MEDIUM_LARGE_FONT_SIZE);
   public static final Font MONOSPACED_FONT_MEDIUM = new Font(MONOSPACED_FONT_NAME, 0, MEDIUM_FONT_SIZE);
   public static final Font MONOSPACED_FONT_NORMAL = new Font(MONOSPACED_FONT_NAME, 0, NORMAL_FONT_SIZE);
   public static final Font MONOSPACED_FONT_TINY = new Font(MONOSPACED_FONT_NAME, 0, TINY_FONT_SIZE);

   public static final Font BUTTON_FONT = FONT_NORMAL;

   /** The {@link Color} to use for text field backgrounds when the field has an error. */
   public static final Color TEXT_FIELD_BACKGROUND_COLOR_HAS_ERROR = new Color(255, 230, 230);

   /** The {@link Color} to use for text field backgrounds when the field has no error. */
   public static final Color TEXT_FIELD_BACKGROUND_COLOR_NO_ERROR = Color.WHITE;

   public static final char BULLET_CHARACTER = '\u2022';

   private GUIConstants()
      {
      // private to prevent instantiation
      }
   }
