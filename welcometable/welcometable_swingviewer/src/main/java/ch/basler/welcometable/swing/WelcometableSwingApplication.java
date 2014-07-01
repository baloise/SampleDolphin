package ch.basler.welcometable.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.opendolphin.binding.Binder;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.JsonCodec;

import com.canoo.codecamp.dolphinpi.swingbaseddisplay.departureboardswingbased.board.DepartureBoard;
import com.canoo.codecamp.dolphinpi.swingbaseddisplay.departureboardswingbased.board.Row;

import ch.basler.welcometable.shared.Welcometable;

public class WelcometableSwingApplication extends JFrame {

  private static WelcometableSwingApplication mainframe;
  private final ClientDolphin clientDolphin = new ClientDolphin();
  private PresentationModel textAttributeModel;

  public WelcometableSwingApplication(String title) {
    super(title);
    DepartureBoard board = new DepartureBoard();

    connectDolphin();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    add(board, BorderLayout.CENTER);
    setBackground(Color.BLACK);
    pack();

    List<Row> rows = board.getRows();
    int i = 0;
    for (Row row : rows) {
      Binder.bind(Welcometable.ATT_VIEW_TEXT + i).of(textAttributeModel).to("destination").of(row);
      i++;
    }
    setVisible(true);
  }

  private void connectDolphin() {
    clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
    HttpClientConnector connector = new HttpClientConnector(clientDolphin, "http://localhost:8080/welcometable_server/welcometable/");
    connector.setCodec(new JsonCodec());
    connector.setUiThreadHandler(new UiThreadHandler() {

      @Override
      public void executeInsideUiThread(Runnable runnable) {
        EventQueue.invokeLater(runnable);
      }
    });
    clientDolphin.setClientConnector(connector);

    createPresentationModel();

    final OnFinishedHandlerAdapter adapter = new OnFinishedHandlerAdapter() {
      @Override
      public void onFinished(List<ClientPresentationModel> presentationModels) {
        clientDolphin.send(Welcometable.CMD_POLL, this);
      }
    };

    clientDolphin.send(Welcometable.CMD_SYNC, new OnFinishedHandlerAdapter() {
      @Override
      public void onFinished(List<ClientPresentationModel> presentationModels) {
        clientDolphin.send(Welcometable.CMD_POLL, adapter);
      }
    });

  }

  private void createPresentationModel() {
    List<ClientAttribute> clientAttributes = new ArrayList<>();
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      clientAttributes.add(new ClientAttribute(Welcometable.ATT_VIEW_TEXT + i, ""));
    }
    textAttributeModel = clientDolphin.presentationModel(Welcometable.PM_WELCOME,
        clientAttributes.toArray(new ClientAttribute[clientAttributes.size()]));
  }

  public static void main(String[] args) {
    mainframe = new WelcometableSwingApplication("Welcometable");

  }

}
