package edu.cmu.ri.createlab.usb.hid.windows;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class WinError
   {
   private static final Log LOG = LogFactory.getLog(WinError.class);
   private static final Map<Integer, WinError> ID_TO_WIN_ERROR_MAP = new HashMap<Integer, WinError>();

   public static WinError findById(final int id)
      {
      final WinError error = ID_TO_WIN_ERROR_MAP.get(id);
      if (error == null)
         {
         LOG.error("WinError.findById(): Failed to find error code [" + id + "], returning UNKNOWN.");
         return UNKNOWN;
         }
      return error;
      }

   private static WinError create(final int id, final String name)
      {
      final WinError error = new WinError(id, name);
      ID_TO_WIN_ERROR_MAP.put(error.getId(), error);
      return error;
      }

   public static final WinError UNKNOWN = create(-1, "Unknown: unknown status");
   public static final WinError SUCCESS = create(0, "Success: the operation completed successfully");
   public static final WinError ERROR_FILE_NOT_FOUND = create(2, "File Not Found: the system cannot find the file specified");
   public static final WinError ERROR_PATH_NOT_FOUND = create(3, "Path Not Found: the system cannot find the path specified");
   public static final WinError ERROR_ACCESS_DENIED = create(5, "Access Denied: access is denied");
   public static final WinError ERROR_INVALID_HANDLE = create(6, "Invalid Handle: the handle is invalid");
   public static final WinError ERROR_GENERAL_FAILURE = create(31, "General Failure: a device attached to the system is not functioning");
   public static final WinError ERROR_SHARING_VIOLATION = create(32, "Sharing Violation: the process cannot access the file because it is being used by another process");
   public static final WinError ERROR_FILE_EXISTS = create(80, "File Exists: the file exists");
   public static final WinError ERROR_INVALID_PARAMETER = create(87, "Invalid Parameter: the parameter is invalid");
   public static final WinError ERROR_INSUFFICIENT_BUFFER = create(122, "Insufficient Buffer: the data area passed to a system call is too small");
   public static final WinError ERROR_ALREADY_EXISTS = create(183, "Already Exists: cannot create a file when that file already exists");
   public static final WinError ERROR_NO_MORE_ITEMS = create(259, "No More Items: no more data is available");
   public static final WinError ERROR_DEVICE_NOT_CONNECTED = create(1167, "Device Not Connected: the device is not connected");
   public static final WinError ERROR_INVALID_USER_BUFFER = create(1784, "Invalid User Buffer: the supplied user buffer is not valid for the requested operation");

   private final int id;
   private final String name;

   private WinError(final int id, final String name)
      {
      this.id = id;
      this.name = name;
      }

   public int getId()
      {
      return id;
      }

   public String getName()
      {
      return name;
      }

   public boolean isSuccess()
      {
      return SUCCESS.equals(this);
      }

   @Override
   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final WinError winError = (WinError)o;

      if (id != winError.id)
         {
         return false;
         }
      if (name != null ? !name.equals(winError.name) : winError.name != null)
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = id;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      return result;
      }

   @Override
   public String toString()
      {
      final StringBuilder sb = new StringBuilder();
      sb.append("WinError");
      sb.append("{id=").append(id);
      sb.append(", name='").append(name).append('\'');
      sb.append('}');
      return sb.toString();
      }
   }