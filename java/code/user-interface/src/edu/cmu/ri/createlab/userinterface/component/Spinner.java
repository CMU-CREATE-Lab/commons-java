package edu.cmu.ri.createlab.userinterface.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.userinterface.GUIConstants;
import edu.cmu.ri.createlab.userinterface.util.ImageUtils;
import edu.cmu.ri.createlab.userinterface.util.SwingUtils;

/**
 * <p>
 * <code>Spinner</code> displays a spinning graphic and an optional message to indicate that the user should wait for
 * some action to complete.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Spinner extends JPanel
   {
   public static void main(final String[] args)
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame(Spinner.class.getSimpleName());

               // add the root panel to the JFrame
               jFrame.add(new Spinner());

               // set various properties for the JFrame
               jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
               jFrame.setBackground(Color.WHITE);
               jFrame.setResizable(true);
               jFrame.pack();
               jFrame.setLocationRelativeTo(null);// center the window on the screen
               jFrame.setVisible(true);
               }
            });
      }

   /** Creates a <code>Spinner</code> consisting of only the graphic. */
   public Spinner()
      {
      this(null);
      }

   /**
    * Creates a <code>Spinner</code> consisting of the graphic and the given <code>message</code> appearing above the
    * graphic.
    */
   public Spinner(final String message)
      {
      this(message, GUIConstants.FONT_MEDIUM);
      }

   /**
    * Creates a <code>Spinner</code> consisting of the graphic and the given <code>message</code> appearing above the
    * graphic.  The message is rendered in the given {@link Font}.
    */
   public Spinner(final String message, final Font messageFont)
      {
      final JLabel spinnerGraphic = new JLabel(ImageUtils.createImageIcon("/edu/cmu/ri/createlab/userinterface/component/spinner.gif"));

      this.setBackground(Color.WHITE);

      if (message == null)
         {
         this.add(spinnerGraphic);
         }
      else
         {
         final Component scanningPanelSpacer = SwingUtils.createRigidSpacer(20);
         final GroupLayout scanningPanelLayout = new GroupLayout(this);
         this.setLayout(scanningPanelLayout);
         final JLabel scanningLabel = SwingUtils.createLabel(message, messageFont);
         scanningPanelLayout.setHorizontalGroup(
               scanningPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                     .addComponent(scanningLabel)
                     .addComponent(scanningPanelSpacer)
                     .addComponent(spinnerGraphic)
         );
         scanningPanelLayout.setVerticalGroup(
               scanningPanelLayout.createSequentialGroup()
                     .addComponent(scanningLabel)
                     .addComponent(scanningPanelSpacer)
                     .addComponent(spinnerGraphic)
         );
         }
      }
   }