package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.util.commandexecution.CommandResponseConverter;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDCommandResponseConverter<DesiredClass> extends CommandResponseConverter<HIDCommandResponse, DesiredClass>
   {
   @Override
   DesiredClass convertResponse(final HIDCommandResponse response);
   }
