package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import edu.cmu.ri.createlab.usb.hid.HIDConnectionException;
import edu.cmu.ri.createlab.usb.hid.HIDDevice;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceFailureException;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotConnectedException;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotFoundException;
import edu.cmu.ri.createlab.usb.hid.HIDWriteStatus;
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
   private static final boolean IS_64_BIT_OS;

   static
      {
      IS_64_BIT_OS = "64".equals(System.getProperty("sun.arch.data.model", "32"));
      if (LOG.isTraceEnabled())
         {
         LOG.debug("WindowsHIDDevice.static intializer(): IS_64_BIT_OS = [" + IS_64_BIT_OS + "]");
         }
      }

   private static WinError getLastError(final String functionName)
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
   private byte commandId = 0;

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
         final WinError enumDeviceInterfacesStatus = getLastError("SetupDiEnumDeviceInterfaces");
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

            getLastError("SetupDiGetDeviceInterfaceDetail");
            if (LOG.isTraceEnabled())
               {
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): SetupDiGetDeviceInterfaceDetail result " + deviceInterfaceDetailResult1);
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): SetupDiGetDeviceInterfaceDetail length " + length.getValue());
               }

            // Now create a SP_DEVICE_INTERFACE_DETAIL_DATA and set the size of the device path to the length obtained above
            final SP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData = new SP_DEVICE_INTERFACE_DETAIL_DATA();
            final int defaultDeviceInterfaceDetailDataSize = new SP_DEVICE_INTERFACE_DETAIL_DATA().size();

            if (LOG.isTraceEnabled())
               {
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): deviceInterfaceDetailData.cbSize is currently [" + deviceInterfaceDetailData.cbSize + "]");
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): default deviceInterfaceDetailData.cbSize is [" + defaultDeviceInterfaceDetailDataSize + "]");
               }

            // Set the size of the fixed part of struct.  We need to do this because "...SetupAPI packs the structs to 8
            // bytes on 64 bit, so this needs to be rounded up to the nearest 8 on 64 bit (hence it is 5 on 32, 8 on 64)"
            // See:  http://codingforums.com/showpost.php?p=776431&postcount=4)
            // TODO find solution to this rather than workaround
            if (IS_64_BIT_OS)
               {
               deviceInterfaceDetailData.cbSize = 4 + 4;
               }
            else
               {
               deviceInterfaceDetailData.cbSize = defaultDeviceInterfaceDetailDataSize;
               }

            if (LOG.isTraceEnabled())
               {
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): deviceInterfaceDetailData.cbSize is now [" + deviceInterfaceDetailData.cbSize + "]");
               }

            deviceInterfaceDetailData.devicePath = new char[length.getValue()];

            // call the function again to get the interface details
            final boolean deviceInterfaceDetailResult2 = SetupAPILibrary.INSTANCE.SetupDiGetDeviceInterfaceDetailW(deviceInformationSet,
                                                                                                                   deviceInterfaceData,
                                                                                                                   deviceInterfaceDetailData,
                                                                                                                   length.getValue(),
                                                                                                                   null,
                                                                                                                   null);

            getLastError("SetupDiGetDeviceInterfaceDetail");
            if (LOG.isTraceEnabled())
               {
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): SetupDiGetDeviceInterfaceDetail result " + deviceInterfaceDetailResult2);
               }

            // get the devicePath
            final String devicePath = Native.toString(deviceInterfaceDetailData.devicePath);
            if (LOG.isTraceEnabled())
               {
               LOG.trace("WindowsHIDDevice.readDeviceInfo(): deviceInterfaceDetailData.devicePath: [" + devicePath + "]");
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

            final WinError createFileStatus = getLastError("CreateFile");
            if (createFileStatus.isSuccess())
               {
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("WindowsHIDDevice.readDeviceInfo(): CreateFile fileHandle " + fileHandle);
                  }

               deviceInfo.setFileHandle(fileHandle);

               // read the HID attributes in order to get the devices vendor and product ID
               final HIDD_ATTRIBUTES hidAttributes = new HIDD_ATTRIBUTES();
               hidAttributes.size = hidAttributes.size();
               final boolean getAttributesResult = HIDLibrary.INSTANCE.HidD_GetAttributes(fileHandle, hidAttributes);
               final WinError getAttributesStatus = getLastError("HidD_GetAttributes");
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("WindowsHIDDevice.readDeviceInfo(): HidD_GetAttributes result " + getAttributesResult);
                  LOG.trace("WindowsHIDDevice.readDeviceInfo(): hidAttributes.vendorId " + Integer.toHexString((int)hidAttributes.vendorId));
                  LOG.trace("WindowsHIDDevice.readDeviceInfo(): hidAttributes.productId " + Integer.toHexString((int)hidAttributes.productId));
                  }

               // See whether we found the target device
               if (getAttributesResult &&
                   getAttributesStatus.isSuccess() &&
                   hidAttributes.vendorId == this.vendorID &&
                   hidAttributes.productId == this.productID)
                  {
                  LOG.trace("WindowsHIDDevice.readDeviceInfo(): Device detected!");
                  deviceInfo.setDeviceFilenamePath(devicePath);
                  wasMyDeviceDetected = true;

                  // call HidD_GetPreparsedData and HidP_GetCaps to get the HID device capabilities
                  final IntByReference hidPreparsedData = new IntByReference();
                  final boolean getPreparsedDataSuccess = HIDLibrary.INSTANCE.HidD_GetPreparsedData(fileHandle, hidPreparsedData);
                  final WinError getPreparsedDataStatus = getLastError("HidD_GetPreparsedData");
                  if (getPreparsedDataSuccess && getPreparsedDataStatus.isSuccess())
                     {
                     if (LOG.isTraceEnabled())
                        {
                        LOG.trace("WindowsHIDDevice.readDeviceInfo(): getPreparsedDataSuccess = [" + getPreparsedDataSuccess + "]");
                        }

                     final HIDP_CAPS hidCapabilities = new HIDP_CAPS();
                     final int getCapsResult = HIDLibrary.INSTANCE.HidP_GetCaps(hidPreparsedData.getValue(), hidCapabilities);
                     final WinError getCapsStatus = getLastError("HidP_GetCaps");
                     if (getCapsStatus.isSuccess())
                        {
                        if (LOG.isTraceEnabled())
                           {
                           LOG.trace("WindowsHIDDevice.readDeviceInfo(): HidP_GetCaps result: " + getCapsResult);
                           LOG.trace("WindowsHIDDevice.readDeviceInfo(): hidCapabilities: " + hidCapabilities);
                           }

                        // store the device capabilities
                        deviceInfo.setInputAndOutputReportLengthInBytes(hidCapabilities.inputReportByteLength,
                                                                        hidCapabilities.outputReportByteLength);
                        }
                     else
                        {
                        LOG.error("WindowsHIDDevice.readDeviceInfo(): Failed to read device capabilities");
                        }
                     }
                  else
                     {
                     LOG.error("WindowsHIDDevice.readDeviceInfo(): Failed to read preparsed data");
                     }

                  // free the preparsed data
                  final boolean freePreparsedDataSuccess = HIDLibrary.INSTANCE.HidD_FreePreparsedData(hidPreparsedData);
                  getLastError("HidD_FreePreparsedData");
                  if (LOG.isTraceEnabled())
                     {
                     LOG.trace("WindowsHIDDevice.readDeviceInfo(): freePreparsedDataSuccess = [" + freePreparsedDataSuccess + "]");
                     }
                  }
               else
                  {
                  LOG.error("WindowsHIDDevice.readDeviceInfo(): Failed to read device attributes");
                  }
               }
            else
               {
               if (LOG.isErrorEnabled())
                  {
                  LOG.error("WindowsHIDDevice.readDeviceInfo(): CreateFile failed (" + createFileStatus + "), skipping this device");
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
         LOG.trace("WindowsHIDDevice.readDeviceInfo(): Result of deleting the deviceInformationSet: " + wasDestructionSuccessful);
         }
      getLastError("SetupDiDestroyDeviceInfoList");

      return deviceInfo;
      }

   public void connect() throws HIDDeviceNotFoundException, HIDConnectionException
      {
      connect(Kernel32Library.SharingMode.FILE_SHARE_READ | Kernel32Library.SharingMode.FILE_SHARE_WRITE);
      }

   public void connectExclusively() throws HIDDeviceNotFoundException, HIDConnectionException
      {
      connect(Kernel32Library.SharingMode.FILE_SHARE_NONE);
      }

   private void connect(final int sharingMode) throws HIDDeviceNotFoundException, HIDConnectionException
      {
      LOG.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ size of NativeLong is [" + NativeLong.SIZE + "]");
      if (LOG.isTraceEnabled())
         {
         LOG.trace("WindowsHIDDevice.connect(" + sharingMode + ")");
         }

      final DeviceInfo deviceInfo = readDeviceInfo();
      if (deviceInfo != null &&
          deviceInfo.getFileHandle() != null &&
          deviceInfo.getDeviceFilenamePath() != null)
         {
         // connect to the device
         final PointerByReference fileHandle = Kernel32Library.INSTANCE.CreateFileA(deviceInfo.getDeviceFilenamePath(),
                                                                                    Kernel32Library.DesiredAccess.GENERIC_READ | Kernel32Library.DesiredAccess.GENERIC_WRITE,
                                                                                    sharingMode,
                                                                                    null,
                                                                                    Kernel32Library.CreationDisposition.OPEN_EXISTING,
                                                                                    Kernel32Library.FlagsAndAttributes.FILE_ATTRIBUTE_NORMAL | Kernel32Library.FlagsAndAttributes.FILE_FLAG_NO_BUFFERING | Kernel32Library.FlagsAndAttributes.FILE_FLAG_WRITE_THROUGH,
                                                                                    null);

         final WinError createFileStatus = getLastError("CreateFile");
         if (createFileStatus.isSuccess())
            {
            LOG.debug("WindowsHIDDevice.connect(): connection successful");
            deviceInfo.setFileHandle(fileHandle);
            this.hidDeviceInfo = deviceInfo;
            }
         else
            {
            LOG.error("WindowsHIDDevice.connect(): connection failed");
            throw new HIDConnectionException("Connection to device with vendor ID [" + vendorID + "] and product ID [" + productID + "] failed (" + createFileStatus + ").");
            }
         }
      else
         {
         LOG.error("WindowsHIDDevice.connect(): device not found");
         throw new HIDDeviceNotFoundException("Device with vendor ID [" + vendorID + "] and product ID [" + productID + "] not found.");
         }
      }

   public String getDeviceFilename()
      {
      if (hidDeviceInfo != null && hidDeviceInfo.getFileHandle() != null)
         {
         return hidDeviceInfo.getDeviceFilenamePath();
         }
      return null;
      }

   public byte[] read() throws HIDDeviceNotConnectedException, HIDDeviceFailureException
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
         final WinError readStatus = getLastError("ReadFile");
         if (readFileResult && readStatus.isSuccess())
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("WindowsHIDDevice.read(): Successfully read [" + bytesRead.getValue() + "] bytes!");
               final int[] dataAsInts = new int[readBuffer.length];
               for (int i = 0; i < readBuffer.length; i++)
                  {
                  dataAsInts[i] = ByteUtils.unsignedByteToInt(readBuffer[i]);
                  }
               LOG.trace("WindowsHIDDevice.read(): Data read: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }
            return readBuffer;
            }
         else
            {
            LOG.error("WindowsHIDDevice.read(): Read failed.  Return was [" + readFileResult + "] and last error was [" + readStatus + "]");
            if (WinError.ERROR_DEVICE_NOT_CONNECTED.equals(readStatus))
               {
               if (LOG.isInfoEnabled())
                  {
                  LOG.info("WindowsHIDDevice.read(): Throwing an HIDDeviceNotConnectedException since the read error was [" + WinError.ERROR_DEVICE_NOT_CONNECTED + "]");
                  }
               throw new HIDDeviceNotConnectedException("Read failed because the device is not connected (" + readStatus + ")");
               }
            else if (WinError.ERROR_GENERAL_FAILURE.equals(readStatus))
               {
               if (LOG.isInfoEnabled())
                  {
                  LOG.info("WindowsHIDDevice.read(): Throwing an HIDDeviceFailureException since the read error was [" + WinError.ERROR_GENERAL_FAILURE + "]");
                  }
               throw new HIDDeviceFailureException("Read failed because the device is not functioning (" + readStatus + ")");
               }
            }
         }

      return null;
      }

   public HIDWriteStatus write(final byte[] data) throws HIDDeviceNotConnectedException, HIDDeviceFailureException
      {
      if (data != null)
         {
         if (hidDeviceInfo != null &&
             hidDeviceInfo.getFileHandle() != null &&
             hidDeviceInfo.getDeviceFilenamePath() != null)
            {
            final byte[] writeBuffer = new byte[hidDeviceInfo.getOutputReportByteLength()];

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
               LOG.trace("WindowsHIDDevice.write(): Writing data: [" + ArrayUtils.arrayToString(dataAsInts) + "]");
               }

            final IntByReference bytesWritten = new IntByReference();
            final boolean writeFileResult = Kernel32Library.INSTANCE.WriteFile(hidDeviceInfo.getFileHandle(),
                                                                               writeBuffer,
                                                                               writeBuffer.length,
                                                                               bytesWritten,
                                                                               null);
            final WinError writeStatus = getLastError("WriteFile");
            if (writeFileResult && writeStatus.isSuccess())
               {
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("WindowsHIDDevice.write(): Write successful, wrote [" + bytesWritten.getValue() + "] bytes!");
                  }
               return new HIDWriteStatus(data.length, bytesWritten.getValue(), true, ByteUtils.unsignedByteToInt(theCommandId));
               }
            else
               {
               if (LOG.isErrorEnabled())
                  {
                  LOG.error("WindowsHIDDevice.write(): Write failed.  Return was [" + writeFileResult + "] and last error was [" + writeStatus + "]");
                  }

               final HIDWriteStatus hidWriteStatus = new HIDWriteStatus(data.length, bytesWritten.getValue(), false, ByteUtils.unsignedByteToInt(theCommandId));
               if (WinError.ERROR_DEVICE_NOT_CONNECTED.equals(writeStatus))
                  {
                  if (LOG.isInfoEnabled())
                     {
                     LOG.info("WindowsHIDDevice.write(): Throwing an HIDDeviceNotConnectedException since the write error was [" + WinError.ERROR_DEVICE_NOT_CONNECTED + "]");
                     }
                  throw new HIDDeviceNotConnectedException("Write failed because the device is not connected (" + writeStatus + ").  HIDWriteStatus = [" + hidWriteStatus + "]");
                  }
               else if (WinError.ERROR_GENERAL_FAILURE.equals(writeStatus))
                  {
                  if (LOG.isInfoEnabled())
                     {
                     LOG.info("WindowsHIDDevice.write(): Throwing an HIDDeviceFailureException since the write error was [" + WinError.ERROR_GENERAL_FAILURE + "]");
                     }
                  throw new HIDDeviceFailureException("Write failed because the device is not functioning (" + writeStatus + ").  HIDWriteStatus = [" + hidWriteStatus + "]");
                  }
               return hidWriteStatus;
               }
            }
         else
            {
            LOG.error("WindowsHIDDevice.write(): failed to write since we don't appear to have a connection established");
            return new HIDWriteStatus(data.length, 0, false, null);
            }
         }

      return HIDWriteStatus.WRITE_FAILED;
      }

   private byte getCommandId()
      {
      commandId++;
      if (commandId > 255)
         {
         commandId = 0;
         }
      return commandId;
      }

   public boolean disconnect()
      {
      return disconnect(hidDeviceInfo);
      }

   private boolean disconnect(final DeviceInfo deviceInfo)
      {
      LOG.trace("WindowsHIDDevice.disconnect()");
      if (deviceInfo != null)
         {
         final PointerByReference fileHandle = deviceInfo.getFileHandle();
         if (fileHandle != null)
            {
            final boolean closeHandleSuccess = Kernel32Library.INSTANCE.CloseHandle(fileHandle);
            final WinError closeHandleStatus = getLastError("CloseHandle");
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
