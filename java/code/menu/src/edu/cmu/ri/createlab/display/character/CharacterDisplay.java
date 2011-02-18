package edu.cmu.ri.createlab.display.character;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CharacterDisplay
   {
   int getRows();

   int getColumns();

   void setText(final String text);

   void setText(final String text, final boolean willClearFirst);

   void setTextWithScrollArrows(final String text);

   void setLine(final int lineNumber, final String text);

   void setLine(final int lineNumber, final String text, final boolean willClearLineFirst);

   void setCharacter(final int row, final int col, final char character);

   void setCharacter(final int row, final int col, final String character);

   void clear();

   void clearLine(final int lineNumber);
   }