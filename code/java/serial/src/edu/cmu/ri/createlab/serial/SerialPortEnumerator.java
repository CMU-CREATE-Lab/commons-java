package edu.cmu.ri.createlab.serial;

import java.util.Enumeration;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * <code>SerialPortEnumerator</code> assists in enumerating serial ports.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialPortEnumerator
   {
   private static final Log LOG = LogFactory.getLog(SerialPortEnumerator.class);

   /**
    * <p>
    * Returns a {@link SortedSet} of serial ports names.  May return an empty {@link SortedSet} if no serial ports are
    * found, but is guaranteed to not return <code>null</code>.
    * </p>
    * <p>
    * Note that the returned ports may already be in use.  To obtain a {@link SortedSet} of available ports, use
    * {@link #getAvailableSerialPorts()} instead.
    * </p>
    */
   public static SortedSet<String> getSerialPorts()
      {
      return new TreeSet<String>(getSerialPortsAsMap().keySet());
      }

   /**
    * <p>
    * Returns a {@link SortedSet} of names of serial ports that are not currently owned by another process.  May return
    * an empty {@link Set} if no such serial ports are found, but is guaranteed to not return <code>null</code>.
    * </p>
    * <p>
    * To obtain a {@link Set} of all serial ports, regardless of whether they are available, use
    * {@link #getSerialPorts()} instead.
    * </p>
    */
   public static SortedSet<String> getAvailableSerialPorts()
      {
      return new TreeSet<String>(getAvailableSerialPortsAsMap().keySet());
      }

   /**
    * Returns the {@link CommPortIdentifier} for the serial port with the given name; returns <code>null</code> if no
    * such port exists or if it is not available.
    */
   public static CommPortIdentifier getSerialPortIdentifer(final String serialPortName)
      {
      return getAvailableSerialPortsAsMap().get(serialPortName);
      }

   /**
    * <p>
    * Returns a {@link SortedMap} which maps {@link CommPortIdentifier} names (as returned by
    * {@link CommPortIdentifier#getName()}) to {@link CommPortIdentifier}s.  The {@link CommPortIdentifier}s identify
    * serial ports.  May return an empty {@link SortedMap} if no serial ports are found, but is guaranteed to not
    * return <code>null</code>.
    * </p>
    */
   private static SortedMap<String, CommPortIdentifier> getSerialPortsAsMap()
      {
      final SortedMap<String, CommPortIdentifier> portMap = new TreeMap<String, CommPortIdentifier>();

      final Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

      if (portIdentifiers != null)
         {
         while (portIdentifiers.hasMoreElements())
            {
            final CommPortIdentifier portIdentifier = (CommPortIdentifier)portIdentifiers.nextElement();
            if (portIdentifier != null)
               {
               // we only care about serial ports
               if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
                  {
                  portMap.put(portIdentifier.getName(), portIdentifier);
                  }
               }
            }
         }

      return portMap;
      }

   /**
    * <p>
    * Returns a {@link SortedMap} which maps {@link CommPortIdentifier} names (as returned by
    * {@link CommPortIdentifier#getName()}) to {@link CommPortIdentifier}s for serial ports that are not currently owned
    * by another process.  The {@link CommPortIdentifier}s identify serial ports.  May return an empty {@link SortedMap}
    * if no serial ports are found, but is guaranteed to not return <code>null</code>.
    * </p>
    */
   private static SortedMap<String, CommPortIdentifier> getAvailableSerialPortsAsMap()
      {
      final SortedMap<String, CommPortIdentifier> allSerialPorts = getSerialPortsAsMap();
      final SortedMap<String, CommPortIdentifier> availablePorts = new TreeMap<String, CommPortIdentifier>();

      for (final CommPortIdentifier portIdentifier : allSerialPorts.values())
         {
         if (portIdentifier != null && !portIdentifier.isCurrentlyOwned())
            {
            // Since we apparently can't rely on isCurrentlyOwned() to actually work, just try to open and close the
            // port.  This seems crazy, I know, but that's exactly what the RxTx folks suggest to do.  See:
            // http://rxtx.qbang.org/wiki/index.php/Discovering_available_comm_ports
            try
               {
               final CommPort port = portIdentifier.open(SerialPortEnumerator.class.getName(), 50);
               port.close();

               // we didn't trigger an exception, so the port must be available (sheesh!)
               availablePorts.put(portIdentifier.getName(), portIdentifier);
               }
            catch (PortInUseException e)
               {
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("PortInUseException while trying to open port [" + portIdentifier.getName() + "].  This can " +
                            "probably be ignored, since we're checking for availability and the only reliable way to do " +
                            "so is to try to open each port.", e);
                  }
               }
            }
         }

      return availablePorts;
      }

   private SerialPortEnumerator()
      {
      // private to prevent instantiation
      }
   }
