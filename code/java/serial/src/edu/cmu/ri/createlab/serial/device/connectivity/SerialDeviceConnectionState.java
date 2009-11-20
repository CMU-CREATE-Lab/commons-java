package edu.cmu.ri.createlab.serial.device.connectivity;

/**
 * Defines the various possible connection states.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum SerialDeviceConnectionState
   {
      CONNECTED("Connected"),
      DISCONNECTED("Disconnected"),
      SCANNING("Scanning");

   private final String state;

   private SerialDeviceConnectionState(final String state)
      {
      this.state = state;
      }

   public String getStateName()
      {
      return state;
      }

   public String toString()
      {
      return "SerialDeviceConnectionState{" +
             "state='" + state + '\'' +
             '}';
      }
   }
