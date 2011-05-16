package edu.cmu.ri.createlab.util;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>NativeLibraryVersionChooser</code> determines whether the 32-bit or 64-bit version of the given library should
 * be used.
 * </p>
 * <p>
 * Code is based on the implementation of the <a href="http://users.frii.com/jarvi/rxtx/doc/gnu/io/RXTXVersion.html"><code>RXTXVersion.getVersion()</code></a> method.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class NativeLibraryVersionChooser
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
               LOG.error("NativeLibraryVersionChooser.getLibraryName(): no compatible library found");
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
               LOG.error("NativeLibraryVersionChooser.getLibraryName(): no compatible library found");
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
         LOG.debug("NativeLibraryVersionChooser.getLibraryName(): trying to load library [" + libraryName + "]...");
         }
      System.loadLibrary(libraryName);
      if (LOG.isDebugEnabled())
         {
         LOG.debug("NativeLibraryVersionChooser.getLibraryName(): successfully loaded library [" + libraryName + "]");
         }
      }

   private NativeLibraryVersionChooser()
      {
      // private to prevent instantiation
      }
   }
