package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.util.commandexecution.ReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDDeviceReturnValueCommandStrategy<DesiredClass> extends ReturnValueCommandStrategy<HIDDevice, HIDCommandResponse, DesiredClass>
   {
   }
