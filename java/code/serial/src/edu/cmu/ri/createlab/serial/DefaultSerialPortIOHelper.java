package edu.cmu.ri.createlab.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultSerialPortIOHelper implements SerialPortIOHelper
   {
   private static final Log LOG = LogFactory.getLog(DefaultSerialPortIOHelper.class);

   private final InputStream in;
   private final OutputStream out;

   public DefaultSerialPortIOHelper(final InputStream in, final OutputStream out)
      {
      this.in = in;
      this.out = out;
      }

   public int available() throws IOException
      {
      return in.available();
      }

   public boolean isDataAvailable() throws IOException
      {
      return in.available() > 0;
      }

   public int read() throws IOException
      {
      return in.read();
      }

   public int read(final byte[] buffer) throws IOException
      {
      return in.read(buffer);
      }

   public void write(final byte[] data) throws IOException
      {
      try
         {
         out.write(data);
         out.flush();
         }
      catch (IOException e)
         {
         LOG.error("Caught IOException while trying to write the data.  Rethrowing...", e);
         throw e;
         }
      catch (Exception e)
         {
         if (LOG.isErrorEnabled())
            {
            LOG.error("Caught " + e.getClass() + " while trying to write the data.  Rethrowing as an IOException.", e);
            }
         throw new IOException(e.getMessage());
         }
      }

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

      final DefaultSerialPortIOHelper that = (DefaultSerialPortIOHelper)o;

      if (in != null ? !in.equals(that.in) : that.in != null)
         {
         return false;
         }
      if (out != null ? !out.equals(that.out) : that.out != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = (in != null ? in.hashCode() : 0);
      result = 31 * result + (out != null ? out.hashCode() : 0);
      return result;
      }
   }
