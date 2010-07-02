package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDDevice
   {
   boolean connect();

   /** Reads from the device and returns the data, or returns <code>null</code> if the read failed */
   byte[] read();

   /**
    * Writes the given data (truncating any bytes which don't fit into a single report) and returns the number of bytes
    * written.
    */
   HIDWriteStatus write(final byte[] data);

   boolean disconnect();
   }