package edu.cmu.ri.createlab.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialPortCommandStrategy
   {
   SerialPortCommandResponse execute(final SerialPortIOHelper ioHelper);
   }