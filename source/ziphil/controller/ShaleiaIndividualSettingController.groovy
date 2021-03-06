package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.ExtensionFilter
import ziphil.custom.Measurement
import ziphil.custom.RichTextLanguage
import ziphil.custom.UtilityStage
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaIndividualSettingController extends Controller<BooleanClass> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/shaleia_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(520)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(360)

  @FXML private TextField $alphabetOrderControl
  @FXML private TextField $versionControl
  @FXML private TextField $akrantiainSourceControl
  @FXML private TextArea $changeDescriptionControl
  private String $akrantiainSource
  private ShaleiaDictionary $dictionary

  public ShaleiaIndividualSettingController(UtilityStage<? super BooleanClass> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $versionControl.setText(dictionary.getVersion())
    $akrantiainSourceControl.setText(dictionary.getAkrantiainSource())
    $changeDescriptionControl.setText(dictionary.getChangeDescription())
    $akrantiainSource = dictionary.getAkrantiainSource()
  }

  @FXML
  protected void commit() {
    $dictionary.setAlphabetOrder($alphabetOrderControl.getText())
    $dictionary.setVersion($versionControl.getText())
    $dictionary.setAkrantiainSource($akrantiainSource)
    $dictionary.setChangeDescription($changeDescriptionControl.getText())
    $stage.commit(true)
  }

  @FXML
  private void editSnoj() {
    UtilityStage<FileStringChooserController.Result> nextStage = createStage()
    FileStringChooserController controller = FileStringChooserController.new(nextStage)
    ExtensionFilter filter = ExtensionFilter.new("snojファイル", "snoj")
    FileStringChooserController.Result previousResult = FileStringChooserController.Result.ofString($akrantiainSource)
    controller.prepare(filter, RichTextLanguage.AKRANTIAIN, previousResult)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      FileStringChooserController.Result result = nextStage.getResult()
      if (result.isFileSelected()) {
        File file = result.getFile()
        if (file != null) {
          String source = file.getText()
          $akrantiainSource = source
          $akrantiainSourceControl.setText(source)
        }
      } else {
        String source = result.getString()
        $akrantiainSource = source
        $akrantiainSourceControl.setText(source)
      }
    }
  }

  @FXML
  private void removeSnoj() {
    $akrantiainSource = null
    $akrantiainSourceControl.setText("")
  }

}