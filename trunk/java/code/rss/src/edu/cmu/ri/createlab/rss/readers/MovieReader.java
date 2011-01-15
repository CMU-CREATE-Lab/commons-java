package edu.cmu.ri.createlab.rss.readers;

import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>MovieReader</code> accesses rottentomatoes movie listings.
 * </p>
 *
 * MovieReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information from the Rottentomatoes RSS feed.
 *
 * @author Daragh Egan
 */

public class MovieReader extends RSSReader
   {
   private String title;
   private String movie;
   private int rating;
   private int index;

   /** Construct moviereader */
   public MovieReader()
      {
      super("http://i.rottentomatoes.com/syndication/rss/in_theaters.xml");
      updateMovieFeed();
      index = 0;
      }

   /** Updates the movie data from the web
    **/
   public void updateMovieFeed()
      {
      updateFeed();
      title = getEntryTitle(index);
      parseMovie();
      }

   /**
    *
    *@return The number of movies on the list
    */
   public int getMovieCount()
      {
      return getEntryCount();
      }

   /**
    *
    *@return The rating and title of the movie at the index
    */
   public String getTitle()
      {
      return title;
      }

   /**
    *
    *@return The movie title at the index
    */
   public String getMovie()
      {
      return movie;
      }

   /**
    *
    *@return The rating at the index
    */
   public int getRating()
      {
      return rating;
      }

   /**
    *
    *@param in int new index value
    */
   public void setIndex(int in)
      {
      updateMovieFeed();
      index = in;
      }

   /**
    *
    *Extracts movie rating and title from the entries
    */
   private void parseMovie()
      {
      int ratingIndex = title.indexOf("% ");
      if (ratingIndex > 0)
         {
         String strRating = title.substring(0, ratingIndex);
         Integer i = new Integer(strRating);
         rating = i.intValue();
         movie = title.substring(ratingIndex + 2);
         }
      else
         {
         rating = -1;
         movie = title;
         }
      }
   }

/*Sample

61% Spider-Man 3

*/