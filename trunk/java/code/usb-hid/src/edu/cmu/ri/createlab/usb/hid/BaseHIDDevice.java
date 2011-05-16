package edu.cmu.ri.createlab.usb.hid;

/**
 * <p>
 * <code>BaseHIDDevice</code> provides base functionality for HID devices.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseHIDDevice implements HIDDevice
   {
   private static final int MAX_COMMAND_ID = 255;

   private final HIDDeviceDescriptor hidDeviceDescriptor;
   private byte commandId = 0;
   private final byte[] dataSynchronizationLock = new byte[0];

   protected BaseHIDDevice(final HIDDeviceDescriptor hidDeviceDescriptor)
      {
      this.hidDeviceDescriptor = hidDeviceDescriptor;
      }

   public HIDDeviceDescriptor getHidDeviceDescriptor()
      {
      return hidDeviceDescriptor;
      }

   public final short getVendorID()
      {
      return hidDeviceDescriptor.getVendorId();
      }

   public final short getProductID()
      {
      return hidDeviceDescriptor.getProductId();
      }

   protected final byte getCommandId()
      {
      synchronized (dataSynchronizationLock)
         {
         commandId++;
         if (commandId > MAX_COMMAND_ID)
            {
            commandId = 0;
            }
         return commandId;
         }
      }
   }
