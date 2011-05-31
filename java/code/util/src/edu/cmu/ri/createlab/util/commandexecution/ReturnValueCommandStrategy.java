package edu.cmu.ri.createlab.util.commandexecution;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ReturnValueCommandStrategy<DeviceIOClass, ResponseClass extends CommandResponse, DesiredClass> extends CommandStrategy<DeviceIOClass, ResponseClass>,
                                                                                                                        CommandResponseConverter<ResponseClass, DesiredClass>
   {
   }