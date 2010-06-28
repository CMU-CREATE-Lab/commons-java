package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Structure;

/**
 * Code taken from http://code.google.com/p/jlsm/wiki/XP_Java_Reader
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDD_ATTRIBUTES extends Structure
   {
   public int size;
   public short vendorId;
   public short productId;
   public short versionNumber;
   }
