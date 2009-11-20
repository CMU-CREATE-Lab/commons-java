package edu.cmu.ri.createlab.serial.device;

import java.io.IOException;
import edu.cmu.ri.createlab.serial.SerialPortException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceProxyCreator
   {
   SerialDeviceProxy createSerialDeviceProxy(final String serialPortName) throws IOException, SerialPortException;
   }