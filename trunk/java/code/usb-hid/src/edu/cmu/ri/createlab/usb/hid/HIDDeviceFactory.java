package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.usb.hid.hidapi.linux.LinuxHIDDevice;
import edu.cmu.ri.createlab.usb.hid.hidapi.mac.MacOSHIDDevice;
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
    * Creates an {@link HIDDevice} for the given {@link HIDDeviceDescriptor}.
    *
    * @throws NotImplementedException if HID support has not been implemented for the host operating system.
    */
   public static HIDDevice create(final HIDDeviceDescriptor hidDeviceDescriptor) throws NotImplementedException
      {
      if (SystemUtils.IS_OS_WINDOWS)
         {
         return new WindowsHIDDevice(hidDeviceDescriptor);
         }
      else if (SystemUtils.IS_OS_MAC_OSX)
         {
         return new MacOSHIDDevice(hidDeviceDescriptor);
         }
      else if (SystemUtils.IS_OS_LINUX)
         {
         return new LinuxHIDDevice(hidDeviceDescriptor);
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
