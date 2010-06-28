package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SetupAPILibrary extends StdCallLibrary
   {
   /**
    * Flags controlling what is included in the device information set built by {@link #SetupDiGetClassDevsW}/{@link #SetupDiGetClassDevsA}.
    */
   interface Flags
      {
      int DIGCF_DEFAULT = 0x00000001;
      int DIGCF_PRESENT = 0x00000002;
      int DIGCF_ALLCLASSES = 0x00000004;
      int DIGCF_PROFILE = 0x00000008;
      int DIGCF_DEVICEINTERFACE = 0x00000010;
      }

   SetupAPILibrary INSTANCE = (SetupAPILibrary)Native.loadLibrary("setupapi", SetupAPILibrary.class);

   /**
    * <p>
    * The <code>SetupDiGetClassDevsW</code> function returns a handle to a device information set that contains requested device
    * information elements for a local computer.  The caller of <code>SetupDiGetClassDevsW</code> must delete the
    * returned device information set when it is no longer needed by calling {@link #SetupDiDestroyDeviceInfoList}. For
    * more information, see <a href="http://msdn.microsoft.com/en-us/library/ff551069(VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff551069(VS.85).aspx</a>.
    * </p>
    * <p>
    * Syntax:
    * </p>
    * <blockquote>
    * <code>
    * HDEVINFO SetupDiGetClassDevsW(<br>
    * &nbsp;&nbsp;__in_opt  const GUID *ClassGuid,<br>
    * &nbsp;&nbsp;__in_opt  PCTSTR Enumerator,<br>
    * &nbsp;&nbsp;__in_opt  HWND hwndParent,<br>
    * &nbsp;&nbsp;__in      DWORD Flags<br>
    * );
    * </code>
    * </blockquote>
    *
    * @param classGuid A pointer to the GUID for a device setup class or a device interface class. This pointer is
    * optional and can be NULL.
    *
    * @param enumerator A pointer to a NULL-terminated string that specifies:
    * <ul>
    *    <li>
    *       An identifier (ID) of a Plug and Play (PnP) enumerator. This ID can either be the value's globally unique
    *       identifier ({@link GUID}) or symbolic name. For example, "PCI" can be used to specify the PCI PnP value.
    *       Other examples of symbolic names for PnP values include "USB," "PCMCIA," and "SCSI".
    *    </li>
    *    <li>
    *       A PnP device instance ID. When specifying a PnP device instance ID, {@link Flags#DIGCF_DEVICEINTERFACE}
    *       must be set in the <code>flags</code> parameter.
    *    </li>
    * </ul>
    * <p>
    * This pointer is optional and can be <code>null</code>. If an enumeration value is not used to select devices, set
    * Enumerator to <code>null</code>.
    * </p>
    *
    * @param hwndParent A handle to the top-level window to be used for a user interface that is associated with
    * installing a device instance in the device information set. This handle is optional and can be NULL.
    *
    * @param flags A variable of type <code>int</code> that specifies control options that filter the device information
    * elements that are added to the device information set. This parameter can be a bitwise OR of zero or more of the
    * following flags:
    * <dl>
    * <dt>DIGCF_ALLCLASSES</dt>
    * <dd>Return a list of installed devices for all device setup classes or all device interface classes.</dd>
    * <dt>DIGCF_DEVICEINTERFACE</dt>
    * <dd>
    *    Return devices that support device interfaces for the specified device interface classes. This flag must be
    *    set in the <code>flags</code> parameter if the <code>enumerator</code> parameter specifies a device instance
    *    ID.
    * </dd>
    * <dt>DIGCF_DEFAULT</dt>
    * <dd>Return only the device that is associated with the system default device interface, if one is set, for the
    * specified device interface classes.</dd>
    * <dt>DIGCF_PRESENT</dt>
    * <dd>Return only devices that are currently present in a system.</dd>
    * <dt>DIGCF_PROFILE</dt>
    * <dd>Return only devices that are a part of the current hardware profile.</dd>
    * </dl>
    *
    * @return If the operation succeeds, <code>SetupDiGetClassDevsW</code> returns a handle to a device information set
    * that contains all installed devices that matched the supplied parameters. If the operation fails, the function
    * returns INVALID_HANDLE_VALUE.
    */
   HDEVINFO SetupDiGetClassDevsW(final GUID classGuid,
                                 final WString enumerator,
                                 final PointerByReference hwndParent,
                                 final int flags);

   /**
    * <p>
    * The <code>SetupDiGetClassDevsA</code> function returns a handle to a device information set that contains requested device
    * information elements for a local computer.  The caller of <code>SetupDiGetClassDevsA</code> must delete the
    * returned device information set when it is no longer needed by calling {@link #SetupDiDestroyDeviceInfoList}. For
    * more information, see <a href="http://msdn.microsoft.com/en-us/library/ff551069(VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff551069(VS.85).aspx</a>.
    * </p>
    * <p>
    * Syntax:
    * </p>
    * <blockquote>
    * <code>
    * HDEVINFO SetupDiGetClassDevsA(<br>
    * &nbsp;&nbsp;__in_opt  const GUID *ClassGuid,<br>
    * &nbsp;&nbsp;__in_opt  PCTSTR Enumerator,<br>
    * &nbsp;&nbsp;__in_opt  HWND hwndParent,<br>
    * &nbsp;&nbsp;__in      DWORD Flags<br>
    * );
    * </code>
    * </blockquote>
    *
    * @param classGuid A pointer to the GUID for a device setup class or a device interface class. This pointer is
    * optional and can be NULL.
    *
    * @param enumerator A pointer to a NULL-terminated string that specifies:
    * <ul>
    *    <li>
    *       An identifier (ID) of a Plug and Play (PnP) enumerator. This ID can either be the value's globally unique
    *       identifier ({@link GUID}) or symbolic name. For example, "PCI" can be used to specify the PCI PnP value.
    *       Other examples of symbolic names for PnP values include "USB," "PCMCIA," and "SCSI".
    *    </li>
    *    <li>
    *       A PnP device instance ID. When specifying a PnP device instance ID, {@link Flags#DIGCF_DEVICEINTERFACE}
    *       must be set in the <code>flags</code> parameter.
    *    </li>
    * </ul>
    * <p>
    * This pointer is optional and can be <code>null</code>. If an enumeration value is not used to select devices, set
    * Enumerator to <code>null</code>.
    * </p>
    *
    * @param hwndParent A handle to the top-level window to be used for a user interface that is associated with
    * installing a device instance in the device information set. This handle is optional and can be NULL.
    *
    * @param flags A variable of type <code>int</code> that specifies control options that filter the device information
    * elements that are added to the device information set. This parameter can be a bitwise OR of zero or more of the
    * following flags:
    * <dl>
    * <dt>DIGCF_ALLCLASSES</dt>
    * <dd>Return a list of installed devices for all device setup classes or all device interface classes.</dd>
    * <dt>DIGCF_DEVICEINTERFACE</dt>
    * <dd>
    *    Return devices that support device interfaces for the specified device interface classes. This flag must be
    *    set in the <code>flags</code> parameter if the <code>enumerator</code> parameter specifies a device instance
    *    ID.
    * </dd>
    * <dt>DIGCF_DEFAULT</dt>
    * <dd>Return only the device that is associated with the system default device interface, if one is set, for the
    * specified device interface classes.</dd>
    * <dt>DIGCF_PRESENT</dt>
    * <dd>Return only devices that are currently present in a system.</dd>
    * <dt>DIGCF_PROFILE</dt>
    * <dd>Return only devices that are a part of the current hardware profile.</dd>
    * </dl>
    *
    * @return If the operation succeeds, <code>SetupDiGetClassDevsA</code> returns a handle to a device information set
    * that contains all installed devices that matched the supplied parameters. If the operation fails, the function
    * returns INVALID_HANDLE_VALUE.
    */
   HDEVINFO SetupDiGetClassDevsA(final GUID classGuid,
                                 final String enumerator,
                                 final PointerByReference hwndParent,
                                 final int flags);

   /**
    * <p>
    * The <code>SetupDiEnumDeviceInterfaces</code> function enumerates the device interfaces that are contained in a
    * device information set.
    * </p>
    * <p>
    * Repeated calls to this function return an {@link SP_DEVICE_INTERFACE_DATA} structure for a different device
    * interface. This function can be called repeatedly to get information about interfaces in a device information set
    * that are associated with a particular device information element or that are associated with all device
    * information elements.
    * </p>
    * <p>
    * <code>deviceInterfaceData</code> points to a structure that identifies a requested device interface. To get
    * detailed information about an interface, call {@link #SetupDiGetDeviceInterfaceDetailW}. The detailed information
    * includes the name of the device interface that can be passed to a Win32 function such as
    * {@link Kernel32Library#CreateFileW}/{@link Kernel32Library#CreateFileA} to get a handle to the interface.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/ff551015(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff551015(v=VS.85).aspx</a>.
    * </p>
    * <p>
    * Syntax:
    * </p>
    * <blockquote>
    * <code>
    * BOOL SetupDiEnumDeviceInterfaces(<br>
    * &nbsp;&nbsp;__in      HDEVINFO DeviceInfoSet,<br>
    * &nbsp;&nbsp;__in_opt  PSP_DEVINFO_DATA DeviceInfoData,<br>
    * &nbsp;&nbsp;__in      const GUID *InterfaceClassGuid,<br>
    * &nbsp;&nbsp;__in      DWORD MemberIndex,<br>
    * &nbsp;&nbsp;__out     PSP_DEVICE_INTERFACE_DATA DeviceInterfaceData<br>
    * );
    * </code>
    * </blockquote>
    *
    * @param deviceInformationSet A pointer to a device information set that contains the device interfaces for which to
    * return information. This handle is typically returned by {@link #SetupDiGetClassDevsW}.
    *
    * @param deviceInfoData A pointer to an {@link SP_DEVINFO_DATA} structure that specifies a device information
    * element in <code>deviceInformationSet</code>. This parameter is optional and can be <code>null</code>.  If this
    * parameter is specified, <code>SetupDiEnumDeviceInterfaces</code> constrains the enumeration to the interfaces that
    * are supported by the specified device. If this parameter is <code>null</code>, repeated calls to
    * <code>SetupDiEnumDeviceInterfaces</code> return information about the interfaces that are associated with all the
    * device information elements in <code>deviceInformationSet</code>. This pointer is typically returned by
    * <code>SetupDiEnumDeviceInfo</code>.
    *
    * @param guid A pointer to a {@link GUID} that specifies the device interface class for the requested interface.
    *
    * @param memberIndex A zero-based index into the list of interfaces in the device information set. The caller
    * should call this function first with <code>memberIndex</code> set to zero to obtain the first interface. Then,
    * repeatedly increment <code>memberIndex</code> and retrieve an interface until this function fails and
    * {@link Native#getLastError()} returns {@link WinError#ERROR_NO_MORE_ITEMS}.  If <code>deviceInfoData</code>
    * specifies a particular device, the <code>memberIndex</code> is relative to only the interfaces exposed by that
    * device.
    *
    * @param deviceInterfaceData A pointer to a caller-allocated buffer that contains, on successful return, a completed
    * {@link SP_DEVICE_INTERFACE_DATA} structure that identifies an interface that meets the search parameters. The
    * caller must set <code>deviceInterfaceData.cbSize</code> to
    * {@link SP_DEVICE_INTERFACE_DATA#size SP_DEVICE_INTERFACE_DATA.size()} before calling this function.
    *
    * @return <code>SetupDiEnumDeviceInterfaces</code> returns <code>true</code> if the function completed without
    * error. If the function completed with an error, <code>false</code> is returned and the error code for the failure
    * can be retrieved by calling {@link Native#getLastError()}.
    */
   boolean SetupDiEnumDeviceInterfaces(final HDEVINFO deviceInformationSet,
                                       final SP_DEVINFO_DATA deviceInfoData,
                                       final GUID guid,
                                       final int memberIndex,
                                       final SP_DEVICE_INTERFACE_DATA deviceInterfaceData);

   /**
    * <p>
    * The <code>SetupDiGetDeviceInterfaceDetailW</code> function returns details about a device interface.
    * </p>
    * <p>
    * Using this function to get details about an interface is typically a two-step process:
    * </p>
    * <ol>
    *    <li>
    *       Get the required buffer size. Call <code>SetupDiGetDeviceInterfaceDetailW</code> with a <code>null</code>
    *       <code>deviceInterfaceDetailData</code> pointer, a <code>deviceInterfaceDetailDataSize</code> of zero, and a
    *       valid <code>requiredSize</code> variable. In response to such a call, this function returns the required
    *       buffer size at <code>requiredSize</code> and fails with {@link Native#getLastError()} returning
    *       {@link WinError#ERROR_INSUFFICIENT_BUFFER}.
    *    </li> 
    *    <li>Allocate an appropriately sized buffer and call the function again to get the interface details.</li>
    * </ol>
    * <p>
    * The interface detail returned by this function consists of a device path that can be passed to Win32 functions
    * such as {@link Kernel32Library#CreateFileW}/{@link Kernel32Library#CreateFileA}. Do not attempt to parse the device path symbolic name. The device path
    * can be reused across system starts.
    * </p>
    * <p>
    * SetupDiGetDeviceInterfaceDetailW can be used to get just the <code>deviceInfoData</code>. If the interface exists
    * but <code>deviceInterfaceDetailData</code> is <code>null</code>, this function fails,
    * {@link Native#getLastError()} returns {@link WinError#ERROR_INSUFFICIENT_BUFFER}, and the
    * <code>deviceInfoData</code> structure is filled with information about the device that exposes the interface.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/ff551120(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff551120(v=VS.85).aspx</a>.
    * </p>
    * <p>
    * Syntax:
    * </p>
    * <blockquote>
    * <code>
    * BOOL SetupDiGetDeviceInterfaceDetailW(<br>
    * &nbsp;&nbsp;__in       HDEVINFO DeviceInfoSet,<br>
    * &nbsp;&nbsp;__in       PSP_DEVICE_INTERFACE_DATA DeviceInterfaceData,<br>
    * &nbsp;&nbsp;__out_opt  PSP_DEVICE_INTERFACE_DETAIL_DATA DeviceInterfaceDetailData,<br>
    * &nbsp;&nbsp;__in       DWORD DeviceInterfaceDetailDataSize,<br>
    * &nbsp;&nbsp;__out_opt  PDWORD RequiredSize,<br>
    * &nbsp;&nbsp;__out_opt  PSP_DEVINFO_DATA DeviceInfoData<br>
    * );
    * </code>
    * </blockquote>
    *
    * @param deviceInformationSet A pointer to the device information set that contains the interface for which to
    * retrieve details. This handle is typically returned by {@link #SetupDiGetClassDevsW}.
    *
    * @param deviceInterfaceData A pointer to an {@link SP_DEVICE_INTERFACE_DATA} structure that specifies the interface
    * in deviceInformationSet for which to retrieve details. A pointer of this type is typically returned by
    * {@link #SetupDiEnumDeviceInterfaces}.
    *
    * @param deviceInterfaceDetailData A pointer to an {@link SP_DEVICE_INTERFACE_DETAIL_DATA} structure to receive
    * information about the specified interface. This parameter is optional and can be <code>null</code>. This parameter
    * must be <code>null</code> if deviceInterfaceDetailDataSize is zero.  If this parameter is specified, the caller
    * must set DeviceInterfaceDetailData.cbSize to sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA) before calling this function.
    * The <code>cbSize</code> member always contains the size of the fixed part of the data structure, not a size
    * reflecting the variable-length string at the end.
    *
    * @param deviceInterfaceDetailDataSize The size of the <code>deviceInterfaceDetailData</code> buffer. The buffer
    * must be at least <code>(offsetof(SP_DEVICE_INTERFACE_DETAIL_DATA, DevicePath) + sizeof(TCHAR))</code> bytes, to
    * contain the fixed part of the structure and a single <code>null</code> to terminate an empty MULTI_SZ string. This
    * parameter must be zero if DeviceInterfaceDetailData is NULL.
    *
    * @param requiredSize A pointer to a int that receives the required size of the deviceInterfaceDetailData buffer.
    * This size includes the size of the fixed part of the structure plus the number of bytes required for the
    * variable-length device path string.  This parameter is optional and can be <code>null</code>.
    *
    * @param deviceInfoData A pointer to a buffer that receives information about the device that supports the requested
    * interface. The caller must set <code>deviceInfoData.cbSize</code> to sizeof(SP_DEVINFO_DATA). This parameter
    * is optional and can be <code>null</code>.
    *
    * @return Returns <code>true</code> if the function completed without error.  If the function completed with an
    * error, <code>false</code> is returned and the error code for the failure can be retrieved by calling
    * {@link Native#getLastError()}.
    */
   boolean SetupDiGetDeviceInterfaceDetailW(final HDEVINFO deviceInformationSet,
                                            final SP_DEVICE_INTERFACE_DATA deviceInterfaceData,
                                            final SP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData,
                                            final int deviceInterfaceDetailDataSize,
                                            final IntByReference requiredSize,
                                            final SP_DEVINFO_DATA deviceInfoData);

   /**
    * <p>
    * The <code>SetupDiGetDeviceInterfaceDetailA</code> function returns details about a device interface.
    * </p>
    * <p>
    * Using this function to get details about an interface is typically a two-step process:
    * </p>
    * <ol>
    *    <li>
    *       Get the required buffer size. Call <code>SetupDiGetDeviceInterfaceDetailA</code> with a <code>null</code>
    *       <code>deviceInterfaceDetailData</code> pointer, a <code>deviceInterfaceDetailDataSize</code> of zero, and a
    *       valid <code>requiredSize</code> variable. In response to such a call, this function returns the required
    *       buffer size at <code>requiredSize</code> and fails with {@link Native#getLastError()} returning
    *       {@link WinError#ERROR_INSUFFICIENT_BUFFER}.
    *    </li>
    *    <li>Allocate an appropriately sized buffer and call the function again to get the interface details.</li>
    * </ol>
    * <p>
    * The interface detail returned by this function consists of a device path that can be passed to Win32 functions
    * such as {@link Kernel32Library#CreateFileW}/{@link Kernel32Library#CreateFileA}. Do not attempt to parse the device path symbolic name. The device path
    * can be reused across system starts.
    * </p>
    * <p>
    * SetupDiGetDeviceInterfaceDetailA can be used to get just the <code>deviceInfoData</code>. If the interface exists
    * but <code>deviceInterfaceDetailData</code> is <code>null</code>, this function fails,
    * {@link Native#getLastError()} returns {@link WinError#ERROR_INSUFFICIENT_BUFFER}, and the
    * <code>deviceInfoData</code> structure is filled with information about the device that exposes the interface.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/ff551120(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff551120(v=VS.85).aspx</a>.
    * </p>
    * <p>
    * Syntax:
    * </p>
    * <blockquote>
    * <code>
    * BOOL SetupDiGetDeviceInterfaceDetailA(<br>
    * &nbsp;&nbsp;__in       HDEVINFO DeviceInfoSet,<br>
    * &nbsp;&nbsp;__in       PSP_DEVICE_INTERFACE_DATA DeviceInterfaceData,<br>
    * &nbsp;&nbsp;__out_opt  PSP_DEVICE_INTERFACE_DETAIL_DATA DeviceInterfaceDetailData,<br>
    * &nbsp;&nbsp;__in       DWORD DeviceInterfaceDetailDataSize,<br>
    * &nbsp;&nbsp;__out_opt  PDWORD RequiredSize,<br>
    * &nbsp;&nbsp;__out_opt  PSP_DEVINFO_DATA DeviceInfoData<br>
    * );
    * </code>
    * </blockquote>
    *
    * @param deviceInformationSet A pointer to the device information set that contains the interface for which to
    * retrieve details. This handle is typically returned by {@link #SetupDiGetClassDevsW}.
    *
    * @param deviceInterfaceData A pointer to an {@link SP_DEVICE_INTERFACE_DATA} structure that specifies the interface
    * in deviceInformationSet for which to retrieve details. A pointer of this type is typically returned by
    * {@link #SetupDiGetDeviceInterfaceDetailA}.
    *
    * @param deviceInterfaceDetailData A pointer to an {@link SP_DEVICE_INTERFACE_DETAIL_DATA} structure to receive
    * information about the specified interface. This parameter is optional and can be <code>null</code>. This parameter
    * must be <code>null</code> if deviceInterfaceDetailDataSize is zero.  If this parameter is specified, the caller
    * must set DeviceInterfaceDetailData.cbSize to sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA) before calling this function.
    * The <code>cbSize</code> member always contains the size of the fixed part of the data structure, not a size
    * reflecting the variable-length string at the end.
    *
    * @param deviceInterfaceDetailDataSize The size of the <code>deviceInterfaceDetailData</code> buffer. The buffer
    * must be at least <code>(offsetof(SP_DEVICE_INTERFACE_DETAIL_DATA, DevicePath) + sizeof(TCHAR))</code> bytes, to
    * contain the fixed part of the structure and a single <code>null</code> to terminate an empty MULTI_SZ string. This
    * parameter must be zero if DeviceInterfaceDetailData is NULL.
    *
    * @param requiredSize A pointer to a int that receives the required size of the deviceInterfaceDetailData buffer.
    * This size includes the size of the fixed part of the structure plus the number of bytes required for the
    * variable-length device path string.  This parameter is optional and can be <code>null</code>.
    *
    * @param deviceInfoData A pointer to a buffer that receives information about the device that supports the requested
    * interface. The caller must set <code>deviceInfoData.cbSize</code> to sizeof(SP_DEVINFO_DATA). This parameter
    * is optional and can be <code>null</code>.
    *
    * @return Returns <code>true</code> if the function completed without error.  If the function completed with an
    * error, <code>false</code> is returned and the error code for the failure can be retrieved by calling
    * {@link Native#getLastError()}.
    */
   boolean SetupDiGetDeviceInterfaceDetailA(final HDEVINFO deviceInformationSet,
                                            final SP_DEVICE_INTERFACE_DATA deviceInterfaceData,
                                            final SP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData,
                                            final int deviceInterfaceDetailDataSize,
                                            final IntByReference requiredSize,
                                            final SP_DEVINFO_DATA deviceInfoData);

   /**
    * <p>
    * The <code>SetupDiDestroyDeviceInfoList</code> function deletes a device information set and frees all associated memory. For
    * more information, see <a href="http://msdn.microsoft.com/en-us/library/ff550996(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ff550996(v=VS.85).aspx</a>.
    * </p>
    * <p>
    * Syntax:
    * </p>
    * <blockquote>
    * <code>
    * BOOL SetupDiDestroyDeviceInfoList(<br>
    * &nbsp;&nbsp;__in  HDEVINFO DeviceInfoSet<br>
    * );
    * </code>
    * </blockquote>
    *
    * @param deviceInformationSet A handle to the device information set to delete.
    *
    * @return returns <code>true</code> upon success; <code>false</code> otherwise.
    */
   boolean SetupDiDestroyDeviceInfoList(final HDEVINFO deviceInformationSet);
   }
