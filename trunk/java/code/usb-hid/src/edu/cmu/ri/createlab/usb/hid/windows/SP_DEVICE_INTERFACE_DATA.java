package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Code taken from http://code.google.com/p/jlsm/wiki/XP_Java_Reader
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SP_DEVICE_INTERFACE_DATA extends Structure
   {
   public int cbSize;
   public GUID interfaceClassGuid;
   public int flags;

   // Making this a Pointer was a total guess.  I originally had it as an int, but 64-bit Windows 7 (running 64-bit
   // Java) barfed on it.  Then I changed it to a long, which made 64-bit Windows 7 happy, but then that broke 32-bit
   // Windows XP.  I then tried NativeLong, which I expected to do the right thing, but that only worked on 32-bit
   // Windows XP because--for some reason I can't explain--NativeLong.SIZE is 4 on both 32-bit and 64-bit Windows.  So,
   // I tried Pointer, and--amazingly--that works in both 32-bit and 64-bit Windows.
   public Pointer reserved;
   }
