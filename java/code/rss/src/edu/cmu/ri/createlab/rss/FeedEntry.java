package edu.cmu.ri.createlab.rss;

import java.util.Date;

/**
 * <p>
 * <code>FeedEntry</code> represents a single entry from an RSS feed.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FeedEntry
   {
   private final String author;
   private final String title;
   private final String link;
   private final String description;
   private final long publishedTimestamp;

   FeedEntry(final String author, final String title, final String link, final String description, final Date publishedDate)
      {
      this.author = author == null ? "" : author;
      this.title = title == null ? "" : title;
      this.link = link == null ? "" : link;
      this.description = description == null ? "" : description;
      this.publishedTimestamp = (publishedDate == null) ? 0 : publishedDate.getTime();
      }

   /**
    * Returns the author, or an empty {@link String} if no author exists.  Guaranteed to not return <code>null</code>.
    */
   public String getAuthor()
   {
   return author;
   }

   /**
    * Returns the title, or an empty {@link String} if no title exists.  Guaranteed to not return <code>null</code>.
    */
   public String getTitle()
   {
   return title;
   }

   /**
    * Returns the link, or an empty {@link String} if no link exists.  Guaranteed to not return <code>null</code>.
    */
   public String getLink()
   {
   return link;
   }

   /**
    * Returns the description, or an empty {@link String} if no description exists.  Guaranteed to not return <code>null</code>.
    */
   public String getDescription()
   {
   return description;
   }

   /**
    * Returns the published timestamp, or 0 if no timestamp exists.
    */
   public long getPublishedTimestamp()
   {
   return publishedTimestamp;
   }

   /**
    * Returns the {@link #getPublishedTimestamp() published timestamp} as a {@link Date}.  Note that the date will be
    * the "epoch" date if no timestamp actually exists.
    *
    * @see Date
    */
   public Date getPublishedTimestampAsDate()
   {
   return new Date(publishedTimestamp);
   }

   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final FeedEntry entry = (FeedEntry)o;

      if (publishedTimestamp != entry.publishedTimestamp)
         {
         return false;
         }
      if (author != null ? !author.equals(entry.author) : entry.author != null)
         {
         return false;
         }
      if (description != null ? !description.equals(entry.description) : entry.description != null)
         {
         return false;
         }
      if (link != null ? !link.equals(entry.link) : entry.link != null)
         {
         return false;
         }
      if (title != null ? !title.equals(entry.title) : entry.title != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = (author != null ? author.hashCode() : 0);
      result = 31 * result + (title != null ? title.hashCode() : 0);
      result = 31 * result + (link != null ? link.hashCode() : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (int)(publishedTimestamp ^ (publishedTimestamp >>> 32));
      return result;
      }

   public String toString()
      {
      return "FeedEntry{" +
             "author='" + author + '\'' +
             ", title='" + title + '\'' +
             ", link='" + link + '\'' +
             ", description='" + description + '\'' +
             ", publishedTimestamp=" + publishedTimestamp +
             '}';
      }
   }
