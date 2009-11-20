package edu.cmu.ri.createlab.serial.config;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialIOConfiguration
   {
   private final String portDeviceName;
   private final BaudRate baudRate;
   private final CharacterSize characterSize;
   private final Parity parity;
   private final StopBits stopBits;
   private final FlowControl flowControl;

   public SerialIOConfiguration(final String portDeviceName,
                                final BaudRate baudRate,
                                final CharacterSize characterSize,
                                final Parity parity,
                                final StopBits stopBits,
                                final FlowControl flowControl)
      {
      if (portDeviceName == null)
         {
         throw new IllegalArgumentException("Port device name cannot be null");
         }
      if (baudRate == null)
         {
         throw new IllegalArgumentException("BaudRate cannot be null");
         }
      if (characterSize == null)
         {
         throw new IllegalArgumentException("CharacterSize cannot be null");
         }
      if (parity == null)
         {
         throw new IllegalArgumentException("Parity cannot be null");
         }
      if (stopBits == null)
         {
         throw new IllegalArgumentException("StopBits cannot be null");
         }
      if (flowControl == null)
         {
         throw new IllegalArgumentException("FlowControl cannot be null");
         }
      this.portDeviceName = portDeviceName;
      this.baudRate = baudRate;
      this.characterSize = characterSize;
      this.parity = parity;
      this.stopBits = stopBits;
      this.flowControl = flowControl;
      }

   public String getPortDeviceName()
      {
      return portDeviceName;
      }

   public BaudRate getBaudRate()
      {
      return baudRate;
      }

   public CharacterSize getCharacterSize()
      {
      return characterSize;
      }

   public Parity getParity()
      {
      return parity;
      }

   public StopBits getStopBits()
      {
      return stopBits;
      }

   public FlowControl getFlowControl()
      {
      return flowControl;
      }

   @SuppressWarnings({"RedundantIfStatement"})
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

      final SerialIOConfiguration that = (SerialIOConfiguration)o;

      if (baudRate != that.baudRate)
         {
         return false;
         }
      if (characterSize != that.characterSize)
         {
         return false;
         }
      if (flowControl != that.flowControl)
         {
         return false;
         }
      if (parity != that.parity)
         {
         return false;
         }
      if (!portDeviceName.equals(that.portDeviceName))
         {
         return false;
         }
      if (stopBits != that.stopBits)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = portDeviceName.hashCode();
      result = 31 * result + baudRate.hashCode();
      result = 31 * result + characterSize.hashCode();
      result = 31 * result + parity.hashCode();
      result = 31 * result + stopBits.hashCode();
      result = 31 * result + flowControl.hashCode();
      return result;
      }

   public String toString()
      {
      return "SerialIOConfiguration{" +
             "portDeviceName='" + portDeviceName + '\'' +
             ", baudRate=" + baudRate +
             ", characterSize=" + characterSize +
             ", parity=" + parity +
             ", stopBits=" + stopBits +
             ", flowControl=" + flowControl +
             '}';
      }
   }
