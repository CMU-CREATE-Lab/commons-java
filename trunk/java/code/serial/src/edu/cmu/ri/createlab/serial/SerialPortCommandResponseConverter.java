package edu.cmu.ri.createlab.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialPortCommandResponseConverter<T>
   {
   T convertResponse(final SerialPortCommandResponse result);
   }