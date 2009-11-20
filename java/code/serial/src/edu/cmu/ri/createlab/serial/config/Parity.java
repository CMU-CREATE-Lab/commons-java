package edu.cmu.ri.createlab.serial.config;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum Parity
   {
      NONE("None"),
      EVEN("Even"),
      ODD("Odd");

   private final String name;

   Parity(final String name)
      {
      this.name = name;
      }

   public String getName()
      {
      return name;
      }

   public String toString()
      {
      return name;
      }
   }
