package edu.cmu.ri.createlab.serial.config;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum CharacterSize
   {
      FIVE("5", 5),
      SIX("6", 6),
      SEVEN("7", 7),
      EIGHT("8", 8);

   private final String name;
   private final int value;

   CharacterSize(final String name, final int value)
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
