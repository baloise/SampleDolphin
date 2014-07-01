package ch.basler.welcometable.admingwt.client;

import java.util.ArrayList;
import java.util.List;

import com.canoo.opendolphin.client.gwt.ClientDolphin;
import com.canoo.opendolphin.client.gwt.Dolphin;
import com.canoo.opendolphin.client.gwt.DolphinStarter;
import com.canoo.opendolphin.client.gwt.PresentationModel;
import com.canoo.opendolphin.client.js.DolphinLoaderJS;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Welcometable_admingwt implements EntryPoint {

  private final List<TextBox> textBoxes = new ArrayList<TextBox>();

  public static final String DOLPHIN_URL = "http://localhost:8080/welcometable_server/welcometable/";

  private PresentationModel textAttributeModel;
  private ClientDolphin clientDolphin;

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {

    // VLayout vLayout = new VLayout();
    // vLayout.setShowEdges(true);
    // vLayout.setWidth(150);
    // vLayout.setMembersMargin(5);
    // vLayout.setLayoutMargin(10);
    //
    // IButton button = new IButton("Hello World");
    // button.addClickHandler(new ClickHandler() {
    //
    // @Override
    // public void onClick(ClickEvent event) {
    // SC.say("Hello World from SmartGWT");
    // }
    // });
    //
    // vLayout.addMember(button);
    // vLayout.draw();

    VerticalPanel vPanel = new VerticalPanel();
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      TextBox box = new TextBox();
      textBoxes.add(box);
      box.setMaxLength(20);
      vPanel.add(box);
    }
    Button pushButton = new Button("Push2Server");

    pushButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
          textAttributeModel.getAt(Welcometable.ATT_VIEW_TEXT + i).setValue(textBoxes.get(i).getText());
        }

        clientDolphin.send(Welcometable.CMD_PUSH);

        // for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
        // textAttributeModel.getAt(Welcometable.ATT_VIEW_TEXT + i).rebase();
        // }
      }
    });

    vPanel.add(pushButton);

    RootPanel.get("content").add(vPanel);
    initialize();
  }

  public void initialize() {
    // 1: Bootstrap Dolphin:
    DolphinLoaderJS.load(DOLPHIN_URL, new DolphinStarter() {
      @Override
      public void start(final Dolphin dolphin) {
        System.out.println("test");
        clientDolphin = dolphin.getClientDolphin();

        createPresentationModel();
        // 2: Initialize PMs:
        // PMContext pmContext = new PMContext().initialize(dolphin);
        //
        // // 3: Initialize View:
        // MainView view = new MainView().initialize();
        //
        // // 4: Bind view and PMs:
        // new Binder().bind(view, pmContext);

        // bind 'textBox' to 'textAttribute' bidirectionally
        int i = 0;
        for (TextBox box : textBoxes) {
          final int row = i++;
          box.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
              TextBox changedBox = (TextBox)event.getSource();
              textAttributeModel.getAt(Welcometable.ATT_VIEW_TEXT + row).setValue(changedBox.getText());
            }
          });
        } // for

        //
        // // 5: Load initial data into PMs:
        // new PMLoader().load(pmContext);
        // pmContext.sendCommand(PMConstants.CMD_LOAD_INITIAL);
      }
    });
  }

  private void createPresentationModel() {
    String[] clientAttributeNames = new String[Welcometable.DISPLAYROWS];
    for (int i = 0; i < Welcometable.DISPLAYROWS; i++) {
      clientAttributeNames[i] = Welcometable.ATT_VIEW_TEXT + i;
    }
    String type = null;
    textAttributeModel = clientDolphin.presentationModel(Welcometable.PM_WELCOME, type, clientAttributeNames);
  }
}
