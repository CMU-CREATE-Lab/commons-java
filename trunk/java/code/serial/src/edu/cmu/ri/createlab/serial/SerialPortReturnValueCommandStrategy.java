package edu.cmu.ri.createlab.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialPortReturnValueCommandStrategy<T> extends SerialPortCommandStrategy, SerialPortCommandResponseConverter<T>
   {
   }