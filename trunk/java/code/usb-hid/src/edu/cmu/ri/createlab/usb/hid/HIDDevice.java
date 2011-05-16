package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDDevice
   {
   void connect() throws HIDDeviceNotFoundException, HIDConnectionException;

   /**
    * Tries to create an exlcusive connection to the device (no other programs will be allowed to write to or read from
    * the device).  No guarantee is made as to whether the connection will actually be exclusive.
    */
   void connectExclusively() throws HIDDeviceNotFoundException, HIDConnectionException;

   /**
    * Returns the filename of the device, as reported by the OS.  Returns <code>null</code> if no connection to the
    * device has been established.
    */
   String getDeviceFilename();

   /** Returns the USB vendor ID for this device. */
   short getVendorID();

   /** Returns the USB product ID for this device. */
   short getProductID();

   /** Returns the {@link HIDDeviceDescriptor} for this device. */
   HIDDeviceDescriptor getHidDeviceDescriptor();

   /**
    * Reads from the device and returns the data, or may either return <code>null</code> or throw an exception if the
    * read failed.  This method returns <code>null</code> if a read is attempted before a connection has been
    * established.  An exception is thrown if the connection had been established but, for example, the user unplugs
    * the device (without disconnecting first) and then attempts a read.
    */
   byte[] read() throws HIDDeviceNotConnectedException, HIDDeviceFailureException;

   /**
    * Returns <code>true</code> if the report ID appears in the first byte of the array of read data; returns
    * <code>false</code> otherwise.
    */
   boolean isReportIDIncludedInReadData();

   /**
    * Writes the given data (truncating any bytes which don't fit into a single report) and returns the number of bytes
    * written.  This method will throw an exception if a connection to the device had been established but, for example,
    * the user unplugs the device (without disconnecting first) and then attempts a write.
    */
   HIDWriteStatus write(final byte[] data) throws HIDDeviceNotConnectedException, HIDDeviceFailureException;

   boolean disconnect();
   }