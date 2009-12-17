package edu.cmu.ri.createlab.serial.device.connectivity;

import java.awt.Component;
import javax.swing.JPanel;
import edu.cmu.ri.createlab.userinterface.util.AbstractTimeConsumingAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultSerialDeviceConnectivityManagerView implements SerialDeviceConnectivityManagerView
   {
   private static final Log LOG = LogFactory.getLog(DefaultSerialDeviceConnectivityManagerView.class);

   private final JPanel panel = new JPanel();
   private final SerialDeviceConnectivityManager manager;
   private final ConnectionStatePanel connectionStatePanel = new ConnectionStatePanel();

   public DefaultSerialDeviceConnectivityManagerView(final SerialDeviceConnectivityManager manager, final Component parentComponent)
      {
      this.manager = manager;

      // register various listeners
      this.manager.addConnectionEventListener(new MySerialDeviceConnectionEventListener());
      connectionStatePanel.addConnectActionListener(new ConnectActionListener(parentComponent));
      connectionStatePanel.addDisconnectActionListener(new DisconnectActionListener(parentComponent));
      connectionStatePanel.addCancelScanActionListener(new CancelScanActionListener(parentComponent));

      panel.add(connectionStatePanel.getComponent());
      }

   public Component getComponent()
      {
      return panel;
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private abstract class MyAbstractTimeConsumingAction extends AbstractTimeConsumingAction
      {
      private MyAbstractTimeConsumingAction(final Component component)
         {
         super(component);
         }

      protected final void executeGUIActionBefore()
         {
         connectionStatePanel.setEnabled(false);
         }

      protected final void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         connectionStatePanel.setEnabled(true);
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class ConnectActionListener extends MyAbstractTimeConsumingAction
      {
      private ConnectActionListener(final Component component)
         {
         super(component);
         }

      protected Object executeTimeConsumingAction()
         {
         LOG.debug("User clicked connect");
         manager.scanAndConnect();
         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class DisconnectActionListener extends MyAbstractTimeConsumingAction
      {
      private DisconnectActionListener(final Component component)
         {
         super(component);
         }

      protected Object executeTimeConsumingAction()
         {
         LOG.debug("User clicked disconnect");
         manager.disconnect();
         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class CancelScanActionListener extends MyAbstractTimeConsumingAction
      {
      private CancelScanActionListener(final Component component)
         {
         super(component);
         }

      protected Object executeTimeConsumingAction()
         {
         LOG.debug("User clicked cancel");
         manager.cancelScanning();
         return null;
         }
      }

   private class MySerialDeviceConnectionEventListener implements SerialDeviceConnectionEventListener
      {
      public void handleConnectionStateChange(final SerialDeviceConnectionState oldState,
                                              final SerialDeviceConnectionState newState,
                                              final String serialPortName)
         {
         connectionStatePanel.updateConnectionState(newState, serialPortName);
         }
      }
   }
