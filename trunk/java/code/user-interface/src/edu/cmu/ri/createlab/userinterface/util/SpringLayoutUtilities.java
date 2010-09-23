package edu.cmu.ri.createlab.userinterface.util;

import java.awt.Component;
import java.awt.Container;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import org.apache.log4j.Logger;

/**
 * <p>
 * Provides utility methods for creating form- or grid-style layouts with SpringLayout.
 * </p>
 * <p>
 * Based on the
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/layout/example-1dot4/SpringUtilities.java"><code>SpringUtilities</code></a>
 * class taken from <a href="http://java.sun.com/docs/books/tutorial/">Sun's Java Tutorial</a>
 * </p>
 */
public final class SpringLayoutUtilities
   {
   private static final Logger LOG = Logger.getLogger(SpringLayoutUtilities.class);

   /**
    * Aligns the first <code>rows</code> * <code>cols</code>
    * components of <code>parent</code> in
    * a grid. Each component is as big as the maximum
    * preferred width and height of the components.
    * The parent is made just big enough to fit them all.
    *
    * @param rows number of rows
    * @param cols number of columns
    * @param initialX x location to start the grid at
    * @param initialY y location to start the grid at
    * @param xPad x padding between cells
    * @param yPad y padding between cells
    */
   public static void makeGrid(final Container parent, final int rows, final int cols, final int initialX, final int initialY, final int xPad, final int yPad)
   {
   final SpringLayout layout;
   try
      {
      layout = (SpringLayout)parent.getLayout();
      }
   catch (final ClassCastException exc)
      {
      LOG.error("The first argument to makeGrid must use SpringLayout.");
      return;
      }

   final Spring xPadSpring = Spring.constant(xPad);
   final Spring yPadSpring = Spring.constant(yPad);
   final Spring initialXSpring = Spring.constant(initialX);
   final Spring initialYSpring = Spring.constant(initialY);
   final int max = rows * cols;

   //Calculate Springs that are the max of the width/height so that all
   //cells have the same size.
   Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
   Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
   for (int i = 1; i < max; i++)
      {
      final SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

      maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
      maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
      }

   //Apply the new width/height Spring. This forces all the
   //components to have the same size.
   for (int i = 0; i < max; i++)
      {
      final SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

      cons.setWidth(maxWidthSpring);
      cons.setHeight(maxHeightSpring);
      }

   //Then adjust the x/y constraints of all the cells so that they
   //are aligned in a grid.
   SpringLayout.Constraints lastCons = null;
   SpringLayout.Constraints lastRowCons = null;
   for (int i = 0; i < max; i++)
      {
      final SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
      if (i % cols == 0)
         {//start of new row
         lastRowCons = lastCons;
         cons.setX(initialXSpring);
         }
      else
         {//x position depends on previous component
         cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST), xPadSpring));
         }

      if (i / cols == 0)
         {//first row
         cons.setY(initialYSpring);
         }
      else
         {//y position depends on previous row
         cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH), yPadSpring));
         }
      lastCons = cons;
      }

   //Set the parent's size.
   final SpringLayout.Constraints pCons = layout.getConstraints(parent);
   pCons.setConstraint(SpringLayout.SOUTH, Spring.sum(Spring.constant(yPad), lastCons.getConstraint(SpringLayout.SOUTH)));
   pCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(xPad), lastCons.getConstraint(SpringLayout.EAST)));
   }

   /* Used by makeCompactGrid. */

   private static SpringLayout.Constraints getConstraintsForCell(final int row, final int col, final Container parent, final int cols)
      {
      final SpringLayout layout = (SpringLayout)parent.getLayout();
      final Component c = parent.getComponent(row * cols + col);
      return layout.getConstraints(c);
      }

   /**
    * Aligns the first <code>rows</code> * <code>cols</code>
    * components of <code>parent</code> in
    * a grid. Each component in a column is as wide as the maximum
    * preferred width of the components in that column;
    * height is similarly determined for each row.
    * The parent is made just big enough to fit them all.
    *
    * @param rows number of rows
    * @param cols number of columns
    * @param initialX x location to start the grid at
    * @param initialY y location to start the grid at
    * @param xPad x padding between cells
    * @param yPad y padding between cells
    */
   public static void makeCompactGrid(final Container parent, final int rows, final int cols, final int initialX, final int initialY, final int xPad, final int yPad)
   {
   final SpringLayout layout;
   try
      {
      layout = (SpringLayout)parent.getLayout();
      }
   catch (ClassCastException exc)
      {
      LOG.error("The first argument to makeCompactGrid must use SpringLayout.");
      return;
      }

   //Align all cells in each column and make them the same width.
   Spring x = Spring.constant(initialX);
   for (int c = 0; c < cols; c++)
      {
      Spring width = Spring.constant(0);
      for (int r = 0; r < rows; r++)
         {
         width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
         }
      for (int r = 0; r < rows; r++)
         {
         final SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
         constraints.setX(x);
         constraints.setWidth(width);
         }
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
      }

   //Align all cells in each row and make them the same height.
   Spring y = Spring.constant(initialY);
   for (int r = 0; r < rows; r++)
      {
      Spring height = Spring.constant(0);
      for (int c = 0; c < cols; c++)
         {
         height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
         }
      for (int c = 0; c < cols; c++)
         {
         final SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
         constraints.setY(y);
         constraints.setHeight(height);
         }
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
      }

   //Set the parent's size.
   final SpringLayout.Constraints pCons = layout.getConstraints(parent);
   pCons.setConstraint(SpringLayout.SOUTH, y);
   pCons.setConstraint(SpringLayout.EAST, x);
   }

   private SpringLayoutUtilities()
      {
      // private to prevent instantiation
      }
   }
