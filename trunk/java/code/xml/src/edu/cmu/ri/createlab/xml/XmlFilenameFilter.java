package edu.cmu.ri.createlab.xml;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * <p>
 * <code>XmlFilenameFilter</code> is a {@link FilenameFilter} and {@link FileFilter} which accepts files ending with a
 * <code>.xml</code> extension (case insensitive).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class XmlFilenameFilter implements FilenameFilter, FileFilter
   {
   public static final String XML_EXTENSION = ".xml";

   @Override
   public boolean accept(final File file, final String name)
      {
      return name != null && name.toLowerCase().endsWith(XML_EXTENSION);
      }

   @Override
   public boolean accept(final File file)
      {
      return file != null && accept(file.getParentFile(), file.getName());
      }
   }
