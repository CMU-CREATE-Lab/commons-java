package edu.cmu.ri.createlab.rss.readers;

import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>WeatherReader</code> accesses current weather conditions for north american cities.
 * </p>
 *
 * WeatherReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information from the weather underground website.
 *
 * @author: Tom Lauwers (tlauwers@andrew.cmu.edu)
 */

public class WeatherReader extends RSSReader
   {

   // String to contain the entire description of the current weather conditions
   String weatherDesc;

   // Declaring variables to store weather conditions
   private double temperature;
   private double humidity;
   private double windSpeed;
   private double pressure;
   private String conditions;
   private String windDirection;

   /** Construct the Weather Reader by providing City, ST of the city which you wish to obtain weather
    *  data from.  For example, to get Pittsburgh, PA's data, construct with WeatherReader("Pittsburgh, PA").
    *
    * @param location String holds which location to get weather data from
    */
   public WeatherReader(String location)
   {
   // Parse the location string and stick it into an appropriate URL
   super("http://rss.wunderground.com/auto/rss_full/" + location.substring(location.length() - 2) + "/" + location.substring(0, location.length() - 4).replace(' ', '_') + ".xml?units=both");

   // Update the feed data on instantiation - this loads all of the feed's data into a Syndfeed object (see RSSReader class)
   updateWeatherFeed();
   // Get the string which contains the current conditions (this is always the description of entry 0)
   weatherDesc = getEntryDescription(0);
   }

   /** Updates all of the weather data from the website */
   public void updateWeatherFeed()
   {
   updateFeed();
   // Get the current conditions (always item 0)
   weatherDesc = getEntryDescription(0);
   parseWeatherFeed();
   }

   /**
    *
    * @return Temperature in fahrenheit
    */
   public double getTemperature()
   {
   return temperature;
   }

   /**
    *
    * @return Barometric pressure in inches
    */
   public double getPressure()
   {
   return pressure;
   }

   /**
    *
    * @return Humidity as a percentage
    */
   public double getHumidity()
   {
   return humidity;
   }

   /**
    *
    * @return Wind speed in miles per hour
    */
   public double getWindSpeed()
   {
   return windSpeed;
   }

   /**
    *
    * @return Current weather conditions (like 'partly cloudy', 'raindy', etc)
    */
   public String getConditions()
   {
   return conditions;
   }

   /**
    *
    * @return Wind direction ("West" or "ENE" or "SW")
    */
   public String getWindDirection()
   {
   return windDirection;
   }

   // Take the weatherDesc string which contains all of weather conditions and parse it to obtain individual values for
   // temperature, humidity, pressure, wind speed, wind direction, and conditions.
   private void parseWeatherFeed()
   {
   String temp;

   // Get the position in the description of the words Humidity, Pressure, Conditions, Wind Direction, and Wind speed
   int tempDegrees = weatherDesc.indexOf("&#176;F");
   int humidityIndex = weatherDesc.indexOf("Humidity: ");
   int pressureIndex = weatherDesc.indexOf("Pressure: ");
   int inIndex = weatherDesc.indexOf("in / ");
   int conditionsIndex = weatherDesc.indexOf("Conditions: ");
   int windDirectionIndex = weatherDesc.indexOf("Wind Direction: ");
   int windSpeedIndex = weatherDesc.indexOf("Wind Speed: ");
   int mphIndex = weatherDesc.indexOf("mph /");

   // First create a substring which is just the numbers corresponding to temperature in fahrenheit
   temp = weatherDesc.substring(13, tempDegrees);
   // Then convert it to a double
   Double tempDouble = new Double(temp);
   temperature = tempDouble.doubleValue();

   // First create a substring which is just the numbers corresponding to humidity
   temp = weatherDesc.substring(humidityIndex + 10, pressureIndex - 4);
   // Then convert it to a double
   tempDouble = tempDouble.valueOf(temp);
   humidity = tempDouble.doubleValue();

   // First create a substring which is just the numbers corresponding to pressure in inches
   temp = weatherDesc.substring(pressureIndex + 10, inIndex);
   // Then convert it to a double
   tempDouble = tempDouble.valueOf(temp);
   pressure = tempDouble.doubleValue();

   conditions = weatherDesc.substring(conditionsIndex + 12, windDirectionIndex - 3);

   windDirection = weatherDesc.substring(windDirectionIndex + 16, windSpeedIndex - 3);

   // First create a substring which is just the numbers corresponding to wind speed in mph
   temp = weatherDesc.substring(windSpeedIndex + 12, mphIndex);
   // Then convert it to a double
   tempDouble = tempDouble.valueOf(temp);
   windSpeed = tempDouble.doubleValue();
   }
   }
    
    