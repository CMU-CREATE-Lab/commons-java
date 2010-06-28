package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Structure;

/**
 * Code taken from http://code.google.com/p/jlsm/wiki/XP_Java_Reader
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SP_DEVICE_INTERFACE_DETAIL_DATA extends Structure
   {
   public int cbSize;
   public char[] devicePath = new char[1];

   public SP_DEVICE_INTERFACE_DETAIL_DATA()
      {
      setAlignType(Structure.ALIGN_NONE);
      }
   }

