package edu.cmu.ri.createlab.menu;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface Menu
   {
   MenuItem getDefaultMenuItem();

   String getWelcomeText();

   boolean hasWelcomeText();
   }