package ch.basler.welcometable.server;

import java.util.List;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import ch.basler.welcometable.shared.Welcometable;

class SyncActionCommand extends DolphinServerAction {

  public SyncActionCommand() {}

  @Override
  public void registerIn(ActionRegistry inRegistry) {

    inRegistry.register(Welcometable.CMD_SYNC, new CommandHandler<Command>() {

      @Override
      public void handleCommand(Command cmd, List<Command> cmds) {

        if (SyncHelper.latestPm != null) {
          for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
            changeValue(getServerDolphin().getAt(Welcometable.PM_WELCOME).getAt(Welcometable.ATT_VIEW_TEXT + i),
                SyncHelper.latestPm.getAt(Welcometable.ATT_VIEW_TEXT + i).getValue());
          }
        }
      }

    });
  }

}
