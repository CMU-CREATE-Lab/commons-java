package edu.cmu.ri.createlab.display.character.menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.display.character.CharacterDisplay;
import edu.cmu.ri.createlab.menu.DefaultMenuItemAction;
import edu.cmu.ri.createlab.menu.MenuItem;
import edu.cmu.ri.createlab.menu.MenuStatusManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class CharacterDisplayMenuItemAction extends DefaultMenuItemAction
   {
   private final CharacterDisplay characterDisplay;
   private final Map<String, String> properties;

   public CharacterDisplayMenuItemAction(final MenuItem menuItem,
                                         final MenuStatusManager menuStatusManager,
                                         final CharacterDisplay characterDisplay)
      {
      this(menuItem, menuStatusManager, characterDisplay, null);
      }

   public CharacterDisplayMenuItemAction(final MenuItem menuItem,
                                         final MenuStatusManager menuStatusManager,
                                         final CharacterDisplay characterDisplay,
                                         final Map<String, String> properties)
      {
      super(menuItem, menuStatusManager);
      this.characterDisplay = characterDisplay;

      if (properties == null)
         {
         this.properties = Collections.emptyMap();
         }
      else
         {
         final Map<String, String> tempProperties = new HashMap<String, String>(properties.size());
         for (final String key : properties.keySet())
            {
            tempProperties.put(key, properties.get(key));
            }
         this.properties = Collections.unmodifiableMap(tempProperties);
         }
      }

   protected final CharacterDisplay getCharacterDisplay()
      {
      return characterDisplay;
      }

   protected final Map<String, String> getProperties()
      {
      return properties;
      }

   protected final String getProperty(final String key)
      {
      return properties.get(key);
      }

   protected final String getProperty(final String key, final String defaultValue)
      {
      final String value = properties.get(key);
      return (value == null) ? defaultValue : value;
      }

   public void activate()
      {
      if (getMenuItem().hasSiblings())
         {
         characterDisplay.setTextWithScrollArrows(getMenuItem().getText());
         }
      else
         {
         characterDisplay.setText(getMenuItem().getText());
         }
      }
   }