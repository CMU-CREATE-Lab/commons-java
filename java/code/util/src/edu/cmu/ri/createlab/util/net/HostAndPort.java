package edu.cmu.ri.createlab.util.net;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>HostAndPort</code> provides a simple way to extract a host and (optional) integer port from a string.  The host
 * and port must be separated by a single colon (with optional whitespace).
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HostAndPort implements Serializable
   {
   private static final Pattern LABEL_PATTERN = Pattern.compile("[a-zA-Z0-9]{1}(?:[a-zA-Z0-9_-]{0,61}[a-zA-Z0-9]{1})?");
   private static final Pattern HOST_AND_PORT_PATTERN = Pattern.compile("\\s*(" + LABEL_PATTERN + "(?:\\." + LABEL_PATTERN + ")*)\\s*(?:\\:\\s*\\d+)?\\s*");

   private final String host;
   private final String port;

   public static HostAndPort createHostAndPort(final String hostAndPort)
      {
      if (hostAndPort == null)
         {
         throw new IllegalArgumentException("Host and port string cannot be null!");
         }
      if (!isValid(hostAndPort))
         {
         return null;
         }

      // we're sure it matches now, so just split on the colon (if it's even present).
      final String[] hostAndPortArray = hostAndPort.split(":");

      // the host is the first element in the array, and the port, if present, will be the second
      final String host = hostAndPortArray[0].trim();
      final String port = (hostAndPortArray.length > 1 ? hostAndPortArray[1].trim() : null);

      return new HostAndPort(host, port);
      }

   /** Returns <code>true</code> if the given {@link String} is a valid host and port string; <code>false</code> otherwise. */
   public static boolean isValid(final String hostAndPort)
      {
      if (hostAndPort != null)
         {
         final Matcher m = HOST_AND_PORT_PATTERN.matcher(hostAndPort);
         return m.matches();
         }
      return false;
      }

   private HostAndPort(final String host, final String port)
      {
      this.port = port;
      this.host = host;
      }

   /**
    * Returns the host portion of the {@link String} used to create the <code>HostAndPort</code>. Guaranteed to not
    * return <code>null</code>.
    */
   public String getHost()
      {
      return host;
      }

   /**
    * Returns the port portion of the {@link String} used to create the <code>HostAndPort</code>. Returns
    * <code>null</code> if no port was specified.
    */
   public String getPort()
      {
      return (port == null) ? null : port;
      }

   /**
    * Convenience method which returns the host and port as a {@link String}, separated by a colon.  If there is no
    * port, only the host is returned.
    */
   public String getHostAndPort()
      {
      return host + ((port != null) ? ":" + port : "");
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

      final HostAndPort that = (HostAndPort)o;

      if (host != null ? !host.equals(that.host) : that.host != null)
         {
         return false;
         }
      if (port != null ? !port.equals(that.port) : that.port != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = (host != null ? host.hashCode() : 0);
      result = 31 * result + (port != null ? port.hashCode() : 0);
      return result;
      }

   /** Returns the same {@link String} as {@link #getHostAndPort()}. */
   public String toString()
      {
      return getHostAndPort();
      }
   }
