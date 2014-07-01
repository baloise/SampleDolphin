package ch.basler.welcometable.server;

import groovyx.gpars.dataflow.DataflowQueue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import ch.basler.welcometable.shared.Welcometable;

public class PollActionCommand extends DolphinServerAction {

  private final DataflowQueue<DTO> queue;

  public PollActionCommand(DataflowQueue<DTO> inQueue) {

    queue = inQueue;
  }

  @Override
  public void registerIn(ActionRegistry actionregistry) {
    actionregistry.register(Welcometable.CMD_POLL, new CommandHandler<Command>() {

      @Override
      public void handleCommand(Command cmd, List<Command> cmds) {
        try {
          DTO val = queue.getVal(20, TimeUnit.SECONDS);
          if (val != null) {
            int i = 0;
            ServerPresentationModel pm = getServerDolphin().getAt(Welcometable.PM_WELCOME);
            for (Slot s : val.getSlots()) {
              System.out.println("Change Value: from " + pm.getAt(Welcometable.ATT_VIEW_TEXT + i).getValue() + " to " + s.getValue());
              changeValue(pm.getAt(Welcometable.ATT_VIEW_TEXT + i), s.getValue());
              i++;
            }
          }
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

}
