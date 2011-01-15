package edu.cmu.ri.createlab.rss.readers;

import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>EarthquakeReader</code> accesses USGS recent earthquake data.
 * </p>
 *
 * EarthquakeReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information from the USGS earthquake site.
 *
 * @author Daragh Egan
 */

public class EarthquakeReader extends RSSReader
   {
   private String quakeDesc;
   private double m;
   private String location;
   private int index;

   /**Construct EarthquakeReader
    */
   public EarthquakeReader()
      {
      super("http://earthquake.usgs.gov/eqcenter/catalogs/eqs7day-M5.xml");
      index = 0;
      updateQuakeFeed();
      }

   /** Updates all Earthquake data from website */
   public void updateQuakeFeed()
      {
      updateFeed();
      // Update the feed data on instantiation - this loads all of the feed's data into a Syndfeed object (see RSSReader class)
      quakeDesc = getEntryTitle(index);
      parseQuakeFeed();
      }

   /**
    *
    *@return Magnitude of quake at index
    */
   public double getMagnitude()
      {
      return m;
      }

   /**
    *
    *@return number of quakes listed in feed
    */
   public int getQuakeCount()
      {
      return getEntryCount();
      }

   /**
    *
    *return location of quake at index
    */
   public String getLocation()
      {
      return location;
      }

   /**
    *
    *@param in int new index value
    */
   public void setIndex(int in)
      {
      updateQuakeFeed();
      index = in;
      }

   /**
    *
    *Stores data from String title into variables
    */
   private void parseQuakeFeed()
      {
      int mIndex = quakeDesc.indexOf("M ");
      int locationIndex = quakeDesc.indexOf(", ");

      String mStr = quakeDesc.substring(mIndex + 2, mIndex + 5);
      location = quakeDesc.substring(locationIndex + 2);

      Double locationDouble = new Double(mStr);
      m = locationDouble.doubleValue();
      }
   }

/*Sample:

M 7.5, Java, Indonesia

*/