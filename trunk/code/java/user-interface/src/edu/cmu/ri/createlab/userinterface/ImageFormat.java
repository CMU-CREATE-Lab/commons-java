package edu.cmu.ri.createlab.userinterface;

import java.io.Serializable;

public enum ImageFormat implements Serializable
   {
      PNG("png"),
      JPEG("jpg");

   private final String name;

   private ImageFormat(final String name)
      {
      this.name = name;
      }

   public String getName()
      {
      return name;
      }

   public String getExtension()
      {
      return "." + name;
      }

   public String toString()
      {
      return name;
      }
   }
