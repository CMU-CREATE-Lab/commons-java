package edu.cmu.ri.createlab.rss;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>RSSReader</code> can be used to generally access RSS feeds.
 * </p>
 *
 * @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 * @author Chris Bartley (bartley@cmu.edu)
 */

public class RSSReader
   {
   private static final Logger LOG = Logger.getLogger(RSSReader.class);

   private SyndFeedInput input = new SyndFeedInput();

   // Create a URL object to hold the location of the feed
   private URL feedUrl;

   // SyndFeed is the object which contains all feed data
   // When input.build is called, all feed information from the specified
   // URL is downloaded to the feed object.
   private SyndFeed feed;
   private List<FeedEntry> feedEntries = new ArrayList<FeedEntry>();

   /** Constructs the reader object by providing a URL which points to an RSS or Atom feed.
    *
    * @param URLFeed Holds the loction of the URL location of the RSS feed.
    *  For example, 'http://rss.news.yahoo.com/rss/topstories'
    */
   public RSSReader(final String URLFeed)
      {
      try
         {
         // Generate a new URL object based on the String - the String argument should be a URL
         feedUrl = new URL(URLFeed);
         }
      catch (Exception ex)
         {
         ex.printStackTrace();
         LOG.error("Exception while creating the RSS URL", ex);
         }
      // Update all of the local variables with the feed's information upon instantiation of this class
      updateFeed();
      }

   /**  Fetches the data from the web and updates all of the feed's data */
   public void updateFeed()
      {
      // Create a SyndFeed feed which contains all of the Xml page information
      try
         {
         feed = input.build(new XmlReader(feedUrl));

         // create the collection of entries
         final List entries = feed.getEntries();
         if ((entries != null) && (!entries.isEmpty()))
            {
            final List<FeedEntry> tempFeedEntries = new ArrayList<FeedEntry>(entries.size());
            final ListIterator listIterator = entries.listIterator();
            while (listIterator.hasNext())
               {
               final SyndEntry syndEntry = (SyndEntry)listIterator.next();
               tempFeedEntries.add(new FeedEntry(syndEntry.getAuthor(),
                                                 syndEntry.getTitle(),
                                                 syndEntry.getLink(),
                                                 (syndEntry.getDescription() == null) ? null : syndEntry.getDescription().getValue(),
                                                 syndEntry.getPublishedDate()));
               }
            feedEntries = tempFeedEntries;
            }
         else
            {
            feedEntries = new ArrayList<FeedEntry>();
            }
         }
      catch (Exception ex)
         {
         //ex.printStackTrace();
         //System.out.println("ERROR: " + ex.getMessage());
         LOG.error("Exception while updating the feed", ex);
         }
      }

   // The following methods provide ways to access data
   // regarding the entire feed.  If any of these calls are
   // for information which is not available, the methods will
   // return a NULL string.

   /** Returns the name of the first author of the feed as a String
    *
    * @return The author of the feed
    */
   public String getFeedAuthor()
      {
      return feed.getAuthor();
      }

   /** Returns the title of the feed as a String
    *
    * @return The title of the feed
    */
   public String getFeedTitle()
      {
      return feed.getTitle();
      }

   /** Returns the URL of the feed as a String
    *
    * @return The URL link of the feed
    */
   public String getFeedLink()
      {
      return feed.getLink();
      }

   /** Returns a description of the feed as a String
    *
    * @return The description of the feed
    */
   public String getFeedDescription()
      {
      return feed.getDescription();
      }

   /** Returns the date the feed was published as a Date object
    *
    * @return The date the feed was published
    */
   public Date getFeedDate(final int index)
      {
      return feed.getPublishedDate();
      }

   // The following methods relate to capturing information from the
   // individual entries in the feed.  If the information is not available,
   // the method will return a Null string.

   /** Returns the total number of feed entries as an int
    *
    * @return The number of feed entries
    */
   public int getEntryCount()
      {
      return feedEntries.size();
      }

   /**
    * Returns (a copy of) the entries in the feed (since the last update).
    *
    * @return a copy of the entries in the feed since the last update.
    */
   public List<FeedEntry> getEntries()
      {
      return new ArrayList<FeedEntry>(feedEntries);
      }

   /**
    * Returns the entries in the feed (since the last update) which were published after the given timestamp.  Returns
    * an empty {@link List} if no such entries are found.  Guaranteed to not return <code>null</code>.
    *
    * @return a copy of the entries in the feed since the last update which were published after the given timestamp.
    */
   public List<FeedEntry> getEntriesPublishedAfterTimestamp(final long timestamp)
      {
      final List<FeedEntry> newEntries = new ArrayList<FeedEntry>();
      for (final FeedEntry entry : feedEntries)
         {
         if (entry.getPublishedTimestamp() > timestamp)
            {
            newEntries.add(entry);
            }
         }
      return newEntries;
      }

   /** Returns the first author of the feed entry specified by index.
    *
    * @param index Specifies which entry to get data from
    * @return The first author of the feed entry
    * @throws IndexOutOfBoundsException if the given index is out of range
    */
   public String getEntryAuthor(final int index)
      {
      return feedEntries.get(index).getAuthor();
      }

   /** Returns the title of the feed entry specified by index.
    *
    * @param index Specifies which entry to get data from
    * @return The title of the feed entry
    * @throws IndexOutOfBoundsException if the given index is out of range
    */
   public String getEntryTitle(final int index)
      {
      return feedEntries.get(index).getTitle();
      }

   /** Returns the link to the feed entry specified by index.
    *
    * @param index Specifies which entry to get data from
    * @return The link of the feed entry
    * @throws IndexOutOfBoundsException if the given index is out of range
    */
   public String getEntryLink(final int index)
      {
      return feedEntries.get(index).getLink();
      }

   /** Returns the entry's text description specified by index.
    *
    * @param index Specifies which entry to get data from
    * @return The description of the feed entry
    * @throws IndexOutOfBoundsException if the given index is out of range
    */
   public String getEntryDescription(final int index)
      {
      return feedEntries.get(index).getDescription();
      }

   /** Get the date the entry was published specified by index.
    *
    * @param index Specifies which entry to get data from
    * @return The publication date of the feed entry
    * @throws IndexOutOfBoundsException if the given index is out of range
    */
   public Date getEntryDate(final int index)
      {
      return feedEntries.get(index).getPublishedTimestampAsDate();
      }
   }