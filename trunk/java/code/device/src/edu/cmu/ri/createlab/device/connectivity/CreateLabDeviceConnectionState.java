package edu.cmu.ri.createlab.device.connectivity;

/**
 * Defines the various possible connection states.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum CreateLabDeviceConnectionState
   {
      CONNECTED("Connected"),
      DISCONNECTED("Disconnected"),
      SCANNING("Scanning");

   private final String state;

   private CreateLabDeviceConnectionState(final String state)
      {
      this.state = state;
      }

   public String getStateName()
      {
      return state;
      }

   public String toString()
      {
      return "CreateLabDeviceConnectionState{" +
             "state='" + state + '\'' +
             '}';
      }
   }
