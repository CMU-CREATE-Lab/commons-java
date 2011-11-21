package edu.cmu.ri.createlab.util;

/**
 * <p>
 * <code>VersionNumber</code> represents a version number with major, minor, and revision components.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface VersionNumber
   {
   String getMajorVersion();

   String getMinorVersion();

   String getRevision();
   }