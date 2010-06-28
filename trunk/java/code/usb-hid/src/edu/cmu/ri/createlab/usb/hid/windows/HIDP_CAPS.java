package edu.cmu.ri.createlab.usb.hid.windows;

import com.sun.jna.Structure;

/**
 * Code taken from http://code.google.com/p/jlsm/wiki/XP_Java_Reader
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDP_CAPS extends Structure
   {
   /** Top-level collection's usage identifier. */
   public short usage;

   /** Top-level collection's usage page. */
   public short usagePage;

   /** Maximum size, in bytes, of the input reports, including the report identifier, if report identifiers are used, which is added to the beginning of the report data. */
   public short inputReportByteLength;

   /** Maximum size, in bytes, of all the output reports, including the report identifier, if report identifiers are used, which is added to the beginning of the report data. */
   public short outputReportByteLength;

   /** Maximum length, in bytes, of all the feature reports, including the report identifier, if report identifiers are used, which is added to the beginning of the report data. */
   public short featureReportByteLength;

   /** Reserved for internal system use. */
   public short reserved[] = new short[17];

   /** Number of HIDP_LINK_COLLECTION_NODE structures that are returned for this top-level collection by HidP_GetLinkCollectionNodes. */
   public short numberLinkConnectionNodes;

   /** Number of input HIDP_BUTTON_CAPS structures that HidP_GetButtonCaps returns. */
   public short numberInputButtonCaps;

   /** Number of input HIDP_VALUE_CAPS structures that HidP_GetValueCaps returns. */
   public short numberInputValueCaps;

   /** Number of data indexes assigned to buttons and values in all input reports. */
   public short numberInputDataIndices;

   /** Number of output HIDP_BUTTON_CAPS structures that HidP_GetButtonCaps returns. */
   public short numberOutputButtonCaps;

   /** Number of output HIDP_VALUE_CAPS structures that HidP_GetValueCaps returns. */
   public short numberOutputValueCaps;

   /** Number of data indexes assigned to buttons and values in all output reports. */
   public short numberOutputDateIndices;

   /** Total number of feature HIDP_BUTTONS_CAPS structures that HidP_GetButtonCaps returns. */
   public short numberFeatureButtonCaps;

   /** Total number of feature HIDP_VALUE_CAPS structures that HidP_GetValueCaps returns. */
   public short numberFeatureValueCaps;

   /** Number of data indexes assigned to buttons and values in all feature reports. */
   public short numberFeatureDataIndices;
   }
