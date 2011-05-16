package edu.cmu.ri.createlab.usb.hid.hidapi.linux;

import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;
import edu.cmu.ri.createlab.usb.hid.hidapi.BaseHIDAPIDevice;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LinuxHIDDevice extends BaseHIDAPIDevice
   {
   public LinuxHIDDevice(final HIDDeviceDescriptor hidDeviceDescriptor)
      {
      super(hidDeviceDescriptor);
      }

   public boolean isReportIDIncludedInReadData()
      {
      // Linux does not include the report ID in the read data
      return false;
      }
   }
