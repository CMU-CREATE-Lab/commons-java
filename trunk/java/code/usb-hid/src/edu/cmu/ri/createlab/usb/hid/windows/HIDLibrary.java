package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HIDLibrary extends StdCallLibrary
   {
   HIDLibrary INSTANCE = (HIDLibrary)Native.loadLibrary("hid", HIDLibrary.class);

   /**
    * <p>
    * The <code>HidD_GetHidGuid</code> routine returns the device interface GUID for HIDClass devices.  Only user-mode
    * applications can call this method.  For more information, see <a href="http://msdn.microsoft.com/en-us/library/ff538924(VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff538924(VS.85).aspx</a>.
    * </p>
    *
    * @param hidGuid Pointer to a caller-allocated {@link GUID} buffer that the routine uses to return the device
    * interface {@link GUID} for HIDClass devices.
    */
   void HidD_GetHidGuid(final GUID hidGuid);

   /**
    * <p>
    * The <code>HidD_GetAttributes</code> routine returns the attributes of a specified top-level collection. For more
    * information, see <a href="http://msdn.microsoft.com/en-us/library/ff538900(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff538900(v=VS.85).aspx</a>.
    * </p>
    *
    * @param hidDeviceObject Specifies an open handle to a top-level collection.
    *
    * @param attributes Pointer to a caller-allocated {@link HIDD_ATTRIBUTES} structure that returns the attributes of
    * the collection specified by <code>hidDeviceObject</code>.
    *
    * @return <code>true</code> upon success; <code>false</code> otherwise
    */
   boolean HidD_GetAttributes(final PointerByReference hidDeviceObject,
                              final HIDD_ATTRIBUTES attributes);

   /**
    * <p>
    * The <code>HidD_GetPreparsedData</code> routine returns a top-level collection's preparsed data. For more
    * information, see <a href="http://msdn.microsoft.com/en-us/library/ff539679(VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff539679(VS.85).aspx</a>.
    * </p>
    * <p>
    * When an application no longer requires the preparsed data, it should call {@link #HidD_FreePreparsedData} to free the preparsed data buffer.
    * </p>
    * @param hidDeviceObject Specifies an open handle to a top-level collection.
    *
    * @param hidPreparsedData Pointer to the address of a routine-allocated buffer that contains a collection's 
    * preparsed data in a _HIDP_PREPARSED_DATA structure.
    *
    * @return <code>true</code> upon success; <code>false</code> otherwise
    */
   boolean HidD_GetPreparsedData(final PointerByReference hidDeviceObject,
                                 final IntByReference hidPreparsedData);

   /**
    * <p>
    * The <code>HidD_FreePreparsedData</code> routine releases the resources that the HID class driver allocated to hold
    * a top-level collection's preparsed data. For more information, see
    * <a href="http://msdn.microsoft.com/en-us/library/ff538893(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff538893(v=VS.85).aspx</a>.
    * </p>
    * @param hidPreparsedData pointer to the buffer, returned by {@link #HidD_GetPreparsedData}, that is freed.
    * @return <code>true</code> upon success; <code>false</code> otherwise
    */
   boolean HidD_FreePreparsedData(final IntByReference hidPreparsedData);

   /**
    * <p>
    * The <code>HidP_GetCaps</code> routine returns a top-level collection's {@link HIDP_CAPS} structure.
    * </p>
    * <p></p>
    * @param hidPreparsedData a top-level collection's preparsed data.
    *
    * @param capabilities pointer to a caller-allocated buffer that the routine uses to return a collection's
    * {@link HIDP_CAPS} structure.
    *
    * @return <code>HIDP_STATUS_SUCCESS</code> if the routine successfully returned the collection capability 
    * information; or <code>HIDP_STATUS_INVALID_PREPARSED_DATA</code> if the specified preparsed data is invalid.
    */
   int HidP_GetCaps(final int hidPreparsedData, final HIDP_CAPS capabilities);
   }
