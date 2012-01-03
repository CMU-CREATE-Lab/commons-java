package edu.cmu.ri.createlab.usb.hid.hidapi;

import com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper;
import com.sun.jna.NativeLibrary;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>NativeLibraryVersionChooser</code> helps determine which version of a native library should be loaded.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class NativeLibraryVersionChooser
   {
   private static final Logger LOG = Logger.getLogger(NativeLibraryVersionChooser.class);

   @SuppressWarnings({"ErrorNotRethrown"})
   public static String getLibraryName(final String nameOf32BitLibrary, final String nameOf64BitLibrary)
      {
      final String archDataModelPropertyValue = System.getProperty("sun.arch.data.model", "");
      if (LOG.isDebugEnabled())
         {
         LOG.debug("NativeLibraryVersionChooser.getLibraryName(): sun.arch.data.model system property = [" + archDataModelPropertyValue + "]");
         }

      String libraryName;
      if ("32".equals(archDataModelPropertyValue))
         {
         try
            {
            tryToLoadLibrary(nameOf32BitLibrary);
            libraryName = nameOf32BitLibrary;
            }
         catch (UnsatisfiedLinkError ignored)
            {
            try
               {
               tryToLoadLibrary(nameOf64BitLibrary);
               libraryName = nameOf64BitLibrary;
               }
            catch (UnsatisfiedLinkError e)
               {
               LOG.error("NativeLibraryVersionChooser.getLibraryName(" + nameOf32BitLibrary + ", " + nameOf64BitLibrary + "): no compatible library found");
               throw e;
               }
            }
         }
      else
         {
         try
            {
            tryToLoadLibrary(nameOf64BitLibrary);
            libraryName = nameOf64BitLibrary;
            }
         catch (UnsatisfiedLinkError ignored)
            {
            try
               {
               tryToLoadLibrary(nameOf32BitLibrary);
               libraryName = nameOf32BitLibrary;
               }
            catch (UnsatisfiedLinkError e)
               {
               LOG.error("NativeLibraryVersionChooser.getLibraryName(" + nameOf32BitLibrary + ", " + nameOf64BitLibrary + "): no compatible library found");
               throw e;
               }
            }
         }

      if (LOG.isDebugEnabled())
         {
         LOG.debug("NativeLibraryVersionChooser.getLibraryName(): returning [" + libraryName + "]");
         }
      return libraryName;
      }

   @SuppressWarnings({"LoadLibraryWithNonConstantString"})
   private static void tryToLoadLibrary(final String libraryName) throws UnsatisfiedLinkError
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("NativeLibraryVersionChooser.tryToLoadLibrary(): calling NativeLibrary.getInstance(" + libraryName + ")...");
         }

      final NativeLibrary nativeLibraryInstance = NativeLibrary.getInstance(libraryName, MangledFunctionMapper.DEFAULT_OPTIONS);

      if (LOG.isDebugEnabled())
         {
         LOG.debug("NativeLibraryVersionChooser.tryToLoadLibrary(): NativeLibrary.getInstance(" + libraryName + ") = [" + nativeLibraryInstance + "]");
         }
      }

   private NativeLibraryVersionChooser()
      {
      // private to prevent instantiation
      }
   }
