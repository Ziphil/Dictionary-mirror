package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.text.Font
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.SwitchButton
import ziphil.custom.UtilityStage
import ziphil.dictionary.exporter.ShaleiaPdfExportConfig
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPdfExportConfigController extends Controller<ShaleiaPdfExportConfig> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/shaleia_pdf_export_config.fxml"
  private static final String TITLE = "エクスポート設定"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $externalCommandControl
  @FXML private ComboBox<String> $firstCaptionFontFamilyControl
  @FXML private ComboBox<String> $secondCaptionFontFamilyControl
  @FXML private Spinner<DoubleClass> $captionFontSizeControl
  @FXML private CheckBox $usesDefaultCaptionFontFamilyControl
  @FXML private ComboBox<String> $firstHeadFontFamilyControl
  @FXML private ComboBox<String> $secondHeadFontFamilyControl
  @FXML private Spinner<DoubleClass> $headFontSizeControl
  @FXML private CheckBox $usesDefaultHeadFontFamilyControl
  @FXML private ComboBox<String> $firstShaleiaFontFamilyControl
  @FXML private ComboBox<String> $secondShaleiaFontFamilyControl
  @FXML private Spinner<DoubleClass> $shaleiaFontSizeControl
  @FXML private CheckBox $usesDefaultShaleiaFontFamilyControl
  @FXML private ComboBox<String> $firstMainFontFamilyControl
  @FXML private ComboBox<String> $secondMainFontFamilyControl
  @FXML private Spinner<DoubleClass> $mainFontSizeControl
  @FXML private CheckBox $usesDefaultMainFontFamilyControl
  @FXML private TextField $relationMarkerControl
  @FXML private CheckBox $usesDefaultRelationMarkerControl
  @FXML private SwitchButton $modifiesControl

  public ShaleiaPdfExportConfigController(UtilityStage<? super ShaleiaPdfExportConfig> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupFontFamilyControls()
    bindFontControlProperties()
    bindMarkerControlProperty()
  }

  @FXML
  protected void commit() {
    String externalCommand = $externalCommandControl.getText()
    String firstCaptionFontFamily = $firstCaptionFontFamilyControl.getValue()
    String secondCaptionFontFamily = $secondCaptionFontFamilyControl.getValue()
    Double captionFontSize = $captionFontSizeControl.getValue()
    Boolean usesDefaultCaptionFontFamily = $usesDefaultCaptionFontFamilyControl.isSelected()
    String firstHeadFontFamily = $firstHeadFontFamilyControl.getValue()
    String secondHeadFontFamily = $secondHeadFontFamilyControl.getValue()
    Double headFontSize = $headFontSizeControl.getValue()
    Boolean usesDefaultHeadFontFamily = $usesDefaultHeadFontFamilyControl.isSelected()
    String firstShaleiaFontFamily = $firstShaleiaFontFamilyControl.getValue()
    String secondShaleiaFontFamily = $secondShaleiaFontFamilyControl.getValue()
    Double shaleiaFontSize = $shaleiaFontSizeControl.getValue()
    Boolean usesDefaultShaleiaFontFamily = $usesDefaultShaleiaFontFamilyControl.isSelected()
    String firstMainFontFamily = $firstMainFontFamilyControl.getValue()
    String secondMainFontFamily = $secondMainFontFamilyControl.getValue()
    Double mainFontSize = $mainFontSizeControl.getValue()
    Boolean usesDefaultMainFontFamily = $usesDefaultMainFontFamilyControl.isSelected()
    String relationMarker = $relationMarkerControl.getText()
    Boolean usesDefaultRelationMarker = $usesDefaultRelationMarkerControl.isSelected()
    Boolean modifies = $modifiesControl.isSelected()
    ShaleiaPdfExportConfig config = ShaleiaPdfExportConfig.new()
    config.setExternalCommand(externalCommand)
    if (!usesDefaultCaptionFontFamily) {
      config.setFirstCaptionFontFamily(firstCaptionFontFamily)
      config.setSecondCaptionFontFamily(secondCaptionFontFamily)
    }
    config.setCaptionFontSize(captionFontSize)
    if (!usesDefaultHeadFontFamily) {
      config.setFirstHeadFontFamily(firstHeadFontFamily)
      config.setSecondHeadFontFamily(secondHeadFontFamily)
    }
    config.setHeadFontSize(headFontSize)
    if (!usesDefaultShaleiaFontFamily) {
      config.setFirstShaleiaFontFamily(firstShaleiaFontFamily)
      config.setSecondShaleiaFontFamily(secondShaleiaFontFamily)
    }
    config.setShaleiaFontSize(shaleiaFontSize)
    if (!usesDefaultMainFontFamily) {
      config.setFirstMainFontFamily(firstMainFontFamily)
      config.setSecondMainFontFamily(secondMainFontFamily)
    }
    config.setMainFontSize(mainFontSize)
    if (!usesDefaultRelationMarker) {
      config.setRelationMarker(relationMarker)
    }
    config.setModifies(modifies)
    $stage.commit(config)
  }

  private void setupFontFamilyControls() {
    List<String> fontFamilies = Font.getFamilies()
    $firstCaptionFontFamilyControl.getItems().addAll(fontFamilies)
    $secondCaptionFontFamilyControl.getItems().addAll(fontFamilies)
    $firstHeadFontFamilyControl.getItems().addAll(fontFamilies)
    $secondHeadFontFamilyControl.getItems().addAll(fontFamilies)
    $firstShaleiaFontFamilyControl.getItems().addAll(fontFamilies)
    $secondShaleiaFontFamilyControl.getItems().addAll(fontFamilies)
    $firstMainFontFamilyControl.getItems().addAll(fontFamilies)
    $secondMainFontFamilyControl.getItems().addAll(fontFamilies)
  }

  private void bindFontControlProperties() {
    $firstCaptionFontFamilyControl.disableProperty().bind($usesDefaultCaptionFontFamilyControl.selectedProperty())
    $secondCaptionFontFamilyControl.disableProperty().bind($usesDefaultCaptionFontFamilyControl.selectedProperty())
    $firstHeadFontFamilyControl.disableProperty().bind($usesDefaultHeadFontFamilyControl.selectedProperty())
    $secondHeadFontFamilyControl.disableProperty().bind($usesDefaultHeadFontFamilyControl.selectedProperty())
    $firstShaleiaFontFamilyControl.disableProperty().bind($usesDefaultShaleiaFontFamilyControl.selectedProperty())
    $secondShaleiaFontFamilyControl.disableProperty().bind($usesDefaultShaleiaFontFamilyControl.selectedProperty())
    $firstMainFontFamilyControl.disableProperty().bind($usesDefaultMainFontFamilyControl.selectedProperty())
    $secondMainFontFamilyControl.disableProperty().bind($usesDefaultMainFontFamilyControl.selectedProperty())
  }

  private void bindMarkerControlProperty() {
    $relationMarkerControl.disableProperty().bind($usesDefaultRelationMarkerControl.selectedProperty())
  }

}