package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface Kernel32Library extends StdCallLibrary
   {
   Kernel32Library INSTANCE = (Kernel32Library)Native.loadLibrary("kernel32", Kernel32Library.class);

   interface DesiredAccess
      {
      /** Read, write, and execute access */
      int GENERIC_ALL = 0x10000000;

      /** Execute access */
      int GENERIC_EXECUTE = 0x20000000;

      /** Write access */
      int GENERIC_WRITE = 0x40000000;

      /** Read access */
      int GENERIC_READ = 0x80000000;
      }

   interface SharingMode    // found in wdm.h
      {
      /** Prevents other processes from opening a file or device if they request delete, read, or write access. */
      int FILE_SHARE_NONE = 0x00000000;

      /**
       * Enables subsequent open operations on a file or device to request read access. Otherwise, other processes cannot
       * open the file or device if they request read access. If this flag is not specified, but the file or device has
       * been opened for read access, the function fails.
       */
      int FILE_SHARE_READ = 0x00000001;

      /**
       * Enables subsequent open operations on a file or device to request write access. Otherwise, other processes cannot
       * open the file or device if they request write access. If this flag is not specified, but the file or device has
       * been opened for write access or has a file mapping with write access, the function fails.
       */
      int FILE_SHARE_WRITE = 0x00000002;

      /**
       * Enables subsequent open operations on a file or device to request delete access. Otherwise, other processes cannot
       * open the file or device if they request delete access. If this flag is not specified, but the file or device has
       * been opened for delete access, the function fails. Note  Delete access allows both delete and rename operations.
       */
      int FILE_SHARE_DELETE = 0x00000004;
      }

   interface CreationDisposition // found in WINBASE.H
      {
      /**
       * Creates a new file, only if it does not already exist. If the specified file exists, the function fails and the
       * last-error code is set to {@link WinError#ERROR_FILE_EXISTS}. If the specified file does not exist and is a
       * valid path to a writable location, a new file is created.
       */
      int CREATE_NEW = 1;

      /**
       * Creates a new file, always. If the specified file exists and is writable, the function overwrites the file, the
       * function succeeds, and last-error code is set to {@link WinError#ERROR_ALREADY_EXISTS}. If the specified file
       * does not exist and is a valid path, a new file is created, the function succeeds, and the last-error code is
       * set to zero.
       */
      int CREATE_ALWAYS = 2;

      /**
       * Opens a file or device, only if it exists. If the specified file or device does not exist, the function fails 
       * and the last-error code is set to {@link WinError#ERROR_FILE_NOT_FOUND}.
       */
      int OPEN_EXISTING = 3;

      /**
       * Opens a file, always. If the specified file exists, the function succeeds and the last-error code is set to
       * {@link WinError#ERROR_ALREADY_EXISTS}. If the specified file does not exist and is a valid path to a writable
       * location, the function creates a file and the last-error code is set to zero.
       */
      int OPEN_ALWAYS = 4;

      /**
       * Opens a file and truncates it so that its size is zero bytes, only if it exists. If the specified file does not
       * exist, the function fails and the last-error code is set to {@link WinError#ERROR_FILE_NOT_FOUND}. The calling
       * process must open the file with the {@link DesiredAccess#GENERIC_WRITE} bit set as part of the
       * <code>desiredAccess</code> parameter.
       */
      int TRUNCATE_EXISTING = 5;
      }

   interface FlagsAndAttributes
      {
      /** The file is read only. Applications can read the file, but cannot write to or delete it. */
      int FILE_ATTRIBUTE_READONLY = 0x1;

      /** The file is hidden. Do not include it in an ordinary directory listing. */
      int FILE_ATTRIBUTE_HIDDEN = 0x2;

      /** The file is part of or used exclusively by an operating system. */
      int FILE_ATTRIBUTE_SYSTEM = 0x4;

      /** The file should be archived. Applications use this attribute to mark files for backup or removal. */
      int FILE_ATTRIBUTE_ARCHIVE = 0x20;

      /** The file does not have other attributes set. This attribute is valid only if used alone. */
      int FILE_ATTRIBUTE_NORMAL = 0x80;

      /** The file is being used for temporary storage. */
      int FILE_ATTRIBUTE_TEMPORARY = 0x100;

      /**
       * The data of a file is not immediately available. This attribute indicates that file data is physically moved
       * to offline storage. This attribute is used by Remote Storage, the hierarchical storage management software.
       * Applications should not arbitrarily change this attribute.
       */
      int FILE_ATTRIBUTE_OFFLINE = 0x1000;

      /**
       * The file or directory is encrypted. For a file, this means that all data in the file is encrypted. For a
       * directory, this means that encryption is the default for newly created files and subdirectories. For more
       * information, see File Encryption. This flag has no effect if {@link #FILE_ATTRIBUTE_SYSTEM} is also specified.
       */
      int FILE_ATTRIBUTE_ENCRYPTED = 0x4000;

      /** The file is being opened or created for a backup or restore operation. The system ensures that the calling
       * process overrides file security checks when the process has SE_BACKUP_NAME and SE_RESTORE_NAME privileges.
       * You must set this flag to obtain a handle to a directory. A directory handle can be passed to some functions
       * instead of a file handle.
       */
      int FILE_FLAG_BACKUP_SEMANTICS = 0x02000000;

      /**
       * The file is to be deleted immediately after all of its handles are closed, which includes the specified handle
       * and any other open or duplicated handles. If there are existing open handles to a file, the call fails unless
       * they were all opened with the {@link SharingMode#FILE_SHARE_DELETE} share mode. Subsequent open requests for
       * the file fail, unless the {@link SharingMode#FILE_SHARE_DELETE} share mode is specified.
       */
      int FILE_FLAG_DELETE_ON_CLOSE = 0x04000000;

      /**
       * The file or device is being opened with no system caching for data reads and writes. This flag does not affect
       * hard disk caching or memory mapped files. There are strict requirements for successfully working with files
       * opened with {@link Kernel32Library#CreateFileW}/{@link Kernel32Library#CreateFileA} using the {@link #FILE_FLAG_NO_BUFFERING} flag, for details see
       * <a href="http://msdn.microsoft.com/en-us/library/cc644950(v=VS.85).aspx">File Buffering</a>.
       */
      int FILE_FLAG_NO_BUFFERING = 0x20000000;

      /**
       * The file data is requested, but it should continue to be located in remote storage. It should not be
       * transported back to local storage. This flag is for use by remote storage systems.
       */
      int FILE_FLAG_OPEN_NO_RECALL = 0x00100000;

      /**
       * Normal reparse point processing will not occur; {@link Kernel32Library#CreateFileW}/{@link Kernel32Library#CreateFileA} will attempt to open the
       * reparse point. When a file is opened, a file handle is returned, whether or not the filter that controls the
       * reparse point is operational. This flag cannot be used with the {@link CreationDisposition#CREATE_ALWAYS} flag.
       * If the file is not a reparse point, then this flag is ignored.
       */
      int FILE_FLAG_OPEN_REPARSE_POINT = 0x00200000;

      /**
       * The file or device is being opened or created for asynchronous I/O. When subsequent I/O operations are
       * completed on this handle, the event specified in the OVERLAPPED structure will be set to the signaled state.
       * If this flag is specified, the file can be used for simultaneous read and write operations. If this flag is
       * not specified, then I/O operations are serialized, even if the calls to the read and write functions specify an
       * OVERLAPPED structure. For information about considerations when using a file handle created with this flag, see
       * the <a href="http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx#synchronous_and_asynchronous_i_o_handles">Synchronous and Asynchronous I/O Handles</a> section of this topic.
       */
      int FILE_FLAG_OVERLAPPED = 0x40000000;

      /**
       * Access will occur according to POSIX rules. This includes allowing multiple files with names, differing only in
       * case, for file systems that support that naming. Use care when using this option, because files created with
       * this flag may not be accessible by applications that are written for MS-DOS or 16-bit Windows.
       */
      int FILE_FLAG_POSIX_SEMANTICS = 0x0100000;

      /**
       * Access is intended to be random. The system can use this as a hint to optimize file caching. This flag has no
       * effect if the file system does not support cached I/O and {@link #FILE_FLAG_NO_BUFFERING}.
       */
      int FILE_FLAG_RANDOM_ACCESS = 0x10000000;

      /**
       * Access is intended to be sequential from beginning to end. The system can use this as a hint to optimize file
       * caching. This flag should not be used if read-behind (that is, backwards scans) will be used. This flag has no
       * effect if the file system does not support cached I/O and {@link #FILE_FLAG_NO_BUFFERING}.
       */
      int FILE_FLAG_SEQUENTIAL_SCAN = 0x08000000;

      /** Write operations will not go through any intermediate cache, they will go directly to disk. */
      int FILE_FLAG_WRITE_THROUGH = 0x80000000;
      }

   /**
    * <p>
    * Creates or opens a file or I/O device. The most commonly used I/O devices are as follows: file, file stream,
    * directory, physical disk, volume, console buffer, tape drive, communications resource, mailslot, and pipe. The
    * function returns a pointer that can be used to access the file or device for various types of I/O depending on the
    * file or device and the flags and attributes specified.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx</a>.
    * </p>
    *
    * @param filename The name of the file or device to be created or opened.
    *
    * @param desiredAccess The requested access to the file or device, which can be summarized as read, write, both or
    * neither (zero). The most commonly used values are {@link DesiredAccess#GENERIC_READ},
    * {@link DesiredAccess#GENERIC_WRITE}, or both <code>({@link DesiredAccess#GENERIC_READ} |
    * {@link DesiredAccess#GENERIC_WRITE})</code>.  If this parameter is zero, the application can query certain
    * metadata such as file, directory, or device attributes without accessing that file or device, even if
    * {@link DesiredAccess#GENERIC_READ} access would have been denied. You cannot request an access mode that conflicts
    * with the sharing mode that is specified by the <code>shareMode</code> parameter in an open request that already
    * has an open handle.
    *
    * @param shareMode The requested sharing mode of the file or device, which can be read, write, both, delete, all of
    * these, or none (see {@link SharingMode}). Access requests to attributes or extended attributes are not affected by
    * this flag. If this parameter is zero and <code>CreateFile</code> succeeds, the file or device cannot be shared and
    * cannot be opened again until the handle to the file or device is closed. You cannot request a sharing mode that
    * conflicts with the access mode that is specified in an existing request that has an open handle. <code>CreateFile</code>
    * would fail and the {@link Native#getLastError()} function would return {@link WinError#ERROR_SHARING_VIOLATION}.
    * To enable a process to share a file or device while another process has the file or device open, use a compatible
    * combination of one or more of the {@link SharingMode}s. The sharing options for each open handle remain in effect
    * until that handle is closed, regardless of process context.
    *
    * @param securityAttributes A pointer to a {@link SECURITY_ATTRIBUTES} structure that contains two separate but
    * related data members: an optional security descriptor, and a boolean value that determines whether the returned
    * handle can be inherited by child processes. This parameter can be <code>null</code>. If this parameter is
    * <code>null</code>, the handle returned by <code>CreateFile</code> cannot be inherited by any child processes the
    * application may create and the file or device associated with the returned handle gets a default security
    * descriptor.
    *
    * @param creationDisposition An action to take on a file or device that exists or does not exist. For devices other
    * than files, this parameter is usually set to {@link CreationDisposition#OPEN_EXISTING}. This parameter must be one
    * of the {@link CreationDisposition} values, which cannot be combined.
    *
    * @param flagsAndAttributes The file or device attributes and flags (see {@link FlagsAndAttributes}).  See
    * <a href="http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx</a>.
    *
    * @param templateFile A valid handle to a template file with the {@link DesiredAccess#GENERIC_READ} access right.
    * The template file supplies file attributes and extended attributes for the file that is being created. This
    * parameter can be <code>null</code>. When opening an existing file, <code>CreateFile</code> ignores this parameter.
    * When opening a new encrypted file, the file inherits the discretionary access control list from its parent
    * directory.
    *
    * @return If the function succeeds, the return value is an open handle to the specified file, device, named pipe, or
    * mail slot. If the function fails, the return value is INVALID_HANDLE_VALUE. To get extended error information,
    * call {@link Native#getLastError}.
    */
   PointerByReference CreateFileW(final WString filename,
                                  final int desiredAccess,
                                  final int shareMode,
                                  final SECURITY_ATTRIBUTES securityAttributes,
                                  final int creationDisposition,
                                  final int flagsAndAttributes,
                                  final PointerByReference templateFile);

   /**
    * <p>
    * Creates or opens a file or I/O device. The most commonly used I/O devices are as follows: file, file stream,
    * directory, physical disk, volume, console buffer, tape drive, communications resource, mailslot, and pipe. The
    * function returns a pointer that can be used to access the file or device for various types of I/O depending on the
    * file or device and the flags and attributes specified.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx</a>.
    * </p>
    *
    * @param filename The name of the file or device to be created or opened.
    *
    * @param desiredAccess The requested access to the file or device, which can be summarized as read, write, both or
    * neither (zero). The most commonly used values are {@link DesiredAccess#GENERIC_READ},
    * {@link DesiredAccess#GENERIC_WRITE}, or both <code>({@link DesiredAccess#GENERIC_READ} |
    * {@link DesiredAccess#GENERIC_WRITE})</code>.  If this parameter is zero, the application can query certain
    * metadata such as file, directory, or device attributes without accessing that file or device, even if
    * {@link DesiredAccess#GENERIC_READ} access would have been denied. You cannot request an access mode that conflicts
    * with the sharing mode that is specified by the <code>shareMode</code> parameter in an open request that already
    * has an open handle.
    *
    * @param shareMode The requested sharing mode of the file or device, which can be read, write, both, delete, all of
    * these, or none (see {@link SharingMode}). Access requests to attributes or extended attributes are not affected by
    * this flag. If this parameter is zero and <code>CreateFile</code> succeeds, the file or device cannot be shared and
    * cannot be opened again until the handle to the file or device is closed. You cannot request a sharing mode that
    * conflicts with the access mode that is specified in an existing request that has an open handle. <code>CreateFile</code>
    * would fail and the {@link Native#getLastError()} function would return {@link WinError#ERROR_SHARING_VIOLATION}.
    * To enable a process to share a file or device while another process has the file or device open, use a compatible
    * combination of one or more of the {@link SharingMode}s. The sharing options for each open handle remain in effect
    * until that handle is closed, regardless of process context.
    *
    * @param securityAttributes A pointer to a {@link SECURITY_ATTRIBUTES} structure that contains two separate but
    * related data members: an optional security descriptor, and a boolean value that determines whether the returned
    * handle can be inherited by child processes. This parameter can be <code>null</code>. If this parameter is
    * <code>null</code>, the handle returned by <code>CreateFile</code> cannot be inherited by any child processes the
    * application may create and the file or device associated with the returned handle gets a default security
    * descriptor.
    *
    * @param creationDisposition An action to take on a file or device that exists or does not exist. For devices other
    * than files, this parameter is usually set to {@link CreationDisposition#OPEN_EXISTING}. This parameter must be one
    * of the {@link CreationDisposition} values, which cannot be combined.
    *
    * @param flagsAndAttributes The file or device attributes and flags (see {@link FlagsAndAttributes}).  See
    * <a href="http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa363858(VS.85).aspx</a>.
    *
    * @param templateFile A valid handle to a template file with the {@link DesiredAccess#GENERIC_READ} access right.
    * The template file supplies file attributes and extended attributes for the file that is being created. This
    * parameter can be <code>null</code>. When opening an existing file, <code>CreateFile</code> ignores this parameter.
    * When opening a new encrypted file, the file inherits the discretionary access control list from its parent
    * directory.
    *
    * @return If the function succeeds, the return value is an open handle to the specified file, device, named pipe, or
    * mail slot. If the function fails, the return value is INVALID_HANDLE_VALUE. To get extended error information,
    * call {@link Native#getLastError}.
    */
   PointerByReference CreateFileA(final String filename,
                                  final int desiredAccess,
                                  final int shareMode,
                                  final SECURITY_ATTRIBUTES securityAttributes,
                                  final int creationDisposition,
                                  final int flagsAndAttributes,
                                  final PointerByReference templateFile);

   /**
    * <p>
    * Reads data from the specified file or input/output (I/O) device. Reads occur at the position specified by the file
    * pointer if supported by the device. This function is designed for both synchronous and asynchronous operations.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/aa365467(VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa365467(VS.85).aspx</a>.
    * </p>
    * @param handle A handle to the device (for example, a file, file stream, physical disk, volume, console buffer,
    * tape drive, socket, communications resource, mailslot, or pipe). The <code>handle</code> parameter must have been
    * created with read access.
    *
    * @param buffer A pointer to the buffer that receives the data read from a file or device. This buffer must remain
    * valid for the duration of the read operation. The caller must not use this buffer until the read operation is
    * completed.
    *
    * @param bytesToRead The maximum number of bytes to be read.
    *
    * @param bytesRead A pointer to the variable that receives the number of bytes read when using a synchronous
    * <code>handle</code> parameter. <code>ReadFile</code> sets this value to zero before doing any work or error
    * checking. Use <code>null</code> for this parameter if this is an asynchronous operation to avoid potentially
    * erroneous results. This parameter can be <code>null</code> only when the <code>overlapped</code> parameter is not
    * <code>null</code>.
    *
    * @param overlapped A pointer to an OVERLAPPED structure is required if the <code>handle</code> parameter was opened
    * with FILE_FLAG_OVERLAPPED, otherwise it can be <code>null</code>.
    *
    * @return <code>true</code> upon success; <code>false</code> otherwise
    */
   boolean ReadFile(PointerByReference handle,
                    byte[] buffer,
                    int bytesToRead,
                    IntByReference bytesRead,
                    IntByReference overlapped);

   /**
    * <p>
    * Writes data to the specified file or input/output (I/O) device. This function is designed for both synchronous and
    * asynchronous operation.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/aa365747(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa365747(v=VS.85).aspx</a>.
    * </p>
    *
    * @param handle A handle to the file or I/O device (for example, a file, file stream, physical disk, volume, console
    * buffer, tape drive, socket, communications resource, mailslot, or pipe). The <code>handle</code> parameter must
    * have been created with the write access.
    *
    * @param buffer A pointer to the buffer containing the data to be written to the file or device. This buffer must
    * remain valid for the duration of the write operation. The caller must not use this buffer until the write
    * operation is completed.
    *
    * @param bytesToWrite The number of bytes to be written to the file or device. A value of zero specifies a null
    * write operation. The behavior of a null write operation depends on the underlying file system or communications
    * technology.
    *
    * @param bytesWritten A pointer to the variable that receives the number of bytes written when using a synchronous
    * <code>handle</code> parameter. WriteFile sets this value to zero before doing any work or error checking. Use
    * <code>null</code> for this parameter if this is an asynchronous operation to avoid potentially erroneous results.
    * This parameter can be <code>null</code> only when the lpOverlapped parameter is not <code>null</code>.
    *
    * @param overlapped
    */
   boolean WriteFile(PointerByReference handle,
                     byte[] buffer,
                     int bytesToWrite,
                     IntByReference bytesWritten,
                     IntByReference overlapped);

   /**
    * <p>
    * Closes an open object handle.
    * </p>
    * <p>
    * For more information, see <a href="http://msdn.microsoft.com/en-us/library/ms724211(v=VS.85).aspx">http://msdn.microsoft.com/en-us/library/ms724211(v=VS.85).aspx</a>.
    * </p>
    *
    * @param handle a valid handle to an open object
    *
    * @return <code>true</code> upon success; <code>false</code> otherwise
    */
   boolean CloseHandle(final PointerByReference handle);
   }
