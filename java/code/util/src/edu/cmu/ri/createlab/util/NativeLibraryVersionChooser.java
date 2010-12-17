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

   @SuppressWarnings({"ErrorNotRethrown", "LoadLibraryWithNonConstantString"})
   public static String getLibraryName(final String nameOf32BitLibrary, final String nameOf64BitLibrary)
      {
      String libraryName;

      if ("32".equals(System.getProperty("sun.arch.data.model", "")))
         {
         try
            {
            System.loadLibrary(nameOf32BitLibrary);
            libraryName = nameOf32BitLibrary;
            }
         catch (UnsatisfiedLinkError ex32)
            {
            try
               {
               System.loadLibrary(nameOf64BitLibrary);
               libraryName = nameOf64BitLibrary;
               }
            catch (UnsatisfiedLinkError ignored)
               {
               throw ex32;
               }
            }
         }
      else
         {
         try
            {
            System.loadLibrary(nameOf64BitLibrary);
            libraryName = nameOf64BitLibrary;
            }
         catch (UnsatisfiedLinkError ex32)
            {
            try
               {
               System.loadLibrary(nameOf32BitLibrary);
               libraryName = nameOf32BitLibrary;
               }
            catch (UnsatisfiedLinkError ignored)
               {
               throw ex32;
               }
            }
         }

      if (LOG.isDebugEnabled())
         {
         LOG.debug("NativeLibraryVersionChooser.getLibraryName(): returning [" + libraryName + "]");
         }
      return libraryName;
      }

   private NativeLibraryVersionChooser()
      {
      // private to prevent instantiation
      }
   }
