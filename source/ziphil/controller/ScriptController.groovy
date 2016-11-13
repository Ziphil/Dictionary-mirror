package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType


@CompileStatic @Newify
public class ScriptController extends Controller<String> {

  private static final String RESOURCE_PATH = "resource/fxml/script.fxml"
  private static final String TITLE = "スクリプト検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(240)

  @FXML private TextArea $scriptControl

  public ScriptController(UtilityStage<String> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  protected void commit() {
    String string = $scriptControl.getText()
    $stage.close(string)
  }

}