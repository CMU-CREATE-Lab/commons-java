package edu.cmu.ri.createlab.usb.hid.hidapi;

import com.ochafik.lang.jnaerator.runtime.CharByReference;
import com.ochafik.lang.jnaerator.runtime.Structure;

/**
 * hidapi info structure<br>
 * <i>native declaration : hidapi.h:48</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class HIDDeviceInfo extends Structure<HIDDeviceInfo, HIDDeviceInfo.ByValue, HIDDeviceInfo.ByReference>
   {
   /**
    * Platform-specific device path<br>
    * C type : char*
    */
   public String path;
   /// Device Vendor ID
   public short vendor_id;
   /// Device Product ID
   public short product_id;
   /**
    * Serial Number<br>
    * C type : wchar_t*
    */
   public CharByReference serial_number;
   /**
    * Device Release Number in binary-coded decimal,<br>
    * also known as Device Version Number
    */
   public short release_number;
   /**
    * Manufacturer String<br>
    * C type : wchar_t*
    */
   public CharByReference manufacturer_string;
   /**
    * Product string<br>
    * C type : wchar_t*
    */
   public CharByReference product_string;
   /**
    * Usage Page for this Device/Interface<br>
    * (Windows/Mac only).
    */
   public short usage_page;
   /**
    * Usage for this Device/Interface<br>
    * (Windows/Mac only).
    */
   public short usage;
   /**
    * The USB interface which this logical device<br>
    * represents. Valid on both Linux implementations<br>
    * in all cases, and valid on the Windows implementation<br>
    * only if the device contains more than one interface.
    */
   public int interface_number;
   /**
    * Pointer to the next device<br>
    * C type : hid_device_info*
    */
   public HIDDeviceInfo.ByReference next;

   public HIDDeviceInfo()
      {
      super();
      }

   protected ByReference newByReference()
      {
      return new ByReference();
      }

   protected ByValue newByValue()
      {
      return new ByValue();
      }

   protected HIDDeviceInfo newInstance()
      {
      return new HIDDeviceInfo();
      }

   public static HIDDeviceInfo[] newArray(final int arrayLength)
      {
      return Structure.newArray(HIDDeviceInfo.class, arrayLength);
      }

   public static class ByReference extends HIDDeviceInfo implements Structure.ByReference
      {

      }

   ;

   public static class ByValue extends HIDDeviceInfo implements Structure.ByValue
      {

      }

   ;
   }
