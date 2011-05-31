package edu.cmu.ri.createlab.serial;

import edu.cmu.ri.createlab.util.commandexecution.ReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceReturnValueCommandStrategy<DesiredClass> extends ReturnValueCommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse, DesiredClass>
   {
   }