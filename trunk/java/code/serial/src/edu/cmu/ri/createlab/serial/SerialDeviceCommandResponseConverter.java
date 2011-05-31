package edu.cmu.ri.createlab.serial;

import edu.cmu.ri.createlab.util.commandexecution.CommandResponseConverter;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceCommandResponseConverter<DesiredClass> extends CommandResponseConverter<SerialDeviceCommandResponse, DesiredClass>
   {
   @Override
   DesiredClass convertResponse(final SerialDeviceCommandResponse response);
   }