package edu.cmu.ri.createlab.rss.readers;

import java.util.ArrayList;
import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>NewsReader</code> accesses yahoo top stories.
 * </p>
 *
 * NewsReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information from the Yahoo news top stories.
 *
 * @author Daragh Egan
 */

public class NewsReader extends RSSReader
   {

   private String newsDesc;
   private int index;

   /**Construct NewsReader using the url of the news feed you wish to use
    *
    *@param url String url of the news feed you wish to use
    */

   public NewsReader(String url)
   {
   super(url);
   index = 0;
   updateNewsFeed();
   }

   /**Construct News Reader using the default news feed
    */

   public NewsReader()
   {
   super("http://rss.news.yahoo.com/rss/topstories");
   // Update the feed data on instantiation - this loads all of the feed's data into a Syndfeed object (see RSSReader class)
   index = 0;
   updateNewsFeed();
   }

   /**Updates all news data from website */
   public void updateNewsFeed()
   {
   updateFeed();
   // Update the feed data on instantiation - this loads all of the feed's data into a Syndfeed object (see RSSReader class)
   newsDesc = getEntryTitle(index);
   }

   /**
    *
    *@return number of articles listed in feed
    */
   public int getNewsCount()
   {
   return getEntryCount();
   }

   /**
    *
    *@return title of article at index
    */
   public String getTitle()
   {
   return newsDesc;
   }

   /**
    *@param keyword String keyword for search
    *@return ArrayList containing search results
    */
   public ArrayList<String> search(String keyword)
   {
   ArrayList<String> results = new ArrayList<String>();
   for (int i = 0; i < getEntryCount(); i++)
      {
      if (getEntryTitle(i).toLowerCase().contains(keyword.toLowerCase()))
         {
         results.add(getEntryTitle(i));
         }
      }

   return results;
   }

   /**
    *@param in int new index value
    */
   public void setIndex(int in)
   {
   updateNewsFeed();
   index = in;
   }
   }