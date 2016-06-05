package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow


@CompileStatic @Newify
public class PersonalWord extends Word {

  private String $name = ""
  private String $translation = ""
  private String $usage = ""
  private Integer $level = 0
  private Integer $memory = 0
  private Integer $modification = 0
  private String $pronunciation = ""
  private String $content = ""
  private String $data = ""
  private List<String> $equivalents = ArrayList.new()
  private VBox $contentPane = VBox.new()

  public PersonalWord(String name, String translation, String usage, Integer level, Integer memory, Integer modification, String pronunciation) {
    update(name, translation, usage, level, memory, modification, pronunciation)
  }

  public void update(String name, String translation, String usage, Integer level, Integer memory, Integer modification, String pronunciation) {
    $name = name
    $translation = translation
    $usage = usage
    $level = level
    $memory = memory
    $modification = modification
    $pronunciation = pronunciation
    Label nameText = Label.new(name)
    Text translationText = Text.new(translation)
    Text usageText = Text.new(usage)
    TextFlow translationTextFlow = TextFlow.new(translationText)
    TextFlow usageTextFlow = TextFlow.new(usageText)
    nameText.getStyleClass().addAll("content-text", "head-name")
    translationText.getStyleClass().add("content-text")
    usageText.getStyleClass().add("content-text")
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(nameText, translationTextFlow, usageTextFlow)
    $content = name + "\n" + translation + "\n" + usage
  }

  public String getName() {
    return $name
  }

  public List<String> getEquivalents() {
    return null
  }

  public String getContent() {
    return $content
  }

  public String getData() {
    return $data
  }

  public Pane getContentPane() {
    return $contentPane
  }

}