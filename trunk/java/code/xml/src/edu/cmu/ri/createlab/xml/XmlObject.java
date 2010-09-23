package edu.cmu.ri.createlab.xml;

import org.jdom.Element;

/**
 * <p>
 * <code>XmlObject</code> provides base functionality for objects that can be serialized to and from XML.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class XmlObject implements XmlSerializable
   {
   private static final String DEFAULT_ELEMENT_NAME = "unknown";

   private final Element element;

   protected XmlObject()
      {
      this(new Element(DEFAULT_ELEMENT_NAME));
      }

   protected XmlObject(final Element element)
      {
      this.element = (Element)element.clone();
      }

   protected final Element getElement()
      {
      return element;
      }

   public final String toXmlString()
      {
      return XmlHelper.writeElementToString(getElement());
      }

   public final String toXmlStringFormatted()
      {
      return XmlHelper.writeElementToStringFormatted(getElement());
      }

   /** Returns a copy of the {@link Element} */
   public final Element toElement()
   {
   return (Element)element.clone();
   }
   }

