package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.usb.hid.hidapi.HIDAPIDeviceHelper;
import edu.cmu.ri.createlab.usb.hid.hidapi.HIDAPILibrary;
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

   /**
    * Returns whether a device matching the given {@link HIDDeviceDescriptor} is plugged in.  Note that a device might
    * be plugged in, but not available (e.g. already in use).
    *
    * @throws NotImplementedException if HID support has not been implemented for the host operating system.
    */
   public static boolean isPluggedIn(final HIDDeviceDescriptor hidDeviceDescriptor) throws NotImplementedException
      {
      if (SystemUtils.IS_OS_WINDOWS)
         {
         return WindowsHIDDevice.isPluggedIn(hidDeviceDescriptor);
         }
      else if (SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_LINUX)
         {
         final DeviceInfo<HIDAPILibrary.hid_device> deviceInfo = HIDAPIDeviceHelper.enumerateDevices(hidDeviceDescriptor,
                                                                                                     new HIDAPIDeviceHelper.HIDDeviceEnumerationProcessor()
                                                                                                     {
                                                                                                     @Override
                                                                                                     public boolean process(final String hidDeviceInfoPath)
                                                                                                        {
                                                                                                        // We always return false since this will only get called if
                                                                                                        // the target device is actually plugged in, and that's all
                                                                                                        // we care about here.  So, return false to signify that the
                                                                                                        // enuerating should stop, and enumerateDevices() should
                                                                                                        // return the device with this path.
                                                                                                        return false;
                                                                                                        }
                                                                                                     });
         return deviceInfo != null;
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
