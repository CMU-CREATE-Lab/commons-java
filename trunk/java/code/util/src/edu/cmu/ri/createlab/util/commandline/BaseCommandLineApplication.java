package edu.cmu.ri.createlab.util.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>BaseCommandLineApplication</code> provides a framework for command line applications.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(BaseCommandLineApplication.class);
   protected static final String QUIT_COMMAND = "q";

   private final Map<String, Runnable> actionMap = new HashMap<String, Runnable>();
   private final BufferedReader in;

   public BaseCommandLineApplication(final BufferedReader in)
      {
      this.in = in;
      }

   protected static void prompt()
      {
      print("==> ");
      }

   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   protected static void println(final Object o)
      {
      System.out.println(o);
      System.out.flush();
      }

   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   protected static void print(final Object o)
      {
      System.out.print(o);
      System.out.flush();
      }

   protected final void registerAction(final String command, final Runnable action)
      {
      if (command != null && action != null)
         {
         actionMap.put(command, action);
         }
      }

   @SuppressWarnings({"UnusedCatchParameter"})
   protected final Integer readInteger(final String prompt)
      {

      try
         {
         print(prompt);
         final String intStr = in.readLine();
         try
            {
            return Integer.parseInt(intStr);
            }
         catch (final NumberFormatException e)
            {
            return null;
            }
         }
      catch (IOException e)
         {
         LOG.error("IOException while reading user input", e);
         }

      return null;
      }

   @SuppressWarnings({"UnusedCatchParameter"})
   protected final Double readDouble(final String prompt)
      {

      try
         {
         print(prompt);
         final String intStr = in.readLine();
         try
            {
            return Double.parseDouble(intStr);
            }
         catch (final NumberFormatException e)
            {
            return null;
            }
         }
      catch (IOException e)
         {
         LOG.error("IOException while reading user input", e);
         }

      return null;
      }

   protected final String readString(final String prompt)
      {
      try
         {
         print(prompt);
         return in.readLine();
         }
      catch (IOException e)
         {
         LOG.error("IOException while reading user input", e);
         }

      return null;
      }

   protected abstract void menu();

   public final void executeCommand(final String command)
      {
      final Runnable action = actionMap.get(command);
      if (action != null)
         {
         action.run();
         }
      else
         {
         if (command != null && command.length() > 0)
            {
            println("Unknown command '" + command + "'");
            println("");
            menu();
            }
         }
      }

   public final void run()
      {
      menu();

      try
         {
         String line;
         do
            {
            prompt();
            line = in.readLine();
            if (line != null)
               {
               line = line.trim();
               }

            executeCommand(line);
            }
         while (!QUIT_COMMAND.equals(line));
         }
      catch (IOException ex)
         {
         ex.printStackTrace();
         }

      System.exit(0);
      }
   }