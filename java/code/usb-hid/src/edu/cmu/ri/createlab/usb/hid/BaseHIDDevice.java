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

   private final short vendorID;
   private final short productID;
   private byte commandId = 0;
   private final byte[] dataSynchronizationLock = new byte[0];

   protected BaseHIDDevice(final short vendorID, final short productID)
      {
      this.vendorID = vendorID;
      this.productID = productID;
      }

   public final short getVendorID()
      {
      return vendorID;
      }

   public final short getProductID()
      {
      return productID;
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
