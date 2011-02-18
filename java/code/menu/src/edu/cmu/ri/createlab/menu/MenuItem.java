package edu.cmu.ri.createlab.menu;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface MenuItem
   {
   String getText();

   MenuItem getParent();

   MenuItem getFirstChild();

   MenuItem getPreviousSibling();

   MenuItem getNextSibling();

   boolean hasSiblings();

   boolean hasChildren();

   boolean isRoot();

   MenuItemAction getMenuItemAction();
   }