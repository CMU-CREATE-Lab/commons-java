package edu.cmu.ri.createlab.rss.readers;

import edu.cmu.ri.createlab.rss.RSSReader;

/**
 * <p>
 * <code>StockReader</code> accesses stock quotes.
 * </p>
 *
 * StockReader.java
 * Description:  Reader class which utilizes the ROME library to read
 * RSS feed information from a stock (www.quoterss.com) website.
 *
 * @author Tom Lauwers
 */

public class StockReader extends RSSReader
   {
   private String title;
   private double price;
   private String symbol;

   /** Construct a stockreader
    *
    * @param input String holds the stock symbol to get quotes for (like 'GOOG' or 'YHOO')
    */
   public StockReader(String input)
   {
   super("http://www.quoterss.com/quote.php?symbol=" + input.toLowerCase());
   symbol = input;
   updateStockFeed();
   }

   // Updates the stock data from the web
   public void updateStockFeed()
   {
   updateFeed();
   title = getEntryTitle(0);
   parseStockFeed();
   }

   /**
    *
    *@return the entire entry title
    */
   public String getTitle()
   {
   return title;
   }

   /**
    *
    *@return the value of the stock
    */
   public double getStockQuote()
   {
   return price;
   }

   // Parsas the title information to extract the stock quote value
   private void parseStockFeed()
   {
   int priceIndex = title.indexOf(symbol.toUpperCase());
   int atIndex = title.indexOf(" at");

   String quoteString = title.substring(symbol.length() + priceIndex + 2, atIndex);
   Double quote = new Double(quoteString);
   price = quote.doubleValue();
   }
   }

/*Sample

QuoteRSS.com: MSFT: 28.63 at 4:00pm 8/13/2007

*/