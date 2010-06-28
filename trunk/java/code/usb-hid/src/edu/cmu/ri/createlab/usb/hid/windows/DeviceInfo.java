package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.ptr.PointerByReference;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class DeviceInfo
   {
   private String deviceFilenamePath = null;
   private PointerByReference fileHandle = null;
   private short inputReportByteLength;
   private short outputReportByteLength;

   public void setDeviceFilenamePath(final String deviceFilenamePath)
      {
      this.deviceFilenamePath = deviceFilenamePath;
      }

   public String getDeviceFilenamePath()
      {
      return deviceFilenamePath;
      }

   public void setFileHandle(final PointerByReference fileHandle)
      {
      this.fileHandle = fileHandle;
      }

   public PointerByReference getFileHandle()
      {
      return fileHandle;
      }

   public void setInputAndOutputReportLengthInBytes(final short inputReportByteLength, final short outputReportByteLength)
      {
      this.inputReportByteLength = inputReportByteLength;
      this.outputReportByteLength = outputReportByteLength;
      }

   public short getInputReportByteLength()
      {
      return inputReportByteLength;
      }

   public short getOutputReportByteLength()
      {
      return outputReportByteLength;
      }
   }
