package edu.cmu.ri.createlab.rss.readers;

import java.util.ArrayList;
import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>ScoreReader</code> accesses recent sports scores.
 * </p>
 *
 * ScoreReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information of North American sports scores.
 *
 * @author Daragh Egan
 */

public class ScoreReader extends RSSReader
   {
   private String title;
   private String home;
   private int homeScore;
   private String away;
   private int awayScore;
   private String sport;
   private int index;

   /**Construct the ScoreReader
    */
   public ScoreReader()
   {
   super("http://totallyscored.com/rss");
   // Update the feed data on instantiation - this loads all of the feed's data into a Syndfeed object (see RSSReader class)
   updateScoreFeed();
   index = 0;
   }

   /** Updates all score data from website */
   public void updateScoreFeed()
   {
   updateFeed();
   // Get the game at the indicated index
   title = getEntryTitle(index);
   parseScore();
   }

   /**
    *
    *@return number of games listed in the feed
    */
   public int getScoreCount()
   {
   return getEntryCount();
   }

   /**
    *
    *@return entry text at the indicated index
    */
   public String getTitle()
   {
   return title;
   }

   /**
    *
    *@return home team's location
    */
   public String getHome()
   {
   return home;
   }

   /**
    *
    *@return away team's location
    */
   public String getAway()
   {
   return away;
   }

   /**
    *
    *@return winning team's location
    */
   public String getWinner()
   {
   if (homeScore > awayScore)
      {
      return home;
      }
   else
      {
      return away;
      }
   }

   /**
    *
    *@return losing team's location
    */
   public String getLoser()
   {
   if (homeScore < awayScore)
      {
      return home;
      }
   else
      {
      return away;
      }
   }

   /**
    *
    *@return score of the home team
    */
   public int getHomeScore()
   {
   return homeScore;
   }

   /**
    *
    *@return score of the away team
    */
   public int getAwayScore()
   {
   return awayScore;
   }

   /**
    *
    *@return score of the winning team
    */
   public int getWinnerScore()
   {
   if (homeScore > awayScore)
      {
      return homeScore;
      }
   else
      {
      return awayScore;
      }
   }

   /**
    *
    *@return score of the losing team
    */
   public int getLoserScore()
   {
   if (homeScore < awayScore)
      {
      return homeScore;
      }
   else
      {
      return awayScore;
      }
   }

   /**
    *
    *@return sport of the game at the indicated index
    */
   public String getSport()
   {
   return sport;
   }

   /**
    *
    *@param keyword String holds the keyword to search for
    *@return ArrayList holding search results
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
    *
    *@param in int holds the new index to use
    */
   public void setIndex(int in)
   {
   updateScoreFeed();
   index = in;
   }

   /**
    *Stores data from String title into variables
    */
   private void parseScore()
   {
   title = title.substring(0, title.indexOf("*")) + title.substring(title.indexOf("*") + 1);   //[Home Place 5]*[ vs. Away Place 3 (sport)]

   int vsIndex = title.indexOf(" vs. ");         // Home 5*| vs. Away 3 (sport)
   int sportIndex = title.indexOf("(");         // Home 5* vs. Away 3| (sport)
   String tmpTitle = title;
   Integer i = new Integer(0);

   home = title.substring(0, vsIndex);                              //[Home Place 5] vs. Away Place 3 (sport)
   String strHomeScore = home.substring(home.lastIndexOf(" ") + 1);      //Home Place [5]
   homeScore = i.valueOf(strHomeScore);
   home = home.substring(0, home.lastIndexOf(" "));                  //[Home Place] 5
   away = title.substring(vsIndex + 5, sportIndex - 1);               //Home Place 5 vs. [Away Place 3] (sport)
   String strAwayScore = away.substring(away.lastIndexOf(" ") + 1);      //Away Place [3]
   awayScore = i.valueOf(strAwayScore);
   away = away.substring(0, away.lastIndexOf(" "));                  //[Away Place] 3
   sport = title.substring(sportIndex + 1, title.length() - 1);         //Home Place 5 vs. Away Place 3 ([sport])

   title = tmpTitle;
   }
   }
/*Sample

Home Place 5* vs. Away Place 3 (sport)
Tampa Bay 1 vs. Texas 9* (Major League Baseball)

*/