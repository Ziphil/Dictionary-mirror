package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public class PersonalWord extends Word {

  private PersonalDictionary $dictionary
  private String $name = ""
  private List<String> $equivalents = ArrayList.new()
  private String $pronunciation = ""
  private String $translation = ""
  private String $usage = ""
  private Integer $level = 0
  private Integer $memory = 0
  private Integer $modification = 0
  private String $content = ""
  private VBox $contentPane = VBox.new()
  private Boolean $isChanged = true

  public PersonalWord(String name, String pronunciation, String translation, String usage, Integer level, Integer memory, Integer modification) {
    update(name, pronunciation, translation, usage, level, memory, modification)
    setupContentPane()
  }

  public void update(String name, String pronunciation, String translation, String usage, Integer level, Integer memory, Integer modification) {
    $name = name
    $pronunciation = pronunciation
    $translation = translation
    $usage = usage
    $level = level
    $memory = memory
    $modification = modification
    $content = name + "\n" + translation + "\n" + usage
    $isChanged = true
  }

  public void createContentPane() {
    HBox headBox = HBox.new()
    VBox translationBox = VBox.new()
    VBox usageBox = VBox.new()
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation() ?: false
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, translationBox, usageBox)
    addNameNode(headBox, $name)
    addOtherNode(translationBox, $translation, modifiesPunctuation)
    addOtherNode(usageBox, $usage, modifiesPunctuation)
    $isChanged = false
  }

  private void addNameNode(HBox box, String name) {
    Label nameText = Label.new(name)
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS)
    box.getChildren().add(nameText)
  }

  private void addOtherNode(VBox box, String other, Boolean modifiesPunctuation) {
    String newOther = (modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    TextFlow textFlow = TextFlow.new()
    Text otherText = Text.new(newOther)
    otherText.getStyleClass().add(CONTENT_CLASS)
    textFlow.getChildren().add(otherText)
    box.getChildren().add(textFlow)
  }

  private void setupContentPane() {
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
  }

  public Boolean isChanged() {
    return $isChanged
  }

  public PersonalDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(PersonalDictionary dictionary) {
    $dictionary = dictionary
  }

  public String getName() {
    return $name
  }

  public List<String> getEquivalents() {
    return null
  }

  public String getPronunciation() {
    return $pronunciation
  }

  public String getTranslation() {
    return $translation
  }

  public String getUsage() {
    return $usage
  }

  public Integer getLevel() {
    return $level
  }

  public Integer getMemory() {
    return $memory
  }

  public Integer getModification() {
    return $modification
  }

  public String getContent() {
    return $content
  }

  public Pane getContentPane() {
    return $contentPane
  }

}