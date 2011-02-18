package edu.cmu.ri.createlab.display.character.menu;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import edu.cmu.ri.createlab.display.character.CharacterDisplay;
import edu.cmu.ri.createlab.menu.Menu;
import edu.cmu.ri.createlab.menu.MenuItem;
import edu.cmu.ri.createlab.menu.MenuItemAction;
import edu.cmu.ri.createlab.menu.MenuStatusManager;
import edu.cmu.ri.createlab.xml.LocalEntityResolver;
import edu.cmu.ri.createlab.xml.XmlHelper;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CharacterDisplayMenu implements Menu
   {
   private static final Logger LOG = Logger.getLogger(Menu.class);

   public static Menu create(final String menuConfigXml, final MenuStatusManager menuStatusManager, final CharacterDisplay characterDisplay)
      {
      try
         {
         XmlHelper.setLocalEntityResolver(LocalEntityResolver.getInstance());
         final Element menuElement = XmlHelper.createElement(CharacterDisplayMenu.class.getResourceAsStream(menuConfigXml));
         final MenuItem menuItem = buildMenuItemTree(menuElement, menuStatusManager, characterDisplay);
         return new CharacterDisplayMenu(menuItem, menuElement.getAttributeValue("welcome-text", ""));
         }
      catch (IOException e)
         {
         final String message = "IOException while trying to read menu config XML [" + menuConfigXml + "]";
         LOG.error(message, e);
         throw new IllegalArgumentException(message, e);
         }
      catch (JDOMException e)
         {
         final String message = "JDOMException while trying to read menu config XML [" + menuConfigXml + "]";
         LOG.error(message, e);
         throw new IllegalArgumentException(message, e);
         }
      }

   // Recursively builds the menu tree
   @SuppressWarnings({"ConstantConditions"})
   private static MenuItemImpl buildMenuItemTree(final Element parentElement, final MenuStatusManager menuStatusManager, final CharacterDisplay characterDisplay)
      {
      if (parentElement != null)
         {
         final String text = parentElement.getAttributeValue("text");

         final List childElements = parentElement.getChildren("item");

         if ((childElements != null) && (!childElements.isEmpty()))
            {
            final List<MenuItemImpl> children = new ArrayList<MenuItemImpl>();

            for (final Object o : childElements)
               {
               if (o != null)
                  {
                  final Element MenuElement = (Element)o;
                  children.add(buildMenuItemTree(MenuElement, menuStatusManager, characterDisplay));
                  }
               }

            // create the MenuItem
            final MenuItemImpl menuItem = new MenuItemImpl(text, children);

            // set the MenuItemAction
            menuItem.setMenuItemAction(new CharacterDisplayMenuItemAction(menuItem, menuStatusManager, characterDisplay));

            // set up the parent and sibling references for each of the children
            final int lastIndex = children.size() - 1;
            for (int menuIndex = 0; menuIndex < children.size(); menuIndex++)
               {
               final MenuItem previousSibling = children.get(menuIndex > 0 ? menuIndex - 1 : lastIndex);
               final MenuItem nextSibling = children.get(menuIndex < lastIndex ? menuIndex + 1 : 0);
               final MenuItemImpl child = children.get(menuIndex);
               child.setParent(menuItem);
               child.setPreviousSibling(previousSibling);
               child.setNextSibling(nextSibling);
               }

            return menuItem;
            }
         else
            {
            // create the MenuItem
            final MenuItemImpl menuItem = new MenuItemImpl(text);

            // create an instance of the MenuItemAction implementation class, or default to the CharacterDisplayMenuItemAction upon failure
            MenuItemAction menuItemAction = null;
            final Element implementationClassElement = parentElement.getChild("implementation-class");
            if (implementationClassElement != null)
               {
               // See if the <implementation-class> element has any <property> child elements.  If so, store them in a Map of Strings.
               final List propertyElements = implementationClassElement.getChildren("property");
               final Map<String, String> properties = new HashMap<String, String>(propertyElements.size());
               if ((propertyElements != null) && (!propertyElements.isEmpty()))
                  {
                  final ListIterator listIterator = propertyElements.listIterator();
                  while (listIterator.hasNext())
                     {
                     final Element propertyElement = (Element)listIterator.next();
                     final String key = propertyElement.getAttributeValue("key");
                     String value = propertyElement.getAttributeValue("value");

                     // The value will be null if the attribute isn't specified.  In
                     // that case, use the element's text contents instead.
                     if (value == null)
                        {
                        value = propertyElement.getText();
                        }
                     properties.put(key, value);
                     }
                  }

               final String menuItemActionImplementationClassName = implementationClassElement.getAttributeValue("name");
               if (menuItemActionImplementationClassName != null && menuItemActionImplementationClassName.length() > 0)
                  {
                  try
                     {
                     final Class clazz = Class.forName(menuItemActionImplementationClassName);
                     final Constructor constructor;
                     if (properties.isEmpty())
                        {
                        constructor = clazz.getConstructor(MenuItem.class, MenuStatusManager.class, CharacterDisplay.class);
                        if (constructor != null)
                           {
                           menuItemAction = (MenuItemAction)constructor.newInstance(menuItem, menuStatusManager, characterDisplay);
                           }
                        }
                     else
                        {
                        constructor = clazz.getConstructor(MenuItem.class, MenuStatusManager.class, CharacterDisplay.class, Map.class);
                        if (constructor != null)
                           {
                           menuItemAction = (MenuItemAction)constructor.newInstance(menuItem, menuStatusManager, characterDisplay, properties);
                           }
                        }
                     }
                  catch (ClassNotFoundException e)
                     {
                     LOG.error("Menu.buildMenuItemTree(): ClassNotFoundException while trying to find MenuItemAction implementation [" + menuItemActionImplementationClassName + "]", e);
                     }
                  catch (NoSuchMethodException e)
                     {
                     LOG.error("Menu.buildMenuItemTree(): NoSuchMethodException while trying to find the constructor accepting a single String for MenuItemAction implementation [" + menuItemActionImplementationClassName + "]", e);
                     }
                  catch (IllegalAccessException e)
                     {
                     LOG.error("Menu.buildMenuItemTree(): IllegalAccessException while trying to instantiate MenuItemAction implementation [" + menuItemActionImplementationClassName + "]", e);
                     }
                  catch (InvocationTargetException e)
                     {
                     LOG.error("Menu.buildMenuItemTree(): InvocationTargetException while trying to instantiate MenuItemAction implementation [" + menuItemActionImplementationClassName + "]", e);
                     }
                  catch (InstantiationException e)
                     {
                     LOG.error("Menu.buildMenuItemTree(): InstantiationException while trying to instantiate MenuItemAction implementation [" + menuItemActionImplementationClassName + "]", e);
                     }
                  catch (Exception e)
                     {
                     LOG.error("Menu.buildMenuItemTree(): Exception while trying to instantiate MenuItemAction implementation [" + menuItemActionImplementationClassName + "]", e);
                     }
                  }
               }

            if (menuItemAction == null)
               {
               LOG.info("Menu.buildMenuItemTree(): MenuItemAction implementation unspecified or invalid, defaulting to using CharacterDisplayMenuItemAction");
               menuItemAction = new CharacterDisplayMenuItemAction(menuItem, menuStatusManager, characterDisplay);
               }
            menuItem.setMenuItemAction(menuItemAction);

            return menuItem;
            }
         }

      return null;
      }

   private final MenuItem menuItem;
   private final String welcomeText;

   private CharacterDisplayMenu(final MenuItem menuItem)
      {
      this(menuItem, null);
      }

   private CharacterDisplayMenu(final MenuItem menuItem, final String welcomeText)
      {
      this.menuItem = menuItem;
      this.welcomeText = (welcomeText == null) ? "" : welcomeText;
      }

   public MenuItem getDefaultMenuItem()
      {
      return menuItem.getFirstChild();
      }

   public String getWelcomeText()
      {
      return welcomeText;
      }

   public boolean hasWelcomeText()
      {
      return !"".equals(welcomeText);
      }

   private static final class MenuItemImpl implements MenuItem
      {
      private final String text;
      private final List<MenuItem> children = new ArrayList<MenuItem>();
      private MenuItem parent = null;
      private MenuItem previousSibling = null;
      private MenuItem nextSibling = null;
      private MenuItemAction action = null;

      private MenuItemImpl(final String text)
         {
         this(text, null);
         }

      private MenuItemImpl(final String text, final List<MenuItemImpl> children)
         {
         this.text = text;
         if (children != null && !children.isEmpty())
            {
            for (final MenuItem item : children)
               {
               if (item != null)
                  {
                  this.children.add(item);
                  }
               }
            }
         if (LOG.isDebugEnabled())
            {
            LOG.debug("MenuItem(" + text + (children == null ? "" : ", " + children.size()) + "))");
            }
         }

      public String getText()
         {
         return text;
         }

      private void setMenuItemAction(final MenuItemAction action)
         {
         this.action = action;
         }

      private void setParent(final MenuItem parent)
         {
         this.parent = parent;
         }

      public MenuItem getParent()
         {
         return parent;
         }

      public MenuItem getFirstChild()
         {
         if (!children.isEmpty())
            {
            return children.get(0);
            }
         return null;
         }

      private void setPreviousSibling(final MenuItem previousSibling)
         {
         this.previousSibling = previousSibling;
         }

      public MenuItem getPreviousSibling()
         {
         return previousSibling;
         }

      private void setNextSibling(final MenuItem nextSibling)
         {
         this.nextSibling = nextSibling;
         }

      public MenuItem getNextSibling()
         {
         return nextSibling;
         }

      public boolean hasSiblings()
         {
         return (!this.previousSibling.equals(this) && !this.nextSibling.equals(this));
         }

      public boolean hasChildren()
         {
         return children.size() > 0;
         }

      public boolean isRoot()
         {
         return parent == null;
         }

      public MenuItemAction getMenuItemAction()
         {
         return action;
         }

      public String toString()
         {
         return "MenuItem{" +
                "text='" + text + '\'' +
                ", parent=" + (parent == null ? null : parent.getText()) +
                ", previousSibling=" + (previousSibling == null ? null : previousSibling.getText()) +
                ", nextSibling=" + (nextSibling == null ? null : nextSibling.getText()) +
                ", children=" + (children == null ? 0 : children.size()) +
                ", menuItemAction=" + (action == null ? null : action.getClass().getCanonicalName()) +
                '}';
         }
      }
   }
