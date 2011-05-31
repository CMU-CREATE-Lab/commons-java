package edu.cmu.ri.createlab.util.commandexecution;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CommandExecutionQueue<CommandStrategyClass extends CommandStrategy, ResponseClass extends CommandResponse>
   {
   ResponseClass execute(final CommandStrategyClass commandStrategyClass) throws Exception;

   boolean executeAndReturnStatus(final CommandStrategyClass commandStrategyClass) throws Exception;

   void shutdown();
   }