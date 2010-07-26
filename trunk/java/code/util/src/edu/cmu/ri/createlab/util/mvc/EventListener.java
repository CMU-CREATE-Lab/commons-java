package edu.cmu.ri.createlab.util.mvc;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface EventListener<T>
   {
   void handleEvent(final T eventData);
   }