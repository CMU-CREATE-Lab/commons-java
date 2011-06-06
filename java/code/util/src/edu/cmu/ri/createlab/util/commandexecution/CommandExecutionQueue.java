package edu.cmu.ri.createlab.util.commandexecution;

import java.util.concurrent.TimeUnit;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CommandExecutionQueue<CommandStrategyClass extends CommandStrategy, ResponseClass extends CommandResponse>
   {
   /**
    * Executes the given {@link CommandStrategy} and returns the {@link CommandResponse}. The timeout used, if any, is
    * implementations specific.
    */
   ResponseClass execute(final CommandStrategyClass commandStrategyClass) throws Exception;

   /**
    * Executes the given {@link CommandStrategy} and returns the {@link CommandResponse}. The timeout used is specified
    * by the <code>timeout</code> and <code>timeoutTimeUnit</code> parameters.
    */
   ResponseClass execute(final CommandStrategyClass commandStrategyClass, final long timeout, final TimeUnit timeoutTimeUnit) throws Exception;

   /**
    * Executes the given {@link CommandStrategy} and returns the status. The timeout used, if any, is implementations
    * specific.
    */
   boolean executeAndReturnStatus(final CommandStrategyClass commandStrategyClass) throws Exception;

   /**
    * Executes the given {@link CommandStrategy} and returns the status. The timeout used is specified by the
    * <code>timeout</code> and <code>timeoutTimeUnit</code> parameters.
    */
   boolean executeAndReturnStatus(final CommandStrategyClass commandStrategyClass, final long timeout, final TimeUnit timeoutTimeUnit) throws Exception;

   void shutdown();
   }