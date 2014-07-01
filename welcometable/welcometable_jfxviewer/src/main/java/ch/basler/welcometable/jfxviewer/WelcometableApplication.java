package ch.basler.welcometable.jfxviewer;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Stage;

import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.core.comm.JsonCodec;

import ch.basler.welcometable.shared.Welcometable;

public class WelcometableApplication extends Application {

  private ClientDolphin clientDolphin;
  private PresentationModel textAttributeModel;

  public WelcometableApplication() {}

  @Override
  public void init() throws Exception {
    clientDolphin = new ClientDolphin();
    clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
    HttpClientConnector connector = new HttpClientConnector(clientDolphin, "http://localhost:8080/welcometable_server/welcometable/");
    connector.setCodec(new JsonCodec());
    connector.setUiThreadHandler(new JavaFXUiThreadHandler());
    clientDolphin.setClientConnector(connector);

  }

  private HBox createHBox(int row) {
    List<TextField> rowTextFields = createTextFields(row);
    return HBoxBuilder.create().id("row_" + row).children(rowTextFields).build();
  }

  private List<TextField> createTextFields(int row) {
    List<TextField> rowTextFields = new ArrayList<TextField>();
    TextField textField = TextFieldBuilder.create().id("row_" + row).build();
    rowTextFields.add(textField);
    JFXBinder.bind(Welcometable.ATT_VIEW_TEXT + row).of(textAttributeModel).to("text").of(textField);
    return rowTextFields;
  }

  @Override
  public void start(Stage stage) throws Exception {

    createPresentationModel();
    List<HBox> lines = new ArrayList<HBox>();
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      lines.add(createHBox(i));
    }

    VBox vBoxLayout = VBoxBuilder.create().id("content").children(lines).build();

    Pane root = PaneBuilder.create().children(vBoxLayout).build();

    bindPresentationModel();
    addClientSideAction();

    Scene scene = new Scene(root, 300, 250);
    stage.setScene(scene);
    stage.setTitle(getClass().getName());

    stage.show();

    final OnFinishedHandlerAdapter adapter = new OnFinishedHandlerAdapter() {
      @Override
      public void onFinished(List<ClientPresentationModel> presentationModels) {
        clientDolphin.send(Welcometable.CMD_POLL, this);
      }
    };

    clientDolphin.send(Welcometable.CMD_SYNC, adapter);

  }

  private void bindPresentationModel() {
    // JFXBinder.bind("text").of(textField).to(ATT_FIRSTNAME).of(textAttributeModel);
    // JFXBinder.bind("text").of(textField).to(Welcometable.ATT_VIEW_TEXT).of(textAttributeModel);
    // JFXBinder.bind(Welcometable.ATT_VIEW_TEXT).of(textAttributeModel).to("text").of(textField);
  }

  private void createPresentationModel() {
    List<ClientAttribute> clientAttributes = new ArrayList<>();
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      clientAttributes.add(new ClientAttribute(Welcometable.ATT_VIEW_TEXT + i));
    }
    textAttributeModel = clientDolphin.presentationModel(Welcometable.PM_WELCOME,
        clientAttributes.toArray(new ClientAttribute[clientAttributes.size()]));
  }

  private void addClientSideAction() {

  }

}
