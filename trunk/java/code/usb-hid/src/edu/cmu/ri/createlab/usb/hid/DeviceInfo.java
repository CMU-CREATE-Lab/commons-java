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
   }