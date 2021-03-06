package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.lang.Thread.UncaughtExceptionHandler
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Dragboard
import javafx.scene.input.DragEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.TransferMode
import javafx.stage.Stage
import javafx.stage.WindowEvent
import javax.script.ScriptException
import ziphil.Launcher
import ziphil.custom.ClosableTab
import ziphil.custom.Dialog
import ziphil.custom.ExtensionFilter
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.Badge
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.SearchMode
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.WordOrderType
import ziphil.module.Setting
import ziphil.module.Version
import ziphil.plugin.SimplePlugin
import ziphil.plugin.PluginManager
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class MainController extends PrimitiveController<Stage> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/main.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String OFFICIAL_SITE_URI = "http://ziphil.com/application/download/2.html"
  private static final String TITLE = "ZpDIC fetith"
  private static final Double MIN_WIDTH = Measurement.rpx(360)
  private static final Double MIN_HEIGHT = Measurement.rpx(240)

  @FXML private MenuBar $menuBar
  @FXML private Menu $createDictionaryMenu
  @FXML private Menu $openRegisteredDictionaryMenu
  @FXML private Menu $registerCurrentDictionaryMenu
  @FXML private Menu $convertDictionaryMenu
  @FXML private Menu $exportDictionaryMenu
  @FXML private Menu $changeSearchModeMenu
  @FXML private Menu $changeWordOrderMenu
  @FXML private Menu $searchRegisteredParameterMenu
  @FXML private Menu $badgeWordsMenu
  @FXML private Menu $pluginMenu
  @FXML private TabPane $tabPane
  private List<MainWordListController> $wordListControllers = ArrayList.new()
  private List<Stage> $openStages = Collections.synchronizedList(ArrayList.new())

  public MainController(Stage stage) {
    super(stage)
    loadOriginalResource()
    setupDragAndDrop()
    setupShortcuts()
    setupCloseConfirmation()
    setupExceptionHandler()
  }

  @FXML
  public void initialize() {
    setupTabPane()
    setupCreateDictionaryMenu()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
    setupChangeSearchModeMenu()
    setupChangeWordOrderMenu()
    setupPluginMenu()
    setupBadgeWordsMenu()
    setupDebug()
  }

  public void prepare() {
    checkVersion()
    openDefaultDictionary()
  }

  private void addDictionaryTab(Dictionary dictionary) {
    ClosableTab tab = ClosableTab.new()
    MainWordListController controller = MainWordListController.new($stage, this, tab)
    tab.setText(dictionary.getName())
    tab.setOnCloseRequest() { Event event ->
      Int index = $tabPane.getTabs().indexOf(tab)
      Boolean allowsClose = $wordListControllers[index].requestClose()
      if (allowsClose) {
        $wordListControllers.removeAt(index)
      } else {
        event.consume()
      }
    }
    controller.open(dictionary)
    $wordListControllers.add(controller)
    $tabPane.getTabs().add(tab)
    $tabPane.getSelectionModel().select(tab)
  }

  private MainWordListController currentWordListController() {
    Int index = $tabPane.getSelectionModel().getSelectedIndex()
    if (index >= 0) {
      return $wordListControllers[index]
    } else {
      return null
    }
  }

  private Dictionary currentDictionary() {
    MainWordListController controller = currentWordListController()
    if (controller != null) {
      return controller.getDictionary()
    } else {
      return null
    }
  }

  private IndividualSetting currentIndividualSetting() {
    MainWordListController controller = currentWordListController()
    if (controller != null) {
      return controller.getDictionary().getIndividualSetting()
    } else {
      return null
    }
  }

  @FXML
  private void openDictionary() {
    File file = chooseDictionary(currentDictionary(), null)
    if (file != null) {
      Dictionary dictionary = DictionaryFactory.loadProper(file)
      if (dictionary != null) {
        Setting.getInstance().setDefaultDictionaryPath(dictionary.getPath())
        addDictionaryTab(dictionary)
      } else {
        showErrorDialog("failOpenDictionary")
      }
    }
  }

  @FXML
  private void reopenDictionary() {
    MainWordListController controller = currentWordListController()
    Boolean allowsReopen = controller.requestClose()
    if (allowsReopen) {
      File file = File.new(currentDictionary().getPath())
      Dictionary dictionary = DictionaryFactory.loadProper(file)
      if (dictionary != null) {
        controller.open(dictionary)
      } else {
        showErrorDialog("failOpenDictionary")
      }
    }
  }

  private void openDefaultDictionary() {
    String filePath = Setting.getInstance().getDefaultDictionaryPath()
    if (filePath != null) {
      File file = File.new(filePath)
      Dictionary dictionary = DictionaryFactory.loadProper(file)
      if (dictionary != null) {
        addDictionaryTab(dictionary)
      } else {
        showErrorDialog("failOpenDictionary")
      }
    } else {
      update()
    }
  }

  private void openRegisteredDictionary(File file) {
    Dictionary dictionary = DictionaryFactory.loadProper(file)
    if (dictionary != null) {
      Setting.getInstance().setDefaultDictionaryPath(dictionary.getPath())
      addDictionaryTab(dictionary)
    } else {
      showErrorDialog("failOpenDictionary")
    }
  }

  private void createDictionary(DictionaryFactory factory) {
    File file = chooseDictionary(currentDictionary(), factory)
    if (file != null) {
      Dictionary dictionary = DictionaryFactory.loadProperEmpty(factory, file)
      if (dictionary != null) {
        Setting.getInstance().setDefaultDictionaryPath(dictionary.getPath())
        dictionary.getDictionaryFactory().save(dictionary)
        addDictionaryTab(dictionary)
      } else {
        showErrorDialog("failCreateDictionary")
      }
    }
  }

  @FXML
  private void saveDictionary() {
    currentWordListController().saveDictionary()
  }

  @FXML
  private void saveAndRenameDictionary() {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      File file = chooseDictionary(dictionary, dictionary.getDictionaryFactory())
      if (file != null) {
        Tab tab = $tabPane.getSelectionModel().getSelectedItem()
        dictionary.setName(file.getName())
        dictionary.setPath(file.getAbsolutePath())
        tab.setText(dictionary.getName())
        currentWordListController().saveDictionary()
        Setting.getInstance().setDefaultDictionaryPath(dictionary.getPath())
      }
    }
  }

  private void convertDictionary(DictionaryFactory factory) {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      File file = chooseDictionary(dictionary, factory)
      if (file != null) {
        Dictionary nextDictionary = DictionaryFactory.convertProper(factory, dictionary, file)
        if (nextDictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(nextDictionary.getPath())
          nextDictionary.getDictionaryFactory().save(nextDictionary)
          addDictionaryTab(nextDictionary)
        } else {
          showErrorDialog("failConvertDictionary")
        }
      }
    }
  }

  @FXML
  private void exportDictionary(ExportType type) {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      File file = chooseExportDestination(type.createExtensionFilter())
      if (file != null) {
        UtilityStage<ExportConfig> nextStage = createStage()
        Controller controller = dictionary.getDictionaryFactory().createExportConfigController(nextStage, dictionary, type)
        ExportConfig config = null
        if (controller != null) {
          nextStage.showAndWait()
          if (nextStage.isCommitted()) {
            config = nextStage.getResult()
          }
        } else {
          config = ExportConfig.new()
        }
        if (config != null) {
          config.setPath(file.getAbsolutePath())
          config.setType(type)
          currentWordListController().exportDictionary(config)
          Task<?> saver = dictionary.getSaver()
          if (saver != null) {
            saver.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
              openExternalFile(File.new(config.getPath()))
            }
          }
        }
      }
    }
  }

  // ファイル選択ウィンドウを開き、参照する辞書ファイルを選択します。
  // デフォルトでは、ホームディレクトリが表示された状態でウィンドウが開き、全てのファイルが表示されます。
  // ただし、dictionary に辞書データを渡すことで、デフォルトで開いておくディレクトリをその辞書データが置かれている場所に変更できます。
  // また、factory に辞書形式を渡すことで、デフォルトでその辞書形式の拡張子のみを表示するようにできます。
  private File chooseDictionary(Dictionary dictionary, DictionaryFactory factory) {
    File directory = (dictionary != null) ? File.new(dictionary.getPath()).getParentFile() : null
    UtilityStage<File> nextStage = createStage()
    DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
    controller.prepare(factory, directory, true)
    nextStage.showAndWait()
    if (nextStage.isCommitted() && nextStage.getResult() != null) {
      return nextStage.getResult()
    } else {
      return null
    }
  }

  private File chooseExportDestination(ExtensionFilter extensionFilter) {
    UtilityStage<File> nextStage = createStage()
    FileChooserController controller = FileChooserController.new(nextStage)
    List<ExtensionFilter> extensionFilters = ArrayList.new()
    extensionFilters.add(extensionFilter)
    controller.prepare(extensionFilters, extensionFilter, true)
    nextStage.showAndWait()
    if (nextStage.isCommitted() && nextStage.getResult() != null) {
      return nextStage.getResult()
    } else {
      return null
    }
  }

  private void openExternalFile(File file) {
    if (Desktop.isDesktopSupported() && !GraphicsEnvironment.isHeadless()) {
      Desktop desktop = Desktop.getDesktop()
      if (desktop.isSupported(Desktop.Action.OPEN)) {
        try {
          desktop.open(file)
        } catch (Exception exception) {
        }
      }
    }
  }

  @FXML
  private void selectPreviousDictionary() {
    $tabPane.getSelectionModel().selectPrevious()
  }

  @FXML
  private void selectNextDictionary() {
    $tabPane.getSelectionModel().selectNext()
  }

  private void updateMenuItems() {
    for (Menu menu : $menuBar.getMenus()) {
      for (MenuItem item : menu.getItems()) {
        List<String> styleClass = item.getStyleClass()
        if (styleClass.contains("option")) {
          Boolean matched = checkValidStyleClass(styleClass)
          if (matched) {
            item.setDisable(false)
            if (styleClass.contains("dammy")) {
              if (!styleClass.contains("menu")) {
                item.setDisable(true)
              }
              item.setVisible(true)
            }
          } else {
            item.setDisable(true)
            if (styleClass.contains("dammy")) {
              item.setVisible(false)
            }
          }
        }
        if (!Setting.getInstance().isDebugging()) {
          if (styleClass.contains("debugging")) {
            item.setDisable(true)
            item.setVisible(false)
          }
        }
      }
    }
    $menuBar.layout()
  }

  private Boolean checkValidStyleClass(List<String> styleClass) {
    Boolean matched = false
    Dictionary dictionary = currentDictionary()
    IndividualSetting individualSetting = currentIndividualSetting()
    if (dictionary != null) {
      if (styleClass.contains("nonnull")) {
        matched = true
      }
    } else {
      if (styleClass.contains("null")) {
        matched = true
      }
    }
    if (dictionary != null && dictionary.getDictionaryFactory().isSearcherSupported(dictionary)) {
      if (styleClass.contains("can-search-detail")) {
        matched = true
      }
    }
    if (individualSetting != null && individualSetting.getRegisteredParameters() != null) {
      if (styleClass.contains("can-register-search-parameter")) {
        matched = true
      }
    } else {
      if (styleClass.contains("cannot-register-search-parameter")) {
        matched = true
      }
    }
    if (dictionary != null && dictionary.getDictionaryFactory().isIndividualSettingSupported(dictionary)) {
      if (styleClass.contains("has-individual-setting")) {
        matched = true
      }
    }
    return matched
  }

  private void updateSearchRegisteredParameterMenu() {
    $searchRegisteredParameterMenu.getItems().clear()
    IndividualSetting individualSetting = currentIndividualSetting()
    if (individualSetting != null) {
      List<SearchParameter> parameters = individualSetting.getRegisteredParameters()
      List<String> parameterNames = individualSetting.getRegisteredParameterNames()
      if (parameters != null) {
        for (Int i = 0 ; i < 10 ; i ++) {
          SearchParameter parameter = parameters[i]
          String parameterName = parameterNames[i]
          MenuItem item = MenuItem.new()
          item.setMnemonicParsing(false)
          if (parameter != null) {
            item.setText(parameterNames[i] ?: "")
            item.setOnAction() {
              currentWordListController().measureAndSearch(parameter)
            }
          } else {
            item.setText("未登録")
            item.setDisable(true)
          }
          Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/empty.png"))
          item.setGraphic(ImageView.new(icon))
          item.setAccelerator(KeyCodeCombination.new(KeyCode.valueOf("DIGIT${(i + 1) % 10}"), KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN))
          $searchRegisteredParameterMenu.getItems().add(item)
        }
      }
    }
  }

  private void updateConvertDictionaryMenu() {
    $convertDictionaryMenu.getItems().clear()
    Dictionary dictionary = currentDictionary()
    for (DictionaryFactory factory : DictionaryFactory.FACTORIES) {
      DictionaryFactory cachedFactory = factory
      MenuItem item = MenuItem.new(factory.getName())
      item.setGraphic(ImageView.new(factory.createIcon()))
      if (dictionary != null) {
        item.setOnAction() {
          convertDictionary(cachedFactory)
        }
        if (!factory.isConvertableFrom(dictionary)) {
          item.setDisable(true)
        }
      } else {
        item.setDisable(true)
      }
      $convertDictionaryMenu.getItems().add(item)
    }
  }

  private void updateExportDictionaryMenu() {
    $exportDictionaryMenu.getItems().clear()
    Dictionary dictionary = currentDictionary()
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/empty.png"))
    for (ExportType type : ExportType.values()) {
      ExportType cachedType = type
      MenuItem item = MenuItem.new(type.getName())
      item.setGraphic(ImageView.new(icon))
      if (dictionary != null) {
        item.setOnAction() {
          exportDictionary(cachedType)
        }
        if (!dictionary.getDictionaryFactory().isExportableTo(dictionary, type)) {
          item.setDisable(true)
        }
      } else {
        item.setDisable(true)
      }
      $exportDictionaryMenu.getItems().add(item)
    }
  }  

  private void updatePluginMenu() {
    Dictionary dictionary = currentDictionary()
    for (int i = 0 ; i < $pluginMenu.getItems().size() ; i ++) {
      MenuItem item = $pluginMenu.getItems()[i]
      SimplePlugin plugin = PluginManager.SIMPLE_PLUGINS[i]
      if (plugin.isSupported(dictionary)) {
        item.setDisable(false)
        item.setOnAction() {
          plugin.call(dictionary)
        }
      } else {
        item.setDisable(true)
        item.setOnAction() {
        }
      }
    }
  }

  private void update() {
    updateMenuItems()
    updateSearchRegisteredParameterMenu()
    updateConvertDictionaryMenu()
    updateExportDictionaryMenu()
    updatePluginMenu()
  }

  private void registerCurrentDictionary(Int index) {
    Setting.getInstance().getRegisteredDictionaryPaths()[index] = currentDictionary().getPath()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
  }

  @FXML
  private void editIndividualSetting() {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      UtilityStage<BooleanClass> nextStage = createStage()
      Controller controller = dictionary.getDictionaryFactory().createIndividualSettingController(nextStage, dictionary)
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        dictionary.updateMinimum()
        updateSearchRegisteredParameterMenu()
      }
    }
  }

  @FXML
  private void editSetting() {
    UtilityStage<BooleanClass> nextStage = createStage()
    SettingController controller = SettingController.new(nextStage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      setupOpenRegisteredDictionaryMenu()
      setupRegisterCurrentDictionaryMenu()
    }
  }

  @FXML
  private void executeHahCompression() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    Dictionary dictionary = currentDictionary()
    UtilityStage<Void> nextStage = createStage(null, null)
    HahCompressionExecutorController controller = HahCompressionExecutorController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    controller.prepare(dictionary.getAlphabetOrder())
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void executeAkrantiain() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    Dictionary dictionary = currentDictionary()
    UtilityStage<Void> nextStage = createStage(null, null)
    AkrantiainExecutorController controller = AkrantiainExecutorController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    controller.prepare(dictionary.getAkrantiain())
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void executeZatlin() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    UtilityStage<Void> nextStage = createStage(null, null)
    ZatlinExecutorController controller = ZatlinExecutorController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void executeCharacterAnalysis() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    UtilityStage<Void> nextStage = createStage(null, null)
    CharacterFrequencyAnalyzerController controller = CharacterFrequencyAnalyzerController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void printDictionary() {
    Dictionary dictionary = currentDictionary()
    if (!dictionary.getElements().isEmpty()) {
      UtilityStage<Void> nextStage = createStage()
      PrintController controller = PrintController.new(nextStage)
      controller.prepare(dictionary)
      nextStage.showAndWait()
    } else {
      showErrorDialog("missingPrintedWord")
    }
  }

  @FXML
  private void showStatistics() {
    Dictionary dictionary = currentDictionary()
    UtilityStage<Void> nextStage = createStage()
    StatisticsController controller = StatisticsController.new(nextStage)
    controller.prepare(dictionary)
    nextStage.showAndWait()
  }

  @FXML
  private void showHelp() {
    UtilityStage<Void> nextStage = createStage()
    HelpController controller = HelpController.new(nextStage)
    nextStage.showAndWait()
  }

  @FXML
  private void showOfficialSite() {
    if (Desktop.isDesktopSupported() && !GraphicsEnvironment.isHeadless()) {
      Desktop desktop = Desktop.getDesktop()
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        URI uri = URI.new(OFFICIAL_SITE_URI)
        desktop.browse(uri)
      } else {
        showErrorDialog("browserUnsupported")
      }
    } else {
      showErrorDialog("desktopUnsupported")
    }
  }

  @FXML
  private void showApplicationInformation() {
    UtilityStage<Void> nextStage = createStage()
    ApplicationInformationController controller = ApplicationInformationController.new(nextStage)
    nextStage.showAndWait()
  }

  @FXML
  private void exit() {
    Boolean allowsClose = requestClose()
    if (allowsClose) {
      Platform.exit()
    }
  }

  @FXML
  public void searchNormal(KeyEvent event) {
    currentWordListController().searchNormal()
  }

  @FXML
  public void searchDetail() {
    currentWordListController().searchDetail()
  }

  @FXML
  private void searchScript() {
    currentWordListController().searchScript()
  }

  @FXML
  private void searchPrevious() {
    currentWordListController().searchPrevious()
  }

  @FXML
  private void searchNext() {
    currentWordListController().searchPrevious()
  }

  @FXML
  private void searchSentence() {
    currentWordListController().searchSentence()
  }

  @FXML
  private void searchHistory() {
    currentWordListController().searchHistory()
  }

  private void changeWordOrder(WordOrderType type) {
    currentWordListController().changeWordOrder(type)
  }

  @FXML
  private void shuffleWords() {
    currentWordListController().shuffleWords()
  }

  private void changeSearchMode(SearchMode mode) {
    currentWordListController().changeSearchMode(mode)
  }

  @FXML
  private void toggleSearchType() {
    currentWordListController().toggleSearchType()
  }

  @FXML
  private void modifyWords() {
    currentWordListController().modifyWords()
  }

  @FXML
  private void removeWords() {
    currentWordListController().removeWords()
  }

  @FXML
  private void addWord() {
    currentWordListController().addWord()
  }

  @FXML
  private void addInheritedWords() {
    currentWordListController().addInheritedWords()
  }

  @FXML
  private void addGeneratedWords() {
    currentWordListController().addGeneratedWords()
  }

  private void badgeWords(Badge badge) {
    currentWordListController().badgeWords(badge)
  }

  @FXML
  private void cutWords() {
    currentWordListController().cutWords()
  }

  @FXML
  private void copyWords() {
    currentWordListController().copyWords()
  }

  @FXML
  private void pasteWords() {
    currentWordListController().pasteWords()
  }

  @FXML
  private void gitInit() {
    currentWordListController().gitInit()
  }

  @FXML
  private void gitAddCommit() {
    currentWordListController().gitAddCommit()
  }

  @FXML
  private void gitCheckout() {
    currentWordListController().gitCheckout()
  }

  @FXML
  private void gitPush() {
    currentWordListController().gitPush()
  }

  private void checkVersion() {
    Version previousVersion = Setting.getInstance().getVersion()
    if (false && previousVersion < Launcher.VERSION) {
      UtilityStage<Void> nextStage = createStage()
      UpdateInformationController controller = UpdateInformationController.new(nextStage)
      nextStage.showAndWait()
    }
  }

  private void saveMainWindowSize() {
    Setting setting = Setting.getInstance()
    if (setting.getPreservesMainWindowSize()) {
      setting.setMainWindowWidth((Int)$scene.getWidth())
      setting.setMainWindowHeight((Int)$scene.getHeight())
    }
  }

  private void closeOpenStages() {
    for (Stage stage : $openStages) {
      stage.close()
    }
    $openStages.clear()
  }

  private Boolean requestClose() {
    Boolean allowsClose = true
    List<Tab> tabs = ArrayList.new($tabPane.getTabs())
    for (Int i = 0 ; i < tabs.size() ; i ++) {
      Tab tab = tabs[i]
      MainWordListController controller = $wordListControllers[i]
      if (controller != null) {
        Boolean allowsCloseTab = controller.requestClose()
        if (allowsCloseTab) {
          $tabPane.getTabs().remove(tab)
        } else {
          allowsClose = false
          break
        }
      }
    }
    if (allowsClose) {
      closeOpenStages()
      saveMainWindowSize()
      Setting.getInstance().save()
      return true
    } else {
      return false
    }
  }

  private void handleException(Throwable throwable) {
    Dictionary dictionary = currentDictionary()
    IndividualSetting individualSetting = currentIndividualSetting()
    if (dictionary != null) {
      dictionary.getDictionaryFactory().saveBackup(dictionary)
    }
    if (individualSetting != null) {
      individualSetting.save()
    }
    Setting.getInstance().save()
    outputStackTrace(throwable, Launcher.BASE_PATH + EXCEPTION_OUTPUT_PATH)
    showErrorDialog("error")
    Platform.exit()
  }

  private void setupTabPane() {
    $tabPane.getSelectionModel().selectedItemProperty().addListener() { ObservableValue<? extends Tab> observableValue, Tab oldValue, Tab newValue ->
      Platform.runLater() {
        MainWordListController controller = currentWordListController()
        Dictionary dictionary = currentDictionary()
        if (controller != null) {
          controller.focusSearchControl()
        }
        if (dictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(dictionary.getPath())
        }
        update()
      }
    }
  }

  private void setupCreateDictionaryMenu() {
    $createDictionaryMenu.getItems().clear()
    for (DictionaryFactory factory : DictionaryFactory.FACTORIES) {
      DictionaryFactory cachedFactory = factory
      MenuItem item = MenuItem.new(factory.getName())
      item.setGraphic(ImageView.new(factory.createIcon()))
      item.setOnAction() {
        createDictionary(cachedFactory)
      }
      if (!factory.isCreatable()) {
        item.setDisable(true)
      }
      $createDictionaryMenu.getItems().add(item)
    }
  }

  private void setupOpenRegisteredDictionaryMenu() {
    $openRegisteredDictionaryMenu.getItems().clear()
    Setting setting = Setting.getInstance()
    List<String> dictionaryPaths = setting.getRegisteredDictionaryPaths()
    List<String> dictionaryNames = setting.getRegisteredDictionaryNames()
    for (Int i = 0 ; i < Math.max(dictionaryPaths.size(), 10) ; i ++) {
      String dictionaryPath = dictionaryPaths[i]
      String dictionaryName = dictionaryNames[i]
      MenuItem item = MenuItem.new()
      item.setMnemonicParsing(false)
      if (dictionaryPath != null) {
        File file = File.new(dictionaryPath)
        item.setText(dictionaryName ?: file.getName())
        item.setOnAction() {
          openRegisteredDictionary(file)
        }
      } else {
        item.setText("未登録")
        item.setDisable(true)
      }
      if (i < 10) {
        Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/dictionary_${(i + 1) % 10}.png"))
        item.setGraphic(ImageView.new(icon))
        item.setAccelerator(KeyCodeCombination.new(KeyCode.valueOf("DIGIT${(i + 1) % 10}"), KeyCombination.SHORTCUT_DOWN))
      }
      $openRegisteredDictionaryMenu.getItems().add(item)
    }
  }

  private void setupRegisterCurrentDictionaryMenu() {
    $registerCurrentDictionaryMenu.getItems().clear()
    List<String> dictionaryPaths = Setting.getInstance().getRegisteredDictionaryPaths()
    for (Int i = 0 ; i < Math.max(dictionaryPaths.size(), 9) + 1 ; i ++) {
      Int j = i
      String dictionaryPath = dictionaryPaths[i]
      MenuItem item = MenuItem.new()
      if (dictionaryPath == null) {
        item.setText("辞書${i + 1}に登録")
        item.setOnAction() {
          registerCurrentDictionary(j)
        }
      } else {
        item.setText("登録済み")
        item.setDisable(true)
      }
      if (i < 10) {
        Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/dictionary_${(i + 1) % 10}.png"))
        item.setGraphic(ImageView.new(icon))
      }
      $registerCurrentDictionaryMenu.getItems().add(item)
    }
  }

  private void setupChangeSearchModeMenu() {
    $changeSearchModeMenu.getItems().clear()
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/empty.png"))
    for (SearchMode mode : SearchMode.values()) {
      SearchMode cachedMode = mode
      MenuItem item = MenuItem.new()
      item.setText(cachedMode.toString())
      item.setGraphic(ImageView.new(icon))
      item.setAccelerator(mode.getAccelerator())
      item.setOnAction() {
        changeSearchMode(cachedMode)
      }
      $changeSearchModeMenu.getItems().add(item)
    }
  }

  private void setupChangeWordOrderMenu() {
    $changeWordOrderMenu.getItems().clear()
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/empty.png"))
    for (WordOrderType type : WordOrderType.values()) {
      WordOrderType cachedType = type
      MenuItem item = MenuItem.new()
      item.setText(type.toString())
      item.setGraphic(ImageView.new(icon))
      item.setOnAction() {
        changeWordOrder(cachedType)
      }
      $changeWordOrderMenu.getItems().add(item)
    }
  }

  private setupPluginMenu() {
    $pluginMenu.getItems().clear()
    for (SimplePlugin plugin : PluginManager.SIMPLE_PLUGINS) {
      MenuItem item = MenuItem.new()
      String name = plugin.getName()
      Image icon = plugin.getIcon() ?: Image.new(getClass().getClassLoader().getResourceAsStream("resource/image/menu/empty.png"))
      KeyCode keyCode = plugin.getKeyCode()
      KeyCombination accelerator = (keyCode != null) ? KeyCodeCombination.new(keyCode, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN) : null
      item.setText(name)
      item.setGraphic(ImageView.new(icon))
      item.setAccelerator(accelerator)
      item.setDisable(true)
      $pluginMenu.getItems().add(item)
    }
  }

  private void setupBadgeWordsMenu() {
    for (Badge badge : Badge.values()) {
      Badge cachedBadge = badge
      MenuItem item = MenuItem.new()
      item.setText(badge.getName())
      item.setGraphic(ImageView.new(badge.getImage()))
      item.setOnAction() {
        badgeWords(cachedBadge)
      }
      $badgeWordsMenu.getItems().add(item)
    }
  }

  private void setupExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler() { Thread thread, Throwable throwable ->
      handleException(throwable)
    }
  }

  private void setupDragAndDrop() {
    $scene.addEventHandler(DragEvent.DRAG_OVER) { DragEvent event ->
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasFiles()) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE)
      }
      event.consume()
    }
    $scene.addEventHandler(DragEvent.DRAG_DROPPED) { DragEvent event ->
      Boolean completed = false
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasFiles()) {
        File file = dragboard.getFiles()[0]
        Platform.runLater() {
          openRegisteredDictionary(file)
        }
        completed = true
      }
      event.setDropCompleted(completed)
      event.consume()
    }
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.L, KeyCombination.SHORTCUT_DOWN).match(event)) {
        MainWordListController controller = currentWordListController()
        if (controller != null) {
          controller.focusWordList()
        }
      }
    }
  }

  private void setupCloseConfirmation() {
    $stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) { WindowEvent event ->
      Boolean allowsClose = requestClose()
      if (!allowsClose) {
        event.consume()
      }
    }
  }

  private void setupDebug() {
    Boolean debugging = Setting.getInstance().isDebugging()
  }

  private void loadOriginalResource() {
    Setting setting = Setting.getInstance()
    Double defaultWidth = setting.getMainWindowWidth()
    Double defaultHeight = setting.getMainWindowHeight()
    loadResource(RESOURCE_PATH, TITLE, defaultWidth, defaultHeight, MIN_WIDTH, MIN_HEIGHT)
  }

}