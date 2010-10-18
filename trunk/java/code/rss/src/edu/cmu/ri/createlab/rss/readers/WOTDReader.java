package edu.cmu.ri.createlab.rss.readers;

import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>WOTDReader</code> accesses the dictionary.com word of the day.
 * </p>
 *
 * WOTDReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information.
 *
 * @author Daragh Egan
 */

public class WOTDReader extends RSSReader
   {
   private String word;

   /**Construct WOTDReader */
   public WOTDReader()
   {
   super("http://dictionary.reference.com/wordoftheday/wotd.rss");
   updateWordFeed();
   }

   /**Updates data from Dictionary.com */
   public void updateWordFeed()
   {
   updateFeed();
   word = getEntryTitle(0);
   parseWordFeed();
   }

   /**
    *
    *@return the Word of the Day
    */
   public String getWord()
   {
   return word;
   }

   /**
    *
    *Gets the word from the entry title
    */
   private void parseWordFeed()
   {
   String tmp = word;
   word = tmp.substring(0, tmp.indexOf(": "));
   }
   }