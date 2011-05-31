package edu.cmu.ri.createlab.util.commandexecution;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CommandResponseConverter<ResponseClass extends CommandResponse, DesiredClass>
   {
   DesiredClass convertResponse(final ResponseClass response);
   }