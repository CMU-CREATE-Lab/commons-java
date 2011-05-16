package edu.cmu.ri.createlab.usb.hid;

/**
 * <p>
 * <code>HIDDeviceDescriptor</code> contains the properties for an HID Device to which you want to connect and
 * communicate with.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDDeviceDescriptor
   {
   private static final String UNDEFINED_COMMON_NAME = "Unnamed";

   private final short vendorId;
   private final short productId;
   private final int inputReportByteLength;
   private final int outputReportByteLength;
   private final String commonName;

   public HIDDeviceDescriptor(final short vendorId,
                              final short productId,
                              final int inputReportByteLength,
                              final int outputReportByteLength)
      {
      this(vendorId, productId, inputReportByteLength, outputReportByteLength, UNDEFINED_COMMON_NAME);
      }

   public HIDDeviceDescriptor(final short vendorId,
                              final short productId,
                              final int inputReportByteLength,
                              final int outputReportByteLength,
                              final String commonName)
      {
      this.vendorId = vendorId;
      this.productId = productId;
      this.inputReportByteLength = inputReportByteLength;
      this.outputReportByteLength = outputReportByteLength;
      this.commonName = commonName;
      }

   public short getVendorId()
      {
      return vendorId;
      }

   public short getProductId()
      {
      return productId;
      }

   public String getVendorIdAsHexString()
      {
      return Integer.toHexString(vendorId);
      }

   public String getProductIdAsHexString()
      {
      return Integer.toHexString(productId);
      }

   /** Returns the length of an input report, including the report ID byte. */
   public int getInputReportByteLength()
      {
      return inputReportByteLength;
      }

   /** Returns the length of an output report, including the report ID byte. */
   public int getOutputReportByteLength()
      {
      return outputReportByteLength;
      }

   public String getCommonName()
      {
      return commonName;
      }

   @Override
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

      final HIDDeviceDescriptor that = (HIDDeviceDescriptor)o;

      if (inputReportByteLength != that.inputReportByteLength)
         {
         return false;
         }
      if (outputReportByteLength != that.outputReportByteLength)
         {
         return false;
         }
      if (productId != that.productId)
         {
         return false;
         }
      if (vendorId != that.vendorId)
         {
         return false;
         }
      if (commonName != null ? !commonName.equals(that.commonName) : that.commonName != null)
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = (int)vendorId;
      result = 31 * result + (int)productId;
      result = 31 * result + inputReportByteLength;
      result = 31 * result + outputReportByteLength;
      result = 31 * result + (commonName != null ? commonName.hashCode() : 0);
      return result;
      }

   @Override
   public String toString()
      {
      final StringBuilder sb = new StringBuilder();
      sb.append("HIDDeviceDescriptor{");
      sb.append(commonName).append(',');
      sb.append(Integer.toHexString(vendorId)).append(',');
      sb.append(Integer.toHexString(productId)).append('}');
      return sb.toString();
      }
   }
