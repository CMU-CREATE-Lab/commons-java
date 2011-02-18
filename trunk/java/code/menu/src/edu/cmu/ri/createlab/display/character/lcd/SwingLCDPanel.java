package edu.cmu.ri.createlab.display.character.lcd;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.display.character.CharacterDisplay;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SwingLCDPanel implements CharacterDisplay
   {
   private static final String LCD_SCROLL_UP_ARROW = "^ ";
   private static final String LCD_SCROLL_DOWN_ARROW = "v ";
   private static final int LCD_WIDTH_OF_SCROLL_ARROW_AND_PADDING = Math.max(LCD_SCROLL_UP_ARROW.length(), LCD_SCROLL_DOWN_ARROW.length());
   private static final String LCD_PADDING_FOR_LINES_WITHOUT_ARROWS_WHEN_IN_SCROLLING_MODE = padRight("", LCD_WIDTH_OF_SCROLL_ARROW_AND_PADDING);

   private static final String FONT_NAME = "Monaco";
   private static final Font FONT_LARGE = new Font(FONT_NAME, 0, 24);

   // taken from http://stackoverflow.com/questions/388461/padding-strings-in-java
   private static String padRight(final String s, final int n)
      {
      return String.format("%1$-" + n + "s", s);
      }

   private final int numRows;
   private final int numColumns;
   private final int totalCharacterCount;
   private final int numColumnsWhenInScrollingMode;

   private final JLabel[][] lcdCells;
   private final JPanel panel = new JPanel();

   public SwingLCDPanel(final int rows, final int columns)
      {
      numRows = rows;
      numColumns = columns;
      totalCharacterCount = numRows * numColumns;
      numColumnsWhenInScrollingMode = numColumns - LCD_WIDTH_OF_SCROLL_ARROW_AND_PADDING;

      lcdCells = new JLabel[numRows][numColumns];

      // create the LCDDemo cells
      for (int i = 0; i < numRows; i++)
         {
         for (int j = 0; j < numColumns; j++)
            {
            lcdCells[i][j] = new JLabel(" ");
            lcdCells[i][j].setFont(FONT_LARGE);
            lcdCells[i][j].setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
         }

      // layout the LCDDemo panel
      final JPanel lcdScreenPanel = new JPanel();
      final GroupLayout lcdScreenGroupLayout = new GroupLayout(lcdScreenPanel);
      lcdScreenPanel.setLayout(lcdScreenGroupLayout);
      lcdScreenPanel.setBackground(Color.WHITE);
      lcdScreenPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                                                                  BorderFactory.createEmptyBorder(1, 1, 1, 1)));

      final GroupLayout.SequentialGroup[] rowSequentialGroups = new GroupLayout.SequentialGroup[numRows];
      final GroupLayout.ParallelGroup[] rowParallelGroups = new GroupLayout.ParallelGroup[numRows];
      final GroupLayout.ParallelGroup horizontalGroup = lcdScreenGroupLayout.createParallelGroup(GroupLayout.LEADING);
      final GroupLayout.SequentialGroup verticalGroup = lcdScreenGroupLayout.createSequentialGroup();

      for (int i = 0; i < numRows; i++)
         {
         rowSequentialGroups[i] = lcdScreenGroupLayout.createSequentialGroup();
         rowParallelGroups[i] = lcdScreenGroupLayout.createParallelGroup(GroupLayout.LEADING);
         for (int j = 0; j < numColumns; j++)
            {
            rowSequentialGroups[i].add(lcdCells[i][j]);
            rowParallelGroups[i].add(lcdCells[i][j]);
            }
         horizontalGroup.add(rowSequentialGroups[i]);
         verticalGroup.add(rowParallelGroups[i]);
         }

      lcdScreenGroupLayout.setHorizontalGroup(horizontalGroup);
      lcdScreenGroupLayout.setVerticalGroup(verticalGroup);

      // ---------------------------------------------------------------------------------------------------------------

      panel.add(lcdScreenPanel);
      panel.setBackground(Color.WHITE);
      }

   public JComponent getComponent()
      {
      return panel;
      }

   public void setEnabled(final boolean isEnabled)
      {
      for (int i = 0; i < numRows; i++)
         {
         for (int j = 0; j < numColumns; j++)
            {
            lcdCells[i][j].setEnabled(isEnabled);
            }
         }
      }

   public int getRows()
      {
      return numRows;
      }

   public int getColumns()
      {
      return numColumns;
      }

   public void setText(final String text)
      {
      setText(text, true);
      }

   public void setText(final String text, final boolean willClearFirst)
      {
      if (text != null && text.length() > 0)
         {
         if (willClearFirst)
            {
            clear();
            }

         for (int charIndex = 0; charIndex < Math.min(text.length(), totalCharacterCount); charIndex++)
            {
            final int row = charIndex / numColumns;
            final int col = charIndex % numColumns;
            final char character = text.charAt(charIndex);
            setCharacter(row, col, character);
            }
         }
      }

   public void setTextWithScrollArrows(final String text)
      {
      if (text != null && text.length() > 0)
         {
         String theText = text;

         final StringBuilder textWithScrollArrows = new StringBuilder();
         for (int line = 0; line < numRows; line++)
            {
            // chop off at most the first numColumnsWhenInScrollingMode characters
            String lineText = theText.substring(0, Math.min(numColumnsWhenInScrollingMode, theText.length()));

            // update the remainder
            theText = theText.substring(lineText.length());

            if (theText.length() == 0)
               {
               lineText = padRight(lineText, numColumnsWhenInScrollingMode);
               }

            if (line == 0)
               {
               textWithScrollArrows.append(LCD_SCROLL_UP_ARROW).append(lineText);
               }
            else if (line == numRows - 1)
               {
               textWithScrollArrows.append(LCD_SCROLL_DOWN_ARROW).append(lineText);
               }
            else
               {
               textWithScrollArrows.append(LCD_PADDING_FOR_LINES_WITHOUT_ARROWS_WHEN_IN_SCROLLING_MODE).append(lineText);
               }
            }
         setText(textWithScrollArrows.toString());
         }
      }

   public void setLine(final int lineNumber, final String text)
      {
      setLine(lineNumber, text, true);
      }

   public void setLine(final int lineNumber, final String text, final boolean willClearLineFirst)
      {
      if (isValidRow(lineNumber))
         {
         if (willClearLineFirst)
            {
            clearLine(lineNumber);
            }
         if (text != null && text.length() > 0)
            {
            for (int charIndex = 0; charIndex < Math.min(text.length(), numColumns); charIndex++)
               {
               setCharacter(lineNumber, charIndex, text.charAt(charIndex));
               }
            }
         }
      }

   public void setCharacter(final int row, final int col, final char character)
      {
      setCharacter(row, col, String.valueOf(character));
      }

   public void setCharacter(final int row, final int col, final String character)
      {
      if (isValidRow(row) && isValidColumn(col))
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  lcdCells[row][col].setText(character);
                  }
               });
         }
      }

   public void clear()
      {
      for (int row = 0; row < numRows; row++)
         {
         for (int col = 0; col < numColumns; col++)
            {
            setCharacter(row, col, " ");
            }
         }
      }

   public void clearLine(final int lineNumber)
      {
      if (isValidRow(lineNumber))
         {
         for (int col = 0; col < numColumns; col++)
            {
            setCharacter(lineNumber, col, " ");
            }
         }
      }

   private boolean isValidColumn(final int col)
      {
      return (col >= 0 && col < numColumns);
      }

   private boolean isValidRow(final int row)
      {
      return (row >= 0 && row < numRows);
      }
   }
