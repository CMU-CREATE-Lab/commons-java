package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import edu.cmu.ri.createlab.usb.hid.HIDDevice;
import edu.cmu.ri.createlab.util.ArrayUtils;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class WindowsHIDDevice implements HIDDevice
   {
   private static final Log LOG = LogFactory.getLog(WindowsHIDDevice.class);

   private static WinError getAndDisplayLastError(final String functionName) // TODO: all calls to this should handle case where last error wasn't SUCCESS
      {
      final int lastError = Native.getLastError();
      final WinError winError = WinError.findById(lastError);
      if (LOG.isTraceEnabled())
         {
         LOG.trace(functionName + "() last error: [" + lastError + "|" + winError + "]");
         }
      return winError;
      }

   private final short vendorID;
   private final short productID;
   private DeviceInfo hidDeviceInfo = null;
   private byte requestId = 0;

   public WindowsHIDDevice(final short vendorID, final short productID)
      {
      this.vendorID = vendorID;
      this.productID = productID;
      }

   private DeviceInfo readDeviceInfo()
      {
      final DeviceInfo deviceInfo = new DeviceInfo();

      // create the GUID
      final GUID guid = new GUID();

      // get the device interface GUID for HIDClass devices
      HIDLibrary.INSTANCE.HidD_GetHidGuid(guid);

      // get a handle to a device information set that contains requested device information elements
      final HDEVINFO deviceInformationSet = SetupAPILibrary.INSTANCE.SetupDiGetClassDevsA(guid,
                                                                                          null,
                                                                                          null,
                                                                                          SetupAPILibrary.Flags.DIGCF_PRESENT | SetupAPILibrary.Flags.DIGCF_DEVICEINTERFACE);

      // Create the deviceInterfaceData and set its cbSize (see the docs for SetupDiEnumDeviceInterfaces())
      final SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SP_DEVICE_INTERFACE_DATA();
      deviceInterfaceData.cbSize = deviceInterfaceData.size();

      int deviceIndex = 0;
      boolean isNoMoreItems = false;
      boolean wasMyDeviceDetected = false;
      do
         {
         // enumerate the device interfaces that are contained in a device information set
         final boolean deviceInterfaceEnumerationSetResult = SetupAPILibrary.INSTANCE.SetupDiEnumDeviceInterfaces(deviceInformationSet,
                                                                                                                  null,
                                                                                                                  guid,
                                                                                                                  deviceIndex,
                                                                                                                  deviceInterfaceData);

         // make sure the enumeration was successful
         final WinError enumDeviceInterfacesStatus = getAndDisplayLastError("SetupDiEnumDeviceInterfaces");
         if (deviceInterfaceEnumerationSetResult && enumDeviceInterfacesStatus.isSuccess())
            {
            // First call SetupDiGetDeviceInterfaceDetailA which will return the required buffer size
            // and last error will be ERROR_INSUFFICIENT_BUFFER
            final IntByReference length = new IntByReference();
            final boolean deviceInterfaceDetailResult1 = SetupAPILibrary.INSTANCE.SetupDiGetDeviceInterfaceDetailW(deviceInformationSet,
                                                                                                                   deviceInterfaceData,
                                                                                                                   null,   // deviceInterfaceDetailData is null
                                                                                                                   0,      // must be 0 since deviceInterfaceDetailData is null
                                                                                                                   length,
                                                                                                                   null);

            getAndDisplayLastError("SetupDiGetDeviceInterfaceDetail");
            if (LOG.isTraceEnabled())
               {
               LOG.trace("SetupDiGetDeviceInterfaceDetail result " + deviceInterfaceDetailResult1);
               LOG.trace("SetupDiGetDeviceInterfaceDetail length " + length.getValue());
               }

            // Now create a SP_DEVICE_INTERFACE_DETAIL_DATA and set the size of the device path to the length obtained above
            final SP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData = new SP_DEVICE_INTERFACE_DETAIL_DATA();
            deviceInterfaceDetailData.cbSize = new SP_DEVICE_INTERFACE_DETAIL_DATA().size();
            deviceInterfaceDetailData.devicePath = new char[length.getValue()];

            // call the function again to get the interface details
            final boolean deviceInterfaceDetailResult2 = SetupAPILibrary.INSTANCE.SetupDiGetDeviceInterfaceDetailW(deviceInformationSet,
                                                                                                                   deviceInterfaceData,
                                                                                                                   deviceInterfaceDetailData,
                                                                                                                   length.getValue(),
                                                                                                                   null,
                                                                                                                   null);

            getAndDisplayLastError("SetupDiGetDeviceInterfaceDetail");
            if (LOG.isTraceEnabled())
               {
               LOG.trace("SetupDiGetDeviceInterfaceDetail result " + deviceInterfaceDetailResult2);
               }

            // get the devicePath
            final String devicePath = Native.toString(deviceInterfaceDetailData.devicePath);
            if (LOG.isTraceEnabled())
               {
               LOG.trace("deviceInterfaceDetailData.devicePath: [" + devicePath + "]");
               }

            // Create the file to get a file handle.  Open the device for read only at this point since we're just
            // interested in getting device info
            final PointerByReference fileHandle = Kernel32Library.INSTANCE.CreateFileA(devicePath,
                                                                                       Kernel32Library.DesiredAccess.GENERIC_READ,
                                                                                       Kernel32Library.SharingMode.FILE_SHARE_READ | Kernel32Library.SharingMode.FILE_SHARE_WRITE,
                                                                                       null,
                                                                                       Kernel32Library.CreationDisposition.OPEN_EXISTING,
                                                                                       Kernel32Library.FlagsAndAttributes.FILE_ATTRIBUTE_NORMAL,
                                                                                       null);

            getAndDisplayLastError("CreateFile");
            if (LOG.isTraceEnabled())
               {
               LOG.trace("CreateFile fileHandle " + fileHandle);
               }

            deviceInfo.setFileHandle(fileHandle);

            // read the HID attributes in order to get the devices vendor and product ID
            final HIDD_ATTRIBUTES hidAttributes = new HIDD_ATTRIBUTES();
            hidAttributes.size = hidAttributes.size();
            final boolean getAttributesResult = HIDLibrary.INSTANCE.HidD_GetAttributes(fileHandle, hidAttributes);
            final WinError getAttributesStatus = getAndDisplayLastError("HidD_GetAttributes");
            if (LOG.isTraceEnabled())
               {
               LOG.trace("HidD_GetAttributes result " + getAttributesResult);
               LOG.trace("hidAttributes.vendorId " + Integer.toHexString((int)hidAttributes.vendorId));
               LOG.trace("hidAttributes.productId " + Integer.toHexString((int)hidAttributes.productId));
               }

            // See whether we found the target device
            if (getAttributesResult &&
                getAttributesStatus.isSuccess() &&
                hidAttributes.vendorId == this.vendorID &&
                hidAttributes.productId == this.productID)
               {
               LOG.trace("Device detected!");
               deviceInfo.setDeviceFilenamePath(devicePath);
               wasMyDeviceDetected = true;

               // call HidD_GetPreparsedData and HidP_GetCaps to get the HID device capabilities
               final IntByReference hidPreparsedData = new IntByReference();
               final boolean getPreparsedDataSuccess = HIDLibrary.INSTANCE.HidD_GetPreparsedData(fileHandle, hidPreparsedData);
               getAndDisplayLastError("HidD_GetPreparsedData");
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("getPreparsedDataSuccess = [" + getPreparsedDataSuccess + "]");
                  }

               final HIDP_CAPS hidCapabilities = new HIDP_CAPS();
               final int getCapsResult = HIDLibrary.INSTANCE.HidP_GetCaps(hidPreparsedData.getValue(), hidCapabilities);
               getAndDisplayLastError("HidP_GetCaps");
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("HidP_GetCaps result: " + getCapsResult);
                  LOG.trace("hidCapabilities: " + hidCapabilities);
                  }

               // store the device capabilities
               deviceInfo.setInputAndOutputReportLengthInBytes(hidCapabilities.inputReportByteLength,
                                                               hidCapabilities.outputReportByteLength);

               // free the preparsed data
               final boolean freePreparsedDataSuccess = HIDLibrary.INSTANCE.HidD_FreePreparsedData(hidPreparsedData);
               getAndDisplayLastError("HidD_FreePreparsedData");
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("freePreparsedDataSuccess = [" + freePreparsedDataSuccess + "]");
                  }
               }

            // disconnect from the device
            disconnect(deviceInfo);
            }
         else
            {
            isNoMoreItems = (WinError.ERROR_NO_MORE_ITEMS.equals(enumDeviceInterfacesStatus));
            }

         deviceIndex++;
         }
      while ((!isNoMoreItems) && (!wasMyDeviceDetected));

      // Free the memory reserved for deviceInformationSet by SetupDiGetClassDevsA.
      final boolean wasDestructionSuccessful = SetupAPILibrary.INSTANCE.SetupDiDestroyDeviceInfoList(deviceInformationSet);
      if (LOG.isTraceEnabled())
         {
         LOG.trace("Result of deleting the deviceInformationSet: " + wasDestructionSuccessful);
         }
      getAndDisplayLastError("SetupDiDestroyDeviceInfoList");

      return deviceInfo;
      }

   public boolean connect()
      {
      LOG.debug("WindowsHIDDevice.connect()");

      final DeviceInfo deviceInfo = readDeviceInfo();
      if (deviceInfo != null &&
          deviceInfo.getFileHandle() != null &&
          deviceInfo.getDeviceFilenamePath() != null)
         {
         // connect to the device
         final PointerByReference fileHandle = Kernel32Library.INSTANCE.CreateFileA(deviceInfo.getDeviceFilenamePath(),
                                                                                    Kernel32Library.DesiredAccess.GENERIC_READ | Kernel32Library.DesiredAccess.GENERIC_WRITE,
                                                                                    Kernel32Library.SharingMode.FILE_SHARE_READ | Kernel32Library.SharingMode.FILE_SHARE_WRITE,
                                                                                    null,
                                                                                    Kernel32Library.CreationDisposition.OPEN_EXISTING,
                                                                                    Kernel32Library.FlagsAndAttributes.FILE_ATTRIBUTE_NORMAL | Kernel32Library.FlagsAndAttributes.FILE_FLAG_NO_BUFFERING | Kernel32Library.FlagsAndAttributes.FILE_FLAG_WRITE_THROUGH,
                                                                                    null);

         final WinError createFileStatus = getAndDisplayLastError("CreateFile");
         if (createFileStatus.isSuccess())
            {
            LOG.debug("WindowsHIDDevice.connect(): connection successful");
            deviceInfo.setFileHandle(fileHandle);
            this.hidDeviceInfo = deviceInfo;
            return true;
            }
         else
            {
            LOG.debug("WindowsHIDDevice.connect(): connection failed");
            }
         }
      else
         {
         LOG.debug("WindowsHIDDevice.connect(): device is not plugged in");
         }

      this.hidDeviceInfo = null;
      return false;
      }

   public byte[] read()
      {
      if (hidDeviceInfo != null &&
          hidDeviceInfo.getFileHandle() != null &&
          hidDeviceInfo.getDeviceFilenamePath() != null)
         {
         final byte[] readBuffer = new byte[hidDeviceInfo.getInputReportByteLength()];
         final IntByReference bytesRead = new IntByReference();
         final boolean readFileResult = Kernel32Library.INSTANCE.ReadFile(hidDeviceInfo.getFileHandle(),
                                                                          readBuffer,
                                                                          readBuffer.length,
                                                                          bytesRead,
                                                                          null);
         final WinError readStatus = getAndDisplayLastError("ReadFile");
         if (readFileResult && readStatus.isSuccess())
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("WindowsHIDDevice.read(): Successfully read [" + bytesRead.getValue() + "] bytes!");
               final int[] dataAsInts = new int[readBuffer.length];
               for (int i = 0; i < readBuffer.length; i++)
                  {
                  dataAsInts[i] = ByteUtils.unsignedByteToInt(readBuffer[i]);
                  }
               LOG.debug("WindowsHIDDevice.read(): Data read: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }
            return readBuffer;
            }
         else
            {
            LOG.error("WindowsHIDDevice.read(): Read failed.  Return was [" + readFileResult + "] and last error was [" + readStatus + "]");
            }
         }

      return null;
      }

   public int write(final byte[] data)
      {
      if (data != null &&
          hidDeviceInfo != null &&
          hidDeviceInfo.getFileHandle() != null &&
          hidDeviceInfo.getDeviceFilenamePath() != null)
         {
         final byte[] writeBuffer = new byte[hidDeviceInfo.getOutputReportByteLength()];

         writeBuffer[0] = 0;  // set the report ID
         writeBuffer[writeBuffer.length - 1] = getRequestId();  // set the request ID

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

         if (LOG.isDebugEnabled())
            {
            final int[] dataAsInts = new int[writeBuffer.length];
            for (int i = 0; i < writeBuffer.length; i++)
               {
               dataAsInts[i] = ByteUtils.unsignedByteToInt(writeBuffer[i]);
               }
            LOG.debug("WindowsHIDDevice.write(): Writing data: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
            }

         final IntByReference bytesWritten = new IntByReference();
         final boolean writeFileResult = Kernel32Library.INSTANCE.WriteFile(hidDeviceInfo.getFileHandle(),
                                                                            writeBuffer,
                                                                            writeBuffer.length,
                                                                            bytesWritten,
                                                                            null);
         final WinError writeStatus = getAndDisplayLastError("WriteFile");
         if (writeFileResult && writeStatus.isSuccess())
            {
            LOG.trace("WindowsHIDDevice.write(): Write successful, wrote [" + bytesWritten.getValue() + "] bytes!");
            return bytesWritten.getValue();
            }
         else
            {
            LOG.error("WindowsHIDDevice.write(): Write failed.  Return was [" + writeFileResult + "] and last error was [" + writeStatus + "]");
            }
         }
      else
         {
         LOG.error("WindowsHIDDevice.write(): failed to write since we don't appear to have a connection established");
         }

      return 0;
      }

   private byte getRequestId()
      {
      requestId++;
      if (requestId > 255)
         {
         requestId = 0;
         }
      return requestId;
      }

   public boolean disconnect()
      {
      return disconnect(hidDeviceInfo);
      }

   private boolean disconnect(final DeviceInfo deviceInfo)
      {
      LOG.debug("WindowsHIDDevice.disconnect()");
      if (deviceInfo != null)
         {
         final PointerByReference fileHandle = deviceInfo.getFileHandle();
         if (fileHandle != null)
            {
            final boolean closeHandleSuccess = Kernel32Library.INSTANCE.CloseHandle(fileHandle);
            final WinError closeHandleStatus = getAndDisplayLastError("CloseHandle");
            if (closeHandleSuccess && closeHandleStatus.isSuccess())
               {
               LOG.debug("WindowsHIDDevice.disconnect(): disconnected successfully");
               return true;
               }
            else
               {
               LOG.error("WindowsHIDDevice.disconnect(): Failed to disconnect.  Return was [" + closeHandleSuccess + "] and last error was [" + closeHandleStatus + "]");
               }
            }
         else
            {
            LOG.error("WindowsHIDDevice.disconnect(): Failed to disconnect because the file handle is null");
            }
         }
      else
         {
         LOG.error("WindowsHIDDevice.disconnect(): Failed to disconnect because the DeviceInfo is null (maybe you didn't connect first?)");
         }

      return false;
      }
   }
