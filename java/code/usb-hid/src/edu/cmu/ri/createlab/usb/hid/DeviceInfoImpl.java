package edu.cmu.ri.createlab.usb.hid;

/**
 * <p>
 * <code>DeviceInfoImpl</code> provides base functionality for implementations of the {@link DeviceInfo} interface.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class DeviceInfoImpl<FILEHANDLE_TYPE> implements DeviceInfo<FILEHANDLE_TYPE>
   {
   private FILEHANDLE_TYPE fileHandle;
   private String deviceFilenamePath = null;

   public void setFileHandle(final FILEHANDLE_TYPE fileHandle)
      {
      this.fileHandle = fileHandle;
      }

   public FILEHANDLE_TYPE getFileHandle()
      {
      return fileHandle;
      }

   public final void setDeviceFilenamePath(final String deviceFilenamePath)
      {
      this.deviceFilenamePath = deviceFilenamePath;
      }

   public final String getDeviceFilenamePath()
      {
      return deviceFilenamePath;
      }
   }
