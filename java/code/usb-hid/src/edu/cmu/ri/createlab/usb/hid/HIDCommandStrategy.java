package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDCommandStrategy
   {
   HIDCommandResult execute(final HIDDevice hidDevice);
   }