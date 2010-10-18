package edu.cmu.ri.createlab.rss.readers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * <p>
 * <code>WikipediaReader</code> accesses wikipedia articles.
 * </p>
 *
 * WikipediaReader.java
 * Description:  Reads articles from wikipedia.
 * I'm sure there are still bugs in here!
 * @author: Daragh Egan
 */

public class WikipediaReader
   {
   private String keyword;
   private URL u;
   private URLConnection connection;
   private InputStream stream;
   private Scanner in;

   private String page;               // html code for entire page
   private String title;               // Title of article
   private String text;               // Article text ouside html tags
   private ArrayList<String> contents;      // List of headings from table of contents

   /** Initializes Wikipedia Reader object
    */
   public WikipediaReader()
   {
   keyword = "";
   contents = new ArrayList<String>();
   }

   /** Returns ArrayList containing a list of headings in the article
    */
   public ArrayList<String> getContents()
   {

   return contents;
   }

   /** Use this method to search wikipedia for a certain String
    * @param keyword String to search wikipedia for
    * @return ArrayList of search results
    */
   public ArrayList<String> search(String keyword)
   {
   ArrayList<String> results = new ArrayList<String>();

   try
      {
      u = new URL("http://en.wikipedia.org/w/index.php?title=Special%3ASearch&search=" + keyword + "&fulltext=Search");
      connection = u.openConnection();
      stream = connection.getInputStream();
      in = new Scanner(stream);

      while (in.hasNext())
         {
         page += in.nextLine();
         }
      }
   catch (IOException e1)
      {
      System.out.println("Page could not be read.\nIOException: " + e1);
      }
   int start = 0;
   int end = 0;
   int last = page.lastIndexOf("</a><br");

   while (end != last)
      {
      start = page.indexOf("<li style=", start) + 35;
      start = page.indexOf("\">", start) + 2;
      end = page.indexOf("</a><br", start);
      results.add(page.substring(start, end));
      }
   return results;
   }

   /** Use this method to search an article for a word or phrase
    * @param keyword String to search article for for
    * @return ArrayList of search results
    */
   public ArrayList<String> searchArticle(String keyword)
   {
   ArrayList<String> results = new ArrayList<String>();

   if (!text.contains(keyword))
      {
      results.add("No results were found.");
      }
   else
      {
      int stop = text.indexOf(keyword);
      int start = 0;
      start = text.indexOf(keyword, start);
      int end = start + 10;
      results.add(text.substring(start, end) + "...");

      while (start != stop)
         {
         start = text.indexOf(keyword, start);
         end = start + 10;
         results.add(text.substring(start, end) + "...");
         }
      }

   return results;
   }

   /* @return String the title of the article opened for reading
     **/
   public String getArticleTitle()
   {
   return title;
   }

   /* @return the full text of the article
     **/
   public String readArticle()
   {
   return text;
   }

   /* @return the text in the paragraph under heading
     * @param String the heading of the paragraph to return
     */
   public String readParagraph(String heading)
   {

   if (!(contents.contains(heading)) && !(heading.equals("")))
      {
      return "No such paragraph.";
      }

   int start = page.indexOf("<span class=\"mw-headline\">" + heading);
   if (heading.equals(""))
      {
      start = 0;
      }
   start = page.indexOf("<p>", start);
   int end = page.indexOf("<span class=\"mw-headline\">", start);
   if (end < 0)
      {
      end = page.length();
      }

   if (start < 0 || end < 0)
      {
      return "Paragraph could not be found.";
      }

   return parseHTML(page.substring(start, end));
   }

   /* @return boolean true if article can be opened, contains text, and is not a
    *  page asking if you want to create a new article with that title
    */
   public boolean articleExists(String articleTitle)
   {
   articleTitle = articleTitle.replace(' ', '_');
   articleTitle = articleTitle.replace("(", "%28");
   articleTitle = articleTitle.replace(")", "%29");

   String s = "";
   try
      {
      u = new URL("http://en.wikipedia.org/wiki/" + articleTitle);
      connection = u.openConnection();
      stream = connection.getInputStream();
      in = new Scanner(stream);

      while (in.hasNext())
         {
         s += in.nextLine();
         }
      }
   catch (IOException e1)
      {
      return false;
      }
   return !s.contains("<b>Wikipedia does not have an article with this exact name.</b>");
   }

   /* @return true if page is a Featured Page */
   public boolean isFeatured()
   {
   return page.contains("alt=\"This is a featured article.");
   }

   /* @return true if article exists and was successfully opened and parsed */
   public boolean setArticle(String articleTitle)
   {
   if (!articleExists(articleTitle))
      {
      return false;
      }

   articleTitle = articleTitle.replace(' ', '_');
   articleTitle = articleTitle.replace("(", "%28");
   articleTitle = articleTitle.replace(")", "%29");

   System.out.println("Reading http://en.wikipedia.org/wiki/" + articleTitle);
   title = articleTitle;
   parseArticle();
   return (text != "");
   }

   // Returns a String containing the text from String in that is not inside html tags
   private String parseHTML(String in)
   {
   boolean read = false;
   String out = "";

   for (int i = 0; i < in.length(); i++)
      {
      if (read && in.charAt(i) != '<')         // Add to text if character is outside html tags
         {
         out += in.charAt(i);
         }
      else
         {
         read = in.charAt(i) == '>';
         }
      }

   out = out.replace("[edit]", "");            // Remove those little [edit] things you click on
   return out;
   }

   // Parses article text and updates variables page, contents, and text
   private void parseArticle()
   {
   page = "";
   text = "";
   //Read html in article and store in page
   try
      {
      u = new URL("http://en.wikipedia.org/wiki/" + title);
      connection = u.openConnection();
      stream = connection.getInputStream();
      in = new Scanner(stream);

      while (in.hasNext())
         {
         page += in.nextLine();
         }
      }
   catch (IOException e1)
      {
      System.out.println("Page could not be read.\nIOException: " + e1);
      }
   //System.out.println(page);
   //Parse String

   //Parse Table of Contents

   System.out.println("Reading Table of Contents...");

   int start = page.indexOf("toclevel") + 24;            // <li class="toclevel-1"><a href="#|History">
   int end = page.indexOf(">", start) - 1;               // <li class="toclevel-1"><a href="#History|">
   int last = page.lastIndexOf("toclevel");            // <li class="|toclevel-1"><a href="#External_links">
   if (start < 0 || end < 0)
      {
      System.out.println("Table of contents could not be read.");
      }
   else
      {
      String heading;
      while (start <= last)
         {
         start = page.indexOf("toclevel", end) + 22;
         end = page.indexOf(">", start) - 1;
         heading = page.substring(start, end);   // <li class="toclevel-1"><a href="#[History]">
         contents.add(heading.replace('_', ' '));
         }
      }
   // Parse Article Text
   //start = page.indexOf("<div id=\"bodyContent\">");
   //end = page.indexOf("<div id=\"column-one\">", start);

   System.out.println("Reading article...");

   //text = parseHTML(page.substring(start, end));
   text = readParagraph("");
   for (int i = 0; i < contents.size(); i++)
      {
      text += contents.get(i) + ":\n" + readParagraph(contents.get(i)) + "\n\n";
      }

   end = text.lastIndexOf("Views");
   if (end > 0)
      {
      text = text.substring(0, end);
      }
   }
   }