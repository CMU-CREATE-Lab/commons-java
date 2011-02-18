package edu.cmu.ri.createlab.display.character.menu;

import java.util.Map;
import edu.cmu.ri.createlab.display.character.CharacterDisplay;
import edu.cmu.ri.createlab.menu.MenuItem;
import edu.cmu.ri.createlab.menu.MenuStatusManager;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>TwoOptionMenuItemAction</code> provides base functionality for all {@link CharacterDisplayMenuItemAction}s
 * which need to prompt the user with a choice between two options (and a third option to cancel altogether) where
 * different actions can be performed depending on the user's choice.
 * </p>
 * <p>
 * Users and subclasses should override the default prompt ("Choose Yes or No") and the default "Yes" and "No" choices
 * by constructing the instance with a {@link Map} containing keys <code>action.prompt</code>,
 * <code>choice.option1</code>, and <code>choice.option2</code>.  The values for those keys will be used instead of the
 * defaults.  The messages displayed upon the user choosing option1, option2, or cancel can be similarly customized by
 * specifying <code>action.option1</code>, <code>action.option2</code>, and <code>action.cancel</code>.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Paul Dille (pdille@andrew.cmu.edu)
 */
public abstract class TwoOptionMenuItemAction extends CharacterDisplayMenuItemAction
   {
   private static final Logger LOG = Logger.getLogger(TwoOptionMenuItemAction.class);

   private static final String DEFAULT_ACTION_PROMPT = "Choose Yes or No";
   private static final String DEFAULT_ACTION_CHOSE_OPTION1 = "You chose yes.";
   private static final String DEFAULT_ACTION_CHOSE_OPTION2 = "You chose no.";
   private static final String DEFAULT_ACTION_CHOSE_CANCEL = "Cancelled!";
   private static final String DEFAULT_LABEL_OPTION1 = "Yes  ";
   private static final String DEFAULT_LABEL_OPTION2 = "No";

   private static final String PROPERTY_CHOICE_OPTION1 = "choice.option1";
   private static final String PROPERTY_CHOICE_OPTION2 = "choice.option2";
   private static final String PROPERTY_ACTION_PROMPT = "action.prompt";
   private static final String PROPERTY_ACTION_CHOSE_OPTION1 = "action.option1";
   private static final String PROPERTY_ACTION_CHOSE_OPTION2 = "action.option2";
   private static final String PROPERTY_ACTION_CHOSE_CANCEL = "action.cancel";

   private boolean userChoseOption1 = false;
   private final int choiceRow;

   public TwoOptionMenuItemAction(final MenuItem menuItem,
                                  final MenuStatusManager menuStatusManager,
                                  final CharacterDisplay characterDisplay)
      {
      this(menuItem, menuStatusManager, characterDisplay, null);
      }

   public TwoOptionMenuItemAction(final MenuItem menuItem,
                                  final MenuStatusManager menuStatusManager,
                                  final CharacterDisplay characterDisplay,
                                  final Map<String, String> properties)
      {
      super(menuItem, menuStatusManager, characterDisplay, properties);

      // calculate which row the choice text will be on
      choiceRow = (int)Math.ceil((double)getPromptText().length() / (double)getCharacterDisplay().getColumns());
      }

   public final void activate()
      {
      userChoseOption1 = shouldOption1BeSelectedUponActivation();
      getCharacterDisplay().setText(getPromptText());
      if (choiceRow < getCharacterDisplay().getRows())
         {
         getCharacterDisplay().setLine(choiceRow, generateOptionChoiceLine());
         }
      }

   public final void start()
      {
      final String text;
      if (userChoseOption1)
         {
         executeOption1Action();
         text = getUserChoseOption1Text();
         }
      else
         {
         executeOption2Action();
         text = getUserChoseOption2Text();
         }
      getCharacterDisplay().setText(text);
      sleepThenPopUpToParentMenuItem();
      }

   public final void stop()
      {
      getCharacterDisplay().setText(getUserChoseCancelText());
      sleepThenPopUpToParentMenuItem();
      }

   public final void rightEvent()
      {
      leftEvent();
      }

   public final void leftEvent()
      {
      userChoseOption1 = !userChoseOption1;
      if (choiceRow < getCharacterDisplay().getRows())
         {
         getCharacterDisplay().setLine(choiceRow, generateOptionChoiceLine());
         }
      }

   public final void upEvent()
      {
      super.upEvent();
      }

   public final void downEvent()
      {
      super.downEvent();
      }

   protected abstract boolean shouldOption1BeSelectedUponActivation();

   protected abstract void executeOption1Action();

   protected abstract void executeOption2Action();

   private String getPromptText()
      {
      return getProperty(PROPERTY_ACTION_PROMPT, DEFAULT_ACTION_PROMPT);
      }

   private String getUserChoseOption1Text()
      {
      return getProperty(PROPERTY_ACTION_CHOSE_OPTION1, DEFAULT_ACTION_CHOSE_OPTION1);
      }

   private String getUserChoseOption2Text()
      {
      return getProperty(PROPERTY_ACTION_CHOSE_OPTION2, DEFAULT_ACTION_CHOSE_OPTION2);
      }

   private String getUserChoseCancelText()
      {
      return getProperty(PROPERTY_ACTION_CHOSE_CANCEL, DEFAULT_ACTION_CHOSE_CANCEL);
      }

   private String generateOptionChoiceLine()
      {
      return "[" + (userChoseOption1 ? "*" : " ") + "]" +
             getProperty(PROPERTY_CHOICE_OPTION1, DEFAULT_LABEL_OPTION1) +
             "[" + (userChoseOption1 ? " " : "*") + "]" +
             getProperty(PROPERTY_CHOICE_OPTION2, DEFAULT_LABEL_OPTION2);
      }

   private void sleepThenPopUpToParentMenuItem()
      {
      sleep();
      super.stop();
      }

   private void sleep()
      {
      try
         {
         Thread.sleep(2000);
         }
      catch (InterruptedException e)
         {
         LOG.error("TwoOptionMenuItemAction.sleep(): InterruptedException while sleeping", e);
         }
      }
   }
