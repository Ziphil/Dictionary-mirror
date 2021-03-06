package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.PaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordPlainPaneFactory extends PaneFactoryBase<SlimeWord, SlimeDictionary, Pane> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"

  public SlimeWordPlainPaneFactory(SlimeWord word, SlimeDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public SlimeWordPlainPaneFactory(SlimeWord word, SlimeDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    addNameNode(pane, $word.getName(), $word.getNumber())
    List<String> wholeEquivalents = ArrayList.new()
    for (SlimeEquivalent equivalent : $word.getRawEquivalents()) {
      wholeEquivalents.addAll(equivalent.getNames())
    }
    addEquivalentNode(pane, wholeEquivalents)
    modifyBreak(pane)
    return pane
  }

  private void addNameNode(TextFlow pane, String name, Int number) {
    String fontFamily = $dictionary.getNameFontFamily()
    Text nameText = Text.new(name + " ")
    Text breakText = Text.new("\n")
    if (fontFamily != null) {
      nameText.setStyle("-fx-font-family: \"${Strings.escapeUnicode(fontFamily)}\";")
    }
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    pane.getChildren().addAll(nameText, breakText)
  }

  private void addEquivalentNode(TextFlow pane, List<String> equivalents) {
    Text equivalentText = Text.new(equivalents.join($dictionary.firstPunctuation()))
    Text breakText = Text.new("\n")
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    pane.getChildren().addAll(equivalentText, breakText)
  }

}