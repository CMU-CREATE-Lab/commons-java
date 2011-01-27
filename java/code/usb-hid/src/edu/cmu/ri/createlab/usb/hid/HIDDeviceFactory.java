package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.usb.hid.mac.MacOSHIDDevice;
import edu.cmu.ri.createlab.usb.hid.windows.WindowsHIDDevice;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>HIDDeviceFactory</code> helps create {@link HIDDevice}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDDeviceFactory
   {
   private static final Logger LOG = Logger.getLogger(HIDDeviceFactory.class);

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
      else if (SystemUtils.IS_OS_MAC_OSX)
         {
         return new MacOSHIDDevice(vendorID, productID);
         }

      final String message = "HID support for this operating system (" + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " [" + SystemUtils.OS_ARCH + "]) has not been implemented.";
      LOG.error(message);
      throw new NotImplementedException(message);
      }

   private HIDDeviceFactory()
      {
      // private to prevent instantiation
      }
   }