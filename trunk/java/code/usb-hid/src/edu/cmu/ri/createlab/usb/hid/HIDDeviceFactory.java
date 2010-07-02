package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.usb.hid.windows.WindowsHIDDevice;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.SystemUtils;

/**
 * <p>
 * <code>HIDDeviceFactory</code> helps create {@link HIDDevice}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDDeviceFactory
   {
   private static final HIDDeviceFactory INSTANCE = new HIDDeviceFactory();

   public static HIDDeviceFactory getInstance()
      {
      return INSTANCE;
      }

   /**
    * Creates an {@link HIDDevice} for the given <code>vendorID</code> and <code>productID</code>.
    *
    * @throws NotImplementedException if HID support has not been implemented for the operating system running the code.
    */
   public static HIDDevice create(final short vendorID, final short productID) throws NotImplementedException
      {
      if (SystemUtils.IS_OS_WINDOWS)
         {
         return new WindowsHIDDevice(vendorID, productID);
         }

      throw new NotImplementedException("Sorry, HID support for your operating system (" + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " [" + SystemUtils.OS_ARCH + "]) has not been implemented.");
      }

   private HIDDeviceFactory()
      {
      // private to prevent instantiation
      }
   }