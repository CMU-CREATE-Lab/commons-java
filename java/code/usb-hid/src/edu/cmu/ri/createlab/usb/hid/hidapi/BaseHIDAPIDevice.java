package edu.cmu.ri.createlab.usb.hid.hidapi;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import edu.cmu.ri.createlab.usb.hid.BaseHIDDevice;
import edu.cmu.ri.createlab.usb.hid.DeviceInfo;
import edu.cmu.ri.createlab.usb.hid.DeviceInfoImpl;
import edu.cmu.ri.createlab.usb.hid.HIDConnectionException;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotFoundException;
import edu.cmu.ri.createlab.usb.hid.HIDWriteStatus;
import edu.cmu.ri.createlab.util.ArrayUtils;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

// TODO: add error checking to HID calls once HIDAPI has support for the hid_error() function!

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseHIDAPIDevice extends BaseHIDDevice
   {
   private static final Logger LOG = Logger.getLogger(BaseHIDAPIDevice.class);

   private static final Lock LOCK = new ReentrantLock();
   private static final Set<String> DEVICES_IN_USE = new HashSet<String>();

   private static DeviceInfo<HIDAPILibrary.hid_device> claimAvailableDevice(final HIDDeviceDescriptor hidDeviceDescriptor)
      {
      LOG.trace("BaseHIDAPIDevice.claimAvailableDevice(): locking...");
      LOCK.lock();  // block until condition holds

      try
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("BaseHIDAPIDevice.readDeviceInfo(): looking for device with vendor id [" + hidDeviceDescriptor.getVendorIdAsHexString() + "] and product id [" + hidDeviceDescriptor.getProductIdAsHexString() + "]");
            }

         DeviceInfo<HIDAPILibrary.hid_device> deviceInfo = null;

         // enumerate the devices, filtering on the one we care about and ignoring the ones already in use
         HIDDeviceInfo hidDeviceInfo = HIDAPILibrary.INSTANCE.hid_enumerate(hidDeviceDescriptor.getVendorId(), hidDeviceDescriptor.getProductId());
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

                  LOG.debug("BaseHIDAPIDevice.readDeviceInfo(): found matching device:");
                  LOG.debug("   manufacturer   = [" + (manufacturerCharArray == null ? null : Native.toString(manufacturerCharArray)) + "]");
                  LOG.debug("   product        = [" + (productCharArray == null ? null : Native.toString(productCharArray)) + "]");
                  LOG.debug("   serial number  = [" + (serialNumberCharArray == null ? null : Native.toString(serialNumberCharArray)) + "]");
                  LOG.debug("   path           = [" + hidDeviceInfo.path + "]");
                  LOG.debug("   Vendor/Product = [" + Integer.toHexString(hidDeviceInfo.vendor_id) + "|" + Integer.toHexString(hidDeviceInfo.product_id) + "]");
                  }

               if (hidDeviceInfo.vendor_id == hidDeviceDescriptor.getVendorId() && hidDeviceInfo.product_id == hidDeviceDescriptor.getProductId())
                  {
                  if (DEVICES_IN_USE.contains(hidDeviceInfo.path))
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("BaseHIDAPIDevice.claimAvailableDevice(): Device with path [" + hidDeviceInfo.path + "] already in use!");
                        }
                     }
                  else
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("BaseHIDAPIDevice.claimAvailableDevice(): Device with path [" + hidDeviceInfo.path + "] NOT in use, so we'll use it");
                        }
                     DEVICES_IN_USE.add(hidDeviceInfo.path);   // mark the device as in use

                     deviceInfo = new DeviceInfoImpl<HIDAPILibrary.hid_device>();
                     deviceInfo.setDeviceFilenamePath(hidDeviceInfo.path);
                     LOG.debug("BaseHIDAPIDevice.claimAvailableDevice(): returing deviceInfo! [" + deviceInfo + "]");
                     break;
                     }
                  }

               hidDeviceInfo = hidDeviceInfo.next;
               }
            }
         else
            {
            LOG.debug("BaseHIDAPIDevice.readDeviceInfo(): null HIDDeviceInfo returned from hid_enumerate");
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("BaseHIDAPIDevice.claimAvailableDevice(): returning the deviceInfo [" + deviceInfo + "]");
            }
         return deviceInfo;
         }
      catch (final Exception e)
         {
         LOG.error("Exception caught while trying to claim a device with vendor id [" + hidDeviceDescriptor.getVendorIdAsHexString() + "] and product id [" + hidDeviceDescriptor.getProductIdAsHexString() + "].  Returning null.", e);
         return null;
         }
      finally
         {
         LOG.trace("BaseHIDAPIDevice.claimAvailableDevice(): about to unlock!");
         LOCK.unlock();
         LOG.trace("BaseHIDAPIDevice.claimAvailableDevice(): done unlocking!");
         }
      }

   private static void releaseDevice(final String devicePath)
      {
      if (devicePath != null)
         {
         LOG.trace("BaseHIDAPIDevice.releaseDevice(): about to lock...");
         LOCK.lock();  // block until condition holds
         try
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("BaseHIDAPIDevice.releaseDevice(): about to release device [" + devicePath + "]");
               }
            DEVICES_IN_USE.remove(devicePath);  // mark the device as no longer in use
            if (LOG.isDebugEnabled())
               {
               LOG.debug("BaseHIDAPIDevice.releaseDevice(): done releasing [" + devicePath + "]");
               }
            }
         catch (final Exception e)
            {
            LOG.error("Exception caught while trying to release device [" + devicePath + "]", e);
            }
         finally
            {
            LOG.trace("BaseHIDAPIDevice.releaseDevice(): about to unlock!");
            LOCK.unlock();
            LOG.trace("BaseHIDAPIDevice.releaseDevice(): done unlocking!");
            }
         }
      }

   private DeviceInfo<HIDAPILibrary.hid_device> hidDevice = null;

   private final int inputReportByteLength;
   private final int outputReportByteLength;
   private final NativeSize inputReportByteLengthAsNativeSize;
   private final NativeSize outputReportByteLengthAsNativeSize;

   protected BaseHIDAPIDevice(final HIDDeviceDescriptor hidDeviceDescriptor)
      {
      super(hidDeviceDescriptor);

      // precalculate and cache these values for speed
      this.inputReportByteLength = getHidDeviceDescriptor().getInputReportByteLength() - (isReportIDIncludedInReadData() ? 0 : 1);
      this.outputReportByteLength = hidDeviceDescriptor.getOutputReportByteLength();
      inputReportByteLengthAsNativeSize = new NativeSize(inputReportByteLength);
      outputReportByteLengthAsNativeSize = new NativeSize(outputReportByteLength);
      }

   public final void connect() throws HIDDeviceNotFoundException, HIDConnectionException
      {
      LOG.trace("BaseHIDAPIDevice.connect()");

      final DeviceInfo<HIDAPILibrary.hid_device> deviceInfo = BaseHIDAPIDevice.claimAvailableDevice(getHidDeviceDescriptor());
      if (deviceInfo != null && deviceInfo.getDeviceFilenamePath() != null)
         {
         // Open the device
         final HIDAPILibrary.hid_device deviceHandle = HIDAPILibrary.INSTANCE.hid_open_path(deviceInfo.getDeviceFilenamePath());
         if (deviceHandle != null)
            {
            // record the device handle
            deviceInfo.setFileHandle(deviceHandle);

            // set reads to non-blocking
            HIDAPILibrary.INSTANCE.hid_set_nonblocking(deviceHandle, 1);

            this.hidDevice = deviceInfo;
            }
         else
            {
            LOG.error("BaseHIDAPIDevice.connect(): connection failed");
            throw new HIDConnectionException("Connection to device with vendor ID [" + Integer.toHexString(getVendorID()) + "] and product ID [" + Integer.toHexString(getProductID()) + "] failed.");
            }
         }
      else
         {
         LOG.error("BaseHIDAPIDevice.connect(): device not found");
         throw new HIDDeviceNotFoundException("Device with vendor ID [" + Integer.toHexString(getVendorID()) + "] and product ID [" + Integer.toHexString(getProductID()) + "] not found.");
         }
      }

   public final void connectExclusively() throws HIDDeviceNotFoundException, HIDConnectionException
      {
      connect();  // all connections are exclusive
      }

   public final String getDeviceFilename()
      {
      if (hidDevice != null && hidDevice.getFileHandle() != null)
         {
         return hidDevice.getDeviceFilenamePath();
         }
      return null;
      }

   public final byte[] read()
      {
      if (hidDevice != null &&
          hidDevice.getFileHandle() != null &&
          hidDevice.getDeviceFilenamePath() != null)
         {
         final ByteBuffer readBuffer = ByteBuffer.allocate(inputReportByteLength);
         final int numBytesRead = HIDAPILibrary.INSTANCE.hid_read(hidDevice.getFileHandle(), readBuffer, inputReportByteLengthAsNativeSize);

         if (numBytesRead > 0)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("BaseHIDAPIDevice.read(): Successfully read [" + numBytesRead + "] bytes!");
               final int[] dataAsInts = new int[inputReportByteLength];
               for (int i = 0; i < inputReportByteLength; i++)
                  {
                  dataAsInts[i] = ByteUtils.unsignedByteToInt(readBuffer.get(i));
                  }
               LOG.trace("BaseHIDAPIDevice.read(): Data read: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }
            return readBuffer.array();
            }
         else
            {
            LOG.trace("BaseHIDAPIDevice.read(): zero bytes read");
            }
         }
      return null;
      }

   public final HIDWriteStatus write(final byte[] data)
      {
      if (data != null)
         {
         if (hidDevice != null &&
             hidDevice.getFileHandle() != null &&
             hidDevice.getDeviceFilenamePath() != null)
            {
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
               LOG.trace("BaseHIDAPIDevice.write(): Writing data: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }

            final int bytesWritten = HIDAPILibrary.INSTANCE.hid_write(hidDevice.getFileHandle(), writeBuffer, outputReportByteLengthAsNativeSize);

            if (bytesWritten > 0)
               {
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("BaseHIDAPIDevice.write(): Write successful, wrote [" + bytesWritten + "] bytes!");
                  }
               return new HIDWriteStatus(data.length, bytesWritten, true, ByteUtils.unsignedByteToInt(theCommandId));
               }
            else
               {
               if (LOG.isEnabledFor(Level.ERROR))
                  {
                  LOG.error("BaseHIDAPIDevice.write(): Write failed.  Return was [" + bytesWritten + "]");
                  }

               return new HIDWriteStatus(data.length, bytesWritten, false, ByteUtils.unsignedByteToInt(theCommandId));
               }
            }
         }
      return HIDWriteStatus.WRITE_FAILED;
      }

   public final boolean disconnect()
      {
      LOG.trace("BaseHIDAPIDevice.disconnect()");

      if (hidDevice != null)
         {
         BaseHIDAPIDevice.releaseDevice(hidDevice.getDeviceFilenamePath());

         final HIDAPILibrary.hid_device fileHandle = hidDevice.getFileHandle();
         if (fileHandle != null)
            {
            HIDAPILibrary.INSTANCE.hid_close(fileHandle);

            LOG.debug("BaseHIDAPIDevice.disconnect(): disconnected successfully");
            return true;
            }
         else
            {
            LOG.error("BaseHIDAPIDevice.disconnect(): Failed to disconnect because the file handle is null");
            }
         }
      else
         {
         LOG.error("BaseHIDAPIDevice.disconnect(): Failed to disconnect because the DeviceInfo is null (maybe you didn't connect first?)");
         }
      return false;
      }
   }
