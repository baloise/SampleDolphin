package ch.basler.welcometable.server;

import groovyx.gpars.dataflow.DataflowQueue;

import java.util.ArrayList;
import java.util.List;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import ch.basler.welcometable.shared.Welcometable;

/**
 * 
 */
public class PushActionCommand extends DolphinServerAction {

  private final DataflowQueue queue;

  public PushActionCommand(DataflowQueue inQueue) {

    queue = inQueue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerIn(ActionRegistry inRegistry) {

    inRegistry.register(Welcometable.CMD_PUSH, new CommandHandler<Command>() {

      @Override
      public void handleCommand(Command arg0, List<Command> arg1) {
        ServerDolphin serverDolphin = getServerDolphin();
        SyncHelper.latestPm = serverDolphin.getAt(Welcometable.PM_WELCOME);
        if (SyncHelper.latestPm != null) {
          List<Slot> slots = new ArrayList<>();
          for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
            String message = (String)SyncHelper.latestPm.getAt(Welcometable.ATT_VIEW_TEXT + i).getValue();
            Slot s = new Slot(Welcometable.ATT_VIEW_TEXT + i, message);
            slots.add(s);
          }
          DTO dataTO = new DTO(slots);
          WelcometableServlet.eventBus.publish(queue, dataTO);
        }
      }
    });
  }
}
