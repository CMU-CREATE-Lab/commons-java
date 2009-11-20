package edu.cmu.ri.createlab.serial.config;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum StopBits
   {
      ONE("1", 1),
      TWO("2", 2);

   private final String name;
   private final int value;

   StopBits(final String name, final int value)
      {
      this.name = name;
      this.value = value;
      }

   public String getName()
      {
      return name;
      }

   public int getValue()
      {
      return value;
      }

   public String toString()
      {
      return name;
      }
   }
