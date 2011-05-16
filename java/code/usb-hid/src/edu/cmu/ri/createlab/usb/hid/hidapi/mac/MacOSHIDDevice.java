package edu.cmu.ri.createlab.usb.hid.hidapi.mac;

import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;
import edu.cmu.ri.createlab.usb.hid.hidapi.BaseHIDAPIDevice;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MacOSHIDDevice extends BaseHIDAPIDevice
   {
   public MacOSHIDDevice(final HIDDeviceDescriptor hidDeviceDescriptor)
      {
      super(hidDeviceDescriptor);
      }

   public boolean isReportIDIncludedInReadData()
      {
      // Mac OS does not include the report ID in the read data
      return false;
      }
   }
