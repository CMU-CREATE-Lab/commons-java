package edu.cmu.ri.createlab.menu;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface MenuStatusManager
   {
   MenuItem getActiveMenuItem();

   void setActiveMenuItem(final MenuItem menuItem);

   void handleStartEvent();

   void handleStopEvent();

   void handleUpEvent();

   void handleRightEvent();

   void handleDownEvent();

   void handleLeftEvent();

   void handleAccessoryOneEvent();

   void handleAccessoryTwoEvent();

   void handleAccessoryThreeEvent();
   }