package edu.cmu.ri.createlab.rss.readers;

import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>TuneReader</code> accesses the iTunes top 100 songs data.
 * </p>
 *
 * TuneReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information.
 *
 * @author Daragh Egan
 */

public class TuneReader extends RSSReader
   {
   private String title;
   private String song;
   private String artist;
   private int index;

   /*Construct TuneReader
     */
   public TuneReader()
      {
      super("http://ax.phobos.apple.com.edgesuite.net/WebObjects/MZStore.woa/wpa/MRSS/topsongs/sf=143441/explicit=false/limit=100/rss.xml");
      updateSongFeed();
      // Update the feed data on instantiation - this loads all of the feed's data into a Syndfeed object (see RSSReader class)
      index = 0;
      }

   /**Updates all song data from iTunes website */
   public void updateSongFeed()
      {
      updateFeed();
      title = getEntryTitle(index);
      parseSong();
      }

   /**
    *
    *@return title of entry at index
    */
   public String getTitle()
      {
      return title;
      }

   /**
    *
    *@return name of song at index
    */
   public String getSong()
      {
      return song;
      }

   /**
    *
    *return name of artist name at index
    */
   public String getArtist()
      {
      return artist;
      }

   /**
    *@param in int new index value
    */
   public void setIndex(int in)
      {
      index = in;
      updateSongFeed();
      }

   /**
    *
    *Stores data from String title into variables
    */
   private void parseSong()
      {
      int songIndex = title.indexOf(". ");
      int artistIndex = title.indexOf(" - ");

      song = title.substring(songIndex + 2, artistIndex);
      artist = title.substring(artistIndex + 3);
      }
   }

/*Sample

1. Beautiful Girls - Sean Kingston

*/