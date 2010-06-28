package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Structure;

/**
 * Code taken from http://code.google.com/p/jlsm/wiki/XP_Java_Reader
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GUID extends Structure
   {
   public int data1;
   public short data2;
   public short data3;
   public byte[] date4 = new byte[8];
   }
