package ch.basler.welcometable.server;

import groovyx.gpars.dataflow.DataflowQueue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

public class WelcometableServlet extends DolphinServlet {

  static EventBus eventBus = new EventBus();

  /**
   * 
   */
  private static final long serialVersionUID = 3145498596449350362L;

  @Override
  protected void registerApplicationActions(ServerDolphin serverDolphin) {
    DataflowQueue<DTO> queue = new DataflowQueue<DTO>();
    eventBus.subscribe(queue);
    serverDolphin.register(new PushActionCommand(queue));
    serverDolphin.register(new PollActionCommand(queue));
    serverDolphin.register(new SyncActionCommand());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doPost(req, resp);
  }
}
