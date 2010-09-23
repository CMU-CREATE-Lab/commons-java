package edu.cmu.ri.createlab.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class LocalEntityResolver implements EntityResolver
   {
   private static final Logger LOG = Logger.getLogger(LocalEntityResolver.class);

   public static final String DOCTYPE_PUBLIC_ID = "-//CREATE Lab//XML//Local Entity Resolver//EN";
   public static final String DOCTYPE_SYSTEM_ID = "http://www.createlab.ri.cmu.edu/dtd/xml/local-entity-resolver.dtd";
   public static final String PATH_TO_LOCAL_ENTITY_RESOLVER_XML = "/local-entity-resolver.xml";
   private static final String PATH_TO_LOCAL_ENTITY_RESOLVER_DTD = "/edu/cmu/ri/createlab/xml/local-entity-resolver.dtd";

   private static final LocalEntityResolver INSTANCE = new LocalEntityResolver();
   private final Map<String, String> publicIdToLocalFileMap = new HashMap<String, String>();

   public static LocalEntityResolver getInstance()
      {
      return INSTANCE;
      }

   private LocalEntityResolver()
      {
      // load the mappings defined in the XML file
      try
         {
         // use my own validating SAX builder instead of the one in XmlHelper because that one relies on this class.
         final SAXBuilder saxBuilder = new SAXBuilder(true);
         saxBuilder.setEntityResolver(new EntityResolver()
         {
         public InputSource resolveEntity(final String publicId, final String systemId)
            {
            return new InputSource(getClass().getResourceAsStream(PATH_TO_LOCAL_ENTITY_RESOLVER_DTD));
            }
         });

         final Document localEntityResolverDocument = saxBuilder.build(getClass().getResourceAsStream(PATH_TO_LOCAL_ENTITY_RESOLVER_XML));
         final Element rootElement = localEntityResolverDocument.getRootElement();
         final List dtdMappingElements = rootElement.getChild("dtd-mappings").getChildren("dtd-mapping");
         for (final ListIterator elementIterator = dtdMappingElements.listIterator(); elementIterator.hasNext();)
            {
            final Element dtdMappingElement = (Element)elementIterator.next();
            final String publicId = dtdMappingElement.getChildText("public-id");
            final String localFile = dtdMappingElement.getChildText("local-file");
            if ((publicId != null) && (localFile != null))
               {
               publicIdToLocalFileMap.put(publicId, localFile);
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("LocalEntityResolver stored DTD Mapping: [" + publicId + "] --> [" + localFile + "]");
                  }
               }
            }
         }
      catch (IOException e)
         {
         LOG.error("IOException while loading the local entity resolver DTD mappings", e);
         }
      catch (JDOMException e)
         {
         LOG.error("IOException while loading the local entity resolver DTD mappings", e);
         }
      }

   public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException
      {
      final String localPath = publicIdToLocalFileMap.get(publicId);
      if (localPath != null)
         {
         return new InputSource(getClass().getResourceAsStream(localPath));
         }

      // returning null asks the parser to open a regular URI connection to the system identifier
      return null;
      }
   }
