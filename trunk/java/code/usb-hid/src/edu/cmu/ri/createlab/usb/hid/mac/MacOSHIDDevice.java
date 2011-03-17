package edu.cmu.ri.createlab.usb.hid.mac;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Native;
import edu.cmu.ri.createlab.usb.hid.BaseHIDDevice;
import edu.cmu.ri.createlab.usb.hid.DeviceInfo;
import edu.cmu.ri.createlab.usb.hid.DeviceInfoImpl;
import edu.cmu.ri.createlab.usb.hid.HIDConnectionException;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotFoundException;
import edu.cmu.ri.createlab.usb.hid.HIDWriteStatus;
import edu.cmu.ri.createlab.util.ArrayUtils;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

// TODO: add error checking to HID calls once HIDAPI has support for the hid_error() function for Mac OS!

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MacOSHIDDevice extends BaseHIDDevice
   {
   private static final Logger LOG = Logger.getLogger(MacOSHIDDevice.class);

   private static final Lock LOCK = new ReentrantLock();
   private static final Set<String> DEVICES_IN_USE = new HashSet<String>();

   private static DeviceInfo<HIDAPILibrary.hid_device> claimAvailableDevice(final short vendorID, final short productID)
      {
      LOG.trace("MacOSHIDDevice.claimAvailableDevice(): locking...");
      LOCK.lock();  // block until condition holds

      try
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("MacOSHIDDevice.readDeviceInfo(): looking for device with vendor id [" + Integer.toHexString(vendorID) + "] and product id [" + Integer.toHexString(productID) + "]");
            }

         DeviceInfo<HIDAPILibrary.hid_device> deviceInfo = null;

         // enumerate the devices, filtering on the one we care about and ignoring the ones already in use
         HIDDeviceInfo hidDeviceInfo = HIDAPILibrary.INSTANCE.hid_enumerate(vendorID, productID);
         if (hidDeviceInfo != null)
            {
            while (hidDeviceInfo != null)
               {
               if (LOG.isDebugEnabled())
                  {
                  final char[] manufacturerCharArray = hidDeviceInfo.manufacturer_string.getPointer().getCharArray(0, 128);
                  final char[] productCharArray = hidDeviceInfo.product_string.getPointer().getCharArray(0, 128);
                  final char[] serialNumberCharArray = hidDeviceInfo.serial_number.getPointer().getCharArray(0, 128);

                  LOG.debug("MacOSHIDDevice.readDeviceInfo(): found matching device:");
                  LOG.debug("   manufacturer   = [" + Native.toString(manufacturerCharArray) + "]");
                  LOG.debug("   product        = [" + Native.toString(productCharArray) + "]");
                  LOG.debug("   serial number  = [" + Native.toString(serialNumberCharArray) + "]");
                  LOG.debug("   path           = [" + hidDeviceInfo.path + "]");
                  LOG.debug("   Vendor/Product = [" + Integer.toHexString(hidDeviceInfo.vendor_id) + "|" + Integer.toHexString(hidDeviceInfo.product_id) + "]");
                  }

               if (hidDeviceInfo.vendor_id == vendorID && hidDeviceInfo.product_id == productID)
                  {
                  if (DEVICES_IN_USE.contains(hidDeviceInfo.path))
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("MacOSHIDDevice.claimAvailableDevice(): Device with path [" + hidDeviceInfo.path + "] already in use!");
                        }
                     }
                  else
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("MacOSHIDDevice.claimAvailableDevice(): Device with path [" + hidDeviceInfo.path + "] NOT in use, so we'll use it");
                        }
                     DEVICES_IN_USE.add(hidDeviceInfo.path);   // mark the device as in use

                     deviceInfo = new DeviceInfoImpl<HIDAPILibrary.hid_device>();
                     deviceInfo.setDeviceFilenamePath(hidDeviceInfo.path);
                     LOG.debug("MacOSHIDDevice.claimAvailableDevice(): returing deviceInfo! [" + deviceInfo + "]");
                     break;
                     }
                  }

               hidDeviceInfo = hidDeviceInfo.next;
               }
            }
         else
            {
            LOG.debug("MacOSHIDDevice.readDeviceInfo(): null HIDDeviceInfo returned from hid_enumerate");
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("MacOSHIDDevice.claimAvailableDevice(): returning the deviceInfo [" + deviceInfo + "]");
            }
         return deviceInfo;
         }
      catch (final Exception e)
         {
         LOG.error("Exception caught while trying to claim a device with vendor id [" + Integer.toHexString(vendorID) + "] and product id [" + Integer.toHexString(productID) + "].  Returning null.", e);
         return null;
         }
      finally
         {
         LOG.trace("MacOSHIDDevice.claimAvailableDevice(): about to unlock!");
         LOCK.unlock();
         LOG.trace("MacOSHIDDevice.claimAvailableDevice(): done unlocking!");
         }
      }

   private static void releaseDevice(final String devicePath)
      {
      if (devicePath != null)
         {
         LOG.trace("MacOSHIDDevice.releaseDevice(): about to lock...");
         LOCK.lock();  // block until condition holds
         try
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("MacOSHIDDevice.releaseDevice(): about to release device [" + devicePath + "]");
               }
            DEVICES_IN_USE.remove(devicePath);  // mark the device as no longer in use
            if (LOG.isDebugEnabled())
               {
               LOG.debug("MacOSHIDDevice.releaseDevice(): done releasing [" + devicePath + "]");
               }
            }
         catch (final Exception e)
            {
            LOG.error("Exception caught while trying to release device [" + devicePath + "]", e);
            }
         finally
            {
            LOG.trace("MacOSHIDDevice.releaseDevice(): about to unlock!");
            LOCK.unlock();
            LOG.trace("MacOSHIDDevice.releaseDevice(): done unlocking!");
            }
         }
      }

   private DeviceInfo<HIDAPILibrary.hid_device> hidDevice = null;

   public MacOSHIDDevice(final short vendorID, final short productID)
      {
      super(vendorID, productID);
      }

   public void connect() throws HIDDeviceNotFoundException, HIDConnectionException
      {
      LOG.trace("MacOSHIDDevice.connect()");

      final DeviceInfo<HIDAPILibrary.hid_device> deviceInfo = MacOSHIDDevice.claimAvailableDevice(getVendorID(), getProductID());
      if (deviceInfo != null && deviceInfo.getDeviceFilenamePath() != null)
         {
         // Open the device
         final HIDAPILibrary.hid_device deviceHandle = HIDAPILibrary.INSTANCE.hid_open_path(deviceInfo.getDeviceFilenamePath());
         if (deviceHandle != null)
            {
            // record the device handle
            deviceInfo.setFileHandle(deviceHandle);

            // now record the input and output report max lengths
            final int inputReportMaxLength = HIDAPILibrary.INSTANCE.hid_get_max_input_report_size(deviceHandle);
            final int outputReportMaxLength = HIDAPILibrary.INSTANCE.hid_get_max_output_report_size(deviceHandle);
            deviceInfo.setInputAndOutputReportLengthInBytes(inputReportMaxLength, outputReportMaxLength);

            // set reads to non-blocking
            HIDAPILibrary.INSTANCE.hid_set_nonblocking(deviceHandle, 1);

            this.hidDevice = deviceInfo;
            }
         else
            {
            LOG.error("MacOSHIDDevice.connect(): connection failed");
            throw new HIDConnectionException("Connection to device with vendor ID [" + Integer.toHexString(getVendorID()) + "] and product ID [" + Integer.toHexString(getProductID()) + "] failed.");
            }
         }
      else
         {
         LOG.error("MacOSHIDDevice.connect(): device not found");
         throw new HIDDeviceNotFoundException("Device with vendor ID [" + Integer.toHexString(getVendorID()) + "] and product ID [" + Integer.toHexString(getProductID()) + "] not found.");
         }
      }

   public void connectExclusively() throws HIDDeviceNotFoundException, HIDConnectionException
      {
      connect();  // all connections are exclusive
      }

   public String getDeviceFilename()
      {
      if (hidDevice != null && hidDevice.getFileHandle() != null)
         {
         return hidDevice.getDeviceFilenamePath();
         }
      return null;
      }

   public byte[] read()
      {
      if (hidDevice != null &&
          hidDevice.getFileHandle() != null &&
          hidDevice.getDeviceFilenamePath() != null)
         {
         final int inputReportByteLength = hidDevice.getInputReportByteLength();
         final ByteBuffer readBuffer = ByteBuffer.allocate(inputReportByteLength);
         final int numBytesRead = HIDAPILibrary.INSTANCE.hid_read(hidDevice.getFileHandle(), readBuffer, new NativeSize(inputReportByteLength));

         if (numBytesRead > 0)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("MacOSHIDDevice.read(): Successfully read [" + numBytesRead + "] bytes!");
               final int[] dataAsInts = new int[inputReportByteLength];
               for (int i = 0; i < inputReportByteLength; i++)
                  {
                  dataAsInts[i] = ByteUtils.unsignedByteToInt(readBuffer.get(i));
                  }
               LOG.trace("MacOSHIDDevice.read(): Data read: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }
            return readBuffer.array();
            }
         else
            {
            LOG.trace("MacOSHIDDevice.read(): zero bytes read");
            }
         }
      return null;
      }

   public boolean isReportIDIncludedInReadData()
      {
      // Mac OS does not include the report ID in the read data
      return false;
      }

   public HIDWriteStatus write(final byte[] data)
      {
      if (data != null)
         {
         if (hidDevice != null &&
             hidDevice.getFileHandle() != null &&
             hidDevice.getDeviceFilenamePath() != null)
            {
            // we must add an extra byte here since Mac OS doesn't include the report byte in the returned length
            final int outputReportByteLength = hidDevice.getOutputReportByteLength() + 1;  // add one for the report ID
            final byte[] writeBuffer = new byte[outputReportByteLength];

            writeBuffer[0] = 0;  // set the report ID
            final byte theCommandId = getCommandId();
            writeBuffer[writeBuffer.length - 1] = theCommandId;  // set the request ID

            // copy the data to the write buffer
            for (int dataIndex = 0; dataIndex < data.length; dataIndex++)
               {
               final int writeBufferIndex = dataIndex + 1;
               if (writeBufferIndex < writeBuffer.length - 1)
                  {
                  writeBuffer[writeBufferIndex] = data[dataIndex];
                  }
               else
                  {
                  break;
                  }
               }

            if (LOG.isTraceEnabled())
               {
               final int[] dataAsInts = new int[writeBuffer.length];
               for (int i = 0; i < writeBuffer.length; i++)
                  {
                  dataAsInts[i] = ByteUtils.unsignedByteToInt(writeBuffer[i]);
                  }
               LOG.trace("MacOSHIDDevice.write(): Writing data: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }

            // do the write (TODO: get rid of the NativeSize object creation)
            final int bytesWritten = HIDAPILibrary.INSTANCE.hid_write(hidDevice.getFileHandle(), writeBuffer, new NativeSize(outputReportByteLength));

            if (bytesWritten > 0)
               {
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("MacOSHIDDevice.write(): Write successful, wrote [" + bytesWritten + "] bytes!");
                  }
               return new HIDWriteStatus(data.length, bytesWritten, true, ByteUtils.unsignedByteToInt(theCommandId));
               }
            else
               {
               if (LOG.isEnabledFor(Level.ERROR))
                  {
                  LOG.error("MacOSHIDDevice.write(): Write failed.  Return was [" + bytesWritten + "]");
                  }

               return new HIDWriteStatus(data.length, bytesWritten, false, ByteUtils.unsignedByteToInt(theCommandId));
               }
            }
         }
      return HIDWriteStatus.WRITE_FAILED;
      }

   public boolean disconnect()
      {
      LOG.trace("MacOSHIDDevice.disconnect()");

      if (hidDevice != null)
         {
         MacOSHIDDevice.releaseDevice(hidDevice.getDeviceFilenamePath());

         final HIDAPILibrary.hid_device fileHandle = hidDevice.getFileHandle();
         if (fileHandle != null)
            {
            HIDAPILibrary.INSTANCE.hid_close(fileHandle);

            LOG.debug("MacOSHIDDevice.disconnect(): disconnected successfully");
            return true;
            }
         else
            {
            LOG.error("MacOSHIDDevice.disconnect(): Failed to disconnect because the file handle is null");
            }
         }
      else
         {
         LOG.error("MacOSHIDDevice.disconnect(): Failed to disconnect because the DeviceInfo is null (maybe you didn't connect first?)");
         }
      return false;
      }
   }
