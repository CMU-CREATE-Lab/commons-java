package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDDevice
   {
   void connect() throws HIDDeviceNotFoundException, HIDConnectionException;

   /**
    * Tries to create an exlcusive connection to the device (no other programs will be allowed to write to or read from
    * the device).
    */
   void connectExclusively() throws HIDDeviceNotFoundException, HIDConnectionException;

   /**
    * Returns the filename of the device, as reported by the OS.  Returns <code>null</code> if no connection to the
    * device has been established.
    */
   String getDeviceFilename();

   /** Reads from the device and returns the data, or returns <code>null</code> if the read failed. */
   byte[] read();

   /**
    * Writes the given data (truncating any bytes which don't fit into a single report) and returns the number of bytes
    * written.
    */
   HIDWriteStatus write(final byte[] data);

   boolean disconnect();
   }