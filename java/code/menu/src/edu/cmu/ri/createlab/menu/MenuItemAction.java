package edu.cmu.ri.createlab.menu;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface MenuItemAction
   {
   void activate();

   void deactivate();

   void start();

   void stop();

   void upEvent();

   void rightEvent();

   void downEvent();

   void leftEvent();
   }