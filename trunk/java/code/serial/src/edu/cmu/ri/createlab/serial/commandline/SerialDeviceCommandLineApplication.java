package edu.cmu.ri.createlab.serial.commandline;

import java.io.BufferedReader;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import edu.cmu.ri.createlab.util.commandline.BaseCommandLineApplication;

/**
 * <p>
 * <code>SerialDeviceCommandLineApplication</code> provides a framework for command line applications to control serial
 * devices.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class SerialDeviceCommandLineApplication extends BaseCommandLineApplication
   {
   public SerialDeviceCommandLineApplication(final BufferedReader in)
      {
      super(in);
      }

   protected abstract void disconnect();

   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   protected final SortedMap<Integer, String> enumeratePorts()
      {
      final SortedSet<String> availableSerialPorts = SerialPortEnumerator.getAvailableSerialPorts();
      final SortedMap<Integer, String> portMap = new TreeMap<Integer, String>();

      if ((availableSerialPorts != null) && (!availableSerialPorts.isEmpty()))
         {
         println("Available serial port(s):");
         int i = 1;
         for (final String portName : availableSerialPorts)
            {
            System.out.printf("%6d:  %s\n", i, portName);
            portMap.put(i++, portName);
            }
         }
      else
         {
         println("No available serial ports.");
         }

      return portMap;
      }
   }

