package edu.cmu.ri.createlab.util.commandexecution;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CommandResponse
   {
   boolean wasSuccessful();

   /** Returns a copy of the data as an array of bytes.  May return null. */
   byte[] getData();
   }