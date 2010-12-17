package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface DeviceInfo<FILEHANDLE_TYPE>
   {
   void setFileHandle(final FILEHANDLE_TYPE fileHandle);

   FILEHANDLE_TYPE getFileHandle();

   void setDeviceFilenamePath(final String deviceFilenamePath);

   String getDeviceFilenamePath();

   void setInputAndOutputReportLengthInBytes(final int inputReportByteLength, final int outputReportByteLength);

   /**
    * Returns the max length of an input report.  Be aware that whether or not the report byte is included is this
    * count is platform-specific (e.g. Windows includes the report byte, but Mac OS doesn't).
    */
   int getInputReportByteLength();

   /**
    * Returns the max length of an output report.  Be aware that whether or not the report byte is included is this
    * count is platform-specific (e.g. Windows includes the report byte, but Mac OS doesn't).
    */
   int getOutputReportByteLength();
   }