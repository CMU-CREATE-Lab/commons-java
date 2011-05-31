package edu.cmu.ri.createlab.util.commandexecution;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CommandStrategy<DeviceIOClass, ResponseClass extends CommandResponse>
   {
   ResponseClass execute(final DeviceIOClass deviceIOClass) throws Exception;
   }