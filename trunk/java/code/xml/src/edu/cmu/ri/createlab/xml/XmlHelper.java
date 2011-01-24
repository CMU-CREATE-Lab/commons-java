package edu.cmu.ri.createlab.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class XmlHelper
   {
   private static final XMLOutputter XML_OUTPUTTER = new XMLOutputter();
   private static final XMLOutputter XML_OUTPUTTER_FORMATTED = new XMLOutputter(Format.getPrettyFormat().setIndent("   "));
   private static final SAXBuilder VALIDATING_SAX_BUILDER = new SAXBuilder(true);
   private static final SAXBuilder NON_VALIDATING_SAX_BUILDER = new SAXBuilder(false);

   static
      {
      // Tell the builders to NOT reuse their parsers so they're thread safe
      VALIDATING_SAX_BUILDER.setReuseParser(false);
      NON_VALIDATING_SAX_BUILDER.setReuseParser(false);
      }

   /**
    * Writes a JDOM {@link Element} to XML with no formatting.
    *
    * @param element A JDOM {@link Element}
    * @return A {@link String} holding the XML.
    */
   public static String writeElementToString(final Element element)
      {
      return XML_OUTPUTTER.outputString(element);
      }

   /**
    * Writes a JDOM {@link Document} to XML with no formatting.
    *
    * @param document A JDOM {@link Document}
    * @return A {@link String} holding the XML.
    */
   public static String writeDocumentToString(final Document document)
      {
      return XML_OUTPUTTER.outputString(document);
      }

   /**
    * Writes a JDOM {@link Element} to XML with basic formatting.
    *
    * @param element A JDOM {@link Element}
    * @return A {@link String} holding the XML.
    */
   public static String writeElementToStringFormatted(final Element element)
      {
      return XML_OUTPUTTER_FORMATTED.outputString(element);
      }

   /**
    * Writes a JDOM {@link Document} to XML with basic formatting.
    *
    * @param document A JDOM {@link Document}
    * @return A {@link String} holding the XML.
    */
   public static String writeDocumentToStringFormatted(final Document document)
      {
      return XML_OUTPUTTER_FORMATTED.outputString(document);
      }

   public static Element createElement(final String xml) throws IOException, JDOMException
      {
      return VALIDATING_SAX_BUILDER.build(new StringReader(xml)).detachRootElement();
      }

   public static Element createElement(final InputStream inputStream) throws IOException, JDOMException
      {
      return VALIDATING_SAX_BUILDER.build(inputStream).detachRootElement();
      }

   public static Element createElementNoValidate(final File xmlFile) throws IOException, JDOMException
      {
      return NON_VALIDATING_SAX_BUILDER.build(xmlFile).detachRootElement();
      }

   public static Element createElementNoValidate(final InputStream inputStream) throws IOException, JDOMException
      {
      return NON_VALIDATING_SAX_BUILDER.build(inputStream).detachRootElement();
      }

   public static Document createDocument(final String xml) throws IOException, JDOMException
      {
      return VALIDATING_SAX_BUILDER.build(new StringReader(xml));
      }

   public static Document createDocument(final InputStream inputStream) throws IOException, JDOMException
      {
      return VALIDATING_SAX_BUILDER.build(inputStream);
      }

   public static Document createDocument(final File file) throws IOException, JDOMException
      {
      return VALIDATING_SAX_BUILDER.build(file);
      }

   public static Document createDocumentNoValidate(final String xml) throws IOException, JDOMException
      {
      return NON_VALIDATING_SAX_BUILDER.build(new StringReader(xml));
      }

   public static void writeDocToOutputStream(final Document doc, final Writer writer) throws IOException
      {
      XML_OUTPUTTER_FORMATTED.output(doc, writer);
      writer.flush();
      }

   public static void setLocalEntityResolver(final EntityResolver localEntityResolver)
      {
      // Tell the builder how to resolve local entities
      VALIDATING_SAX_BUILDER.setEntityResolver(localEntityResolver);
      NON_VALIDATING_SAX_BUILDER.setEntityResolver(localEntityResolver);
      }

   private XmlHelper()
      {
      // private to prevent instantiation
      }
   }

