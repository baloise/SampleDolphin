package ch.basler.welcometable.admin;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
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

public class AdminApplication extends Application {

  // private TextField textField;
  private Button pushToServer;
  private ClientDolphin clientDolphin;
  private PresentationModel textAttributeModel;
  private Pane root;
  private final List<TextField> fields = new ArrayList<>();

  public AdminApplication() {}

  @Override
  public void init() throws Exception {
    clientDolphin = new ClientDolphin();
    clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
    HttpClientConnector connector = new HttpClientConnector(clientDolphin, "http://localhost:8080/welcometable_server/welcometable/");
    connector.setCodec(new JsonCodec());
    connector.setUiThreadHandler(new JavaFXUiThreadHandler());
    clientDolphin.setClientConnector(connector);

  }

  @Override
  public void start(Stage stage) throws Exception {

    // textField = TextFieldBuilder.create().id("firstname").build();
    createPresentationModel();

    List rows = new ArrayList<>();
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      HBox hbox = createHBox(i);
      rows.add(hbox);
    }

    pushToServer = ButtonBuilder.create().text("save").build();
    rows.add(pushToServer);

    VBox vBoxLayout = VBoxBuilder.create().id("content").children(rows).build();

    root = PaneBuilder.create().children(vBoxLayout).build();

    addClientSideAction();

    Scene scene = new Scene(root, 300, 250);
    stage.setScene(scene);
    stage.setTitle(getClass().getName());

    stage.show();

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
    fields.add(textField);
    return rowTextFields;
  }

  private void createPresentationModel() {
    List<ClientAttribute> clientAttributes = new ArrayList<>();
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      clientAttributes.add(new ClientAttribute(Welcometable.ATT_VIEW_TEXT + i, ""));
    }
    textAttributeModel = clientDolphin.presentationModel(Welcometable.PM_WELCOME,
        clientAttributes.toArray(new ClientAttribute[clientAttributes.size()]));
  }

  private void addClientSideAction() {
    pushToServer.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {

        for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
          textAttributeModel.getAt(Welcometable.ATT_VIEW_TEXT + i).setValue(fields.get(i).getText());
        }

        OnFinishedHandlerAdapter onFinished = new OnFinishedHandlerAdapter() {
          @Override
          public void onFinished(List<ClientPresentationModel> list) {
            super.onFinished(list);
          }
        };
        clientDolphin.send(Welcometable.CMD_PUSH, onFinished);

        // for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
        // textAttributeModel.getAt(Welcometable.ATT_VIEW_TEXT + i).rebase();
        // }
      }
    });
  }

}
