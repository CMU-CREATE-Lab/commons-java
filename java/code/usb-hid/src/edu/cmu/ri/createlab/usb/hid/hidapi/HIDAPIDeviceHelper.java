package edu.cmu.ri.createlab.usb.hid.hidapi;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import edu.cmu.ri.createlab.usb.hid.DeviceInfo;
import edu.cmu.ri.createlab.usb.hid.DeviceInfoImpl;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDAPIDeviceHelper
   {
   private static final Logger LOG = Logger.getLogger(HIDAPIDeviceHelper.class);

   public interface HIDDeviceEnumerationProcessor
      {
      /**
       * This method will be called for each device enumerated by
       * {@link #enumerateDevices(HIDDeviceDescriptor, HIDDeviceEnumerationProcessor)} which matches the given
       * {@link HIDDeviceDescriptor}.  This method should return <code>true</code> if the enumerating should continue,
       * <code>false</code> otherwise.
       */
      boolean process(String hidDeviceInfoPath);
      }

   /**
    * Enumerate devices matching the specified {@link HIDDeviceDescriptor}.  Upon each item in the enumeration, this
    * method will call the {@link HIDDeviceEnumerationProcessor#process(String)} method of the given
    * {@link HIDDeviceEnumerationProcessor}.  The {@link HIDDeviceEnumerationProcessor} should return <code>true</code>
    * if the enumerating should continue, <code>false</code> otherwise.  If the processor returns <code>true</code>,
    * this method will return an instance of {@link DeviceInfo<HIDAPILibrary.hid_device>} for the HID device having
    * the path given to {@link HIDDeviceEnumerationProcessor#process(String)} which caused it to return <code>true</code>.
    * Otherwise, it returns <code>null</code>.
    */
   public static DeviceInfo<HIDAPILibrary.hid_device> enumerateDevices(final HIDDeviceDescriptor targetDeviceDescriptor,
                                                                       final HIDDeviceEnumerationProcessor enumerationProcessor)
      {
      LOG.trace("HIDAPIDeviceHelper.enumerateDevices(): locking...");

      try
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HIDAPIDeviceHelper.enumerateDevices(): enumerating devices with vendor id [" + targetDeviceDescriptor.getVendorIdAsHexString() + "] and product id [" + targetDeviceDescriptor.getProductIdAsHexString() + "]");
            }

         // enumerate the devices, filtering on the one we care about and ignoring the ones already in use
         HIDDeviceInfo hidDeviceInfo = HIDAPILibrary.INSTANCE.hid_enumerate(targetDeviceDescriptor.getVendorId(), targetDeviceDescriptor.getProductId());
         if (hidDeviceInfo != null)
            {
            while (hidDeviceInfo != null)
               {
               if (LOG.isDebugEnabled())
                  {
                  char[] manufacturerCharArray = null;
                  if (hidDeviceInfo.manufacturer_string != null)
                     {
                     final Pointer ptr = hidDeviceInfo.manufacturer_string.getPointer();
                     if (ptr != null)
                        {
                        manufacturerCharArray = ptr.getCharArray(0, 128);
                        }
                     }

                  char[] productCharArray = null;
                  if (hidDeviceInfo.product_string != null)
                     {
                     final Pointer ptr = hidDeviceInfo.product_string.getPointer();
                     if (ptr != null)
                        {
                        productCharArray = ptr.getCharArray(0, 128);
                        }
                     }

                  char[] serialNumberCharArray = null;
                  if (hidDeviceInfo.serial_number != null)
                     {
                     final Pointer ptr = hidDeviceInfo.serial_number.getPointer();
                     if (ptr != null)
                        {
                        serialNumberCharArray = ptr.getCharArray(0, 128);
                        }
                     }

                  LOG.debug("HIDAPIDeviceHelper.enumerateDevices(): found matching device:");
                  LOG.debug("   manufacturer   = [" + (manufacturerCharArray == null ? null : Native.toString(manufacturerCharArray)) + "]");
                  LOG.debug("   product        = [" + (productCharArray == null ? null : Native.toString(productCharArray)) + "]");
                  LOG.debug("   serial number  = [" + (serialNumberCharArray == null ? null : Native.toString(serialNumberCharArray)) + "]");
                  LOG.debug("   path           = [" + hidDeviceInfo.path + "]");
                  LOG.debug("   Vendor/Product = [" + Integer.toHexString(hidDeviceInfo.vendor_id) + "|" + Integer.toHexString(hidDeviceInfo.product_id) + "]");
                  }

               if (hidDeviceInfo.vendor_id == targetDeviceDescriptor.getVendorId() && hidDeviceInfo.product_id == targetDeviceDescriptor.getProductId())
                  {
                  try
                     {
                     final boolean shouldContinue = enumerationProcessor.process(hidDeviceInfo.path);
                     if (!shouldContinue)
                        {
                        final DeviceInfo<HIDAPILibrary.hid_device> deviceInfo = new DeviceInfoImpl<HIDAPILibrary.hid_device>();
                        deviceInfo.setDeviceFilenamePath(hidDeviceInfo.path);
                        LOG.debug("HIDAPIDeviceHelper.enumerateDevices(): returing deviceInfo! [" + deviceInfo + "]");
                        return deviceInfo;
                        }
                     }
                  catch (Exception e)
                     {
                     LOG.error("Ignoring exception caught while processing hidDeviceInfo.", e);
                     }
                  }

               hidDeviceInfo = hidDeviceInfo.next;
               }
            }
         else
            {
            LOG.debug("HIDAPIDeviceHelper.enumerateDevices(): null HIDDeviceInfo returned from hid_enumerate");
            }
         }
      catch (final Exception e)
         {
         LOG.error("Exception caught while trying to enumerate devices with vendor id [" + targetDeviceDescriptor.getVendorIdAsHexString() + "] and product id [" + targetDeviceDescriptor.getProductIdAsHexString() + "].", e);
         }
      return null;
      }

   private HIDAPIDeviceHelper()
      {
      // private to prevent instantiation
      }
   }
