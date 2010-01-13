package edu.cmu.ri.createlab.serial.device.connectivity;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.userinterface.GUIConstants;
import edu.cmu.ri.createlab.userinterface.util.SpringLayoutUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ConnectionStatePanel
   {
   private static final Log LOG = LogFactory.getLog(ConnectionStatePanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ConnectionStatePanel.class.getName());

   /** Button and label text */
   private static final String BUTTON_TEXT_CONNECT = RESOURCES.getString("button.label.connect");
   private static final String BUTTON_TEXT_DISCONNECT = RESOURCES.getString("button.label.disconnect");
   private static final String BUTTON_TEXT_CANCEL = RESOURCES.getString("button.label.cancel");
   private static final String LABEL_TEXT_CONNECTED = RESOURCES.getString("label.connected");
   private static final String LABEL_TEXT_DISCONNECTED = RESOURCES.getString("label.disconnected");
   private static final String LABEL_TEXT_SCANNING = RESOURCES.getString("label.scanning");
   private static final String LABEL_TEXT_NOT_APPLICABLE = RESOURCES.getString("label.not_applicable");

   private final JPanel panel = new JPanel();

   private final JButton connectCancelDisconnectButton = new JButton();

   private final JLabel connectionStatusValueLabel = createLabel(RESOURCES.getString("label.disconnected"));

   private final JLabel portNameValueLabel = createLabel(RESOURCES.getString("label.not_applicable"));

   private final Collection<ActionListener> connectActionListeners = new HashSet<ActionListener>();
   private final Collection<ActionListener> disconnectActionListeners = new HashSet<ActionListener>();
   private final Collection<ActionListener> cancelScanActionListeners = new HashSet<ActionListener>();
   private Collection<ActionListener> buttonActionListeners;
   private final Runnable disconnectedRunnable =
         new Runnable()
         {
         public void run()
            {
            connectionStatusValueLabel.setText(LABEL_TEXT_DISCONNECTED);
            portNameValueLabel.setText(LABEL_TEXT_NOT_APPLICABLE);
            connectCancelDisconnectButton.setText(BUTTON_TEXT_CONNECT);
            }
         };

   ConnectionStatePanel()
      {
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

      // create the connect/cancel/disconnect button
      connectCancelDisconnectButton.setFont(GUIConstants.FONT_NORMAL);
      connectCancelDisconnectButton.setEnabled(true);
      connectCancelDisconnectButton.setOpaque(false);// required for Mac
      connectCancelDisconnectButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               // notify the listeners
               for (final ActionListener listener : buttonActionListeners)
                  {
                  listener.actionPerformed(e);
                  }
               }
            });

      // set the initial state to disconnected
      updateConnectionState(SerialDeviceConnectionState.DISCONNECTED, "");

      // create a panel for the connect/disconnect button
      final JPanel connectDisconnectButtonPanel = new JPanel();
      connectDisconnectButtonPanel.setLayout(new BoxLayout(connectDisconnectButtonPanel, BoxLayout.Y_AXIS));
      connectDisconnectButtonPanel.add(Box.createGlue());
      connectDisconnectButtonPanel.add(connectCancelDisconnectButton);
      connectDisconnectButtonPanel.add(Box.createGlue());

      // create a panel for the connection state
      final JPanel connectionStatePanel = new JPanel(new SpringLayout());
      final JLabel connectionStatusLabel = createLabel(RESOURCES.getString("label.status"));
      final JLabel portNameLabel = createLabel(RESOURCES.getString("label.port"));
      connectionStatePanel.add(connectionStatusLabel);
      connectionStatePanel.add(connectionStatusValueLabel);
      connectionStatePanel.add(GUIConstants.createRigidSpacer());
      connectionStatePanel.add(GUIConstants.createRigidSpacer(200, 5));
      connectionStatePanel.add(portNameLabel);
      connectionStatePanel.add(portNameValueLabel);
      SpringLayoutUtilities.makeCompactGrid(connectionStatePanel,
                                            3, 2, // rows, cols
                                            0, 0, // initX, initY
                                            5, 0);// xPad, yPad

      // add the button panel and the state panel to the main panel
      panel.add(Box.createGlue());
      panel.add(connectDisconnectButtonPanel);
      panel.add(GUIConstants.createRigidSpacer(10, 0));
      panel.add(connectionStatePanel);
      panel.add(Box.createGlue());
      }

   Component getComponent()
      {
      return panel;
      }

   /** Registers the given {@link ActionListener listener} for when the user clicks the Connect button. */
   void addConnectActionListener(final ActionListener listener)
      {
      if (listener != null)
         {
         connectActionListeners.add(listener);
         }
      }

   /** Registers the given {@link ActionListener listener} for when the user clicks the Disconnect button. */
   void addDisconnectActionListener(final ActionListener listener)
      {
      if (listener != null)
         {
         disconnectActionListeners.add(listener);
         }
      }

   /** Registers the given {@link ActionListener listener} for when the user clicks the Cancel button. */
   void addCancelScanActionListener(final ActionListener listener)
      {
      if (listener != null)
         {
         cancelScanActionListeners.add(listener);
         }
      }

   /**
    * Updates the connection state to the given {@link SerialDeviceConnectionState state} for the given serial port name.  The
    * serial port name may be empty if it's not applicable (e.g. for when the state is disconnected).
    */
   void updateConnectionState(final SerialDeviceConnectionState state, final String serialPortName)
      {
      final Runnable runnable;
      final String portName = (serialPortName == null) ? "" : serialPortName;

      switch (state)
         {
         case CONNECTED:
            runnable =
                  new Runnable()
                  {
                  public void run()
                     {
                     connectionStatusValueLabel.setText(LABEL_TEXT_CONNECTED);
                     portNameValueLabel.setText(portName);
                     connectCancelDisconnectButton.setText(BUTTON_TEXT_DISCONNECT);
                     }
                  };

            // if we're currently connected, then the button reads "Disconnect",
            // so register the disconnect action listeners
            buttonActionListeners = disconnectActionListeners;
            break;

         case DISCONNECTED:
            runnable = disconnectedRunnable;

            // if we're currently disconnected, then the button reads "Connect",
            // so register the connect action listeners
            buttonActionListeners = connectActionListeners;
            break;

         case SCANNING:
            runnable =
                  new Runnable()
                  {
                  public void run()
                     {
                     connectionStatusValueLabel.setText(LABEL_TEXT_SCANNING);
                     portNameValueLabel.setText(portName);
                     connectCancelDisconnectButton.setText(BUTTON_TEXT_CANCEL);
                     }
                  };

            // if we're currently scanning, then the button reads "Cancel",
            // so register the cancel scan action listeners
            buttonActionListeners = cancelScanActionListeners;
            break;

         default:
            LOG.error("Unexpected SerialDeviceConnectionState: " + state);
            runnable = null;
         }

      if (runnable != null)
         {
         SwingUtilities.invokeLater(runnable);
         }
      }

   private JLabel createLabel(final String labelText)
      {
      final JLabel label = new JLabel(labelText);
      label.setFont(GUIConstants.FONT_NORMAL);
      return label;
      }

   void setEnabled(final boolean isEnabled)
      {
      connectCancelDisconnectButton.setEnabled(isEnabled);
      }
   }
