package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.ElementPane
import ziphil.custom.Measurement
import ziphil.dictionary.Badge
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.PaneFactoryBase
import ziphil.dictionary.SearchMode
import ziphil.dictionary.SearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.InnerClass
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWordPaneFactory extends PaneFactoryBase<ShaleiaWord, ShaleiaDictionary, ElementPane> {

  private static final String SHALEIA_HEAD_NAME_CLASS = "shaleia-head-name"
  private static final String SHALEIA_PRONUNCIATION_CLASS = "shaleia-pronunciation"
  private static final String SHALEIA_EQUIVALENT_CLASS = "shaleia-equivalent"
  private static final String SHALEIA_SORT_CLASS = "shaleia-sort"
  private static final String SHALEIA_CATEGORY_CLASS = "shaleia-category"
  private static final String SHALEIA_CREATION_DATE_CLASS = "shaleia-creation-date"
  private static final String SHALEIA_CONTENT_TITLE_CLASS = "shaleia-content-title"
  private static final String SHALEIA_MARKER_CLASS = "shaleia-marker"
  private static final String SHALEIA_NAME_CLASS = "shaleia-name"
  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_ITALIC_CLASS = "shaleia-italic"
  private static final String SHALEIA_HAIRIA_CLASS = "shaleia-hairia"
  private static final Char START_NAME_CHARACTER = '['
  private static final Char END_NAME_CHARACTER = ']'
  private static final Char START_LINK_CHARACTER = '{'
  private static final Char END_LINK_CHARACTER = '}'
  private static final Char START_ITALIC_CHARACTER = '/'
  private static final Char END_ITALIC_CHARACTER = '/'
  private static final Char START_ESCAPE_CHARACTER = '&'
  private static final Char END_ESCAPE_CHARACTER = ';'
  private static final Char START_HAIRIA_CHARACTER = 'H'
  private static final String PUNCTUATIONS = " .,?!-"

  public ShaleiaWordPaneFactory(ShaleiaWord word, ShaleiaDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public ShaleiaWordPaneFactory(ShaleiaWord word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  protected ElementPane doCreate() {
    Int lineSpacing = Setting.getInstance().getLineSpacing()
    VBox pane = VBox.new()
    Map<Badge, Node> badgeNodes = EnumMap.new(Badge)
    TextFlow mainPane = TextFlow.new()
    TextFlow contentPane = TextFlow.new()
    TextFlow synonymPane = TextFlow.new()
    Boolean hasContent = false
    Boolean hasSynonym = false
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    mainPane.setLineSpacing(lineSpacing)
    contentPane.setLineSpacing(lineSpacing)
    ShaleiaDescriptionReader reader = ShaleiaDescriptionReader.new($word.getDescription())
    try {
      while (reader.readLine() != null) {
        if (mainPane.getChildren().isEmpty()) {
          String name = $word.getDisplayedName()
          addBadgeNodes(mainPane, badgeNodes)
          addNameNode(mainPane, name, $word.createPronunciation())
        }
        if (reader.findCreationDate()) {
          String sort = reader.lookupSort()
          String creationDate = reader.lookupCreationDate()
          addCreationDateNode(mainPane, sort, creationDate)
        }
        if (reader.findEquivalent()) {
          String category = reader.lookupCategory()
          String equivalent = reader.lookupEquivalent()
          addEquivalentNode(mainPane, category, equivalent)
        }
        if (reader.findContent()) {
          String title = reader.title()
          String content = reader.lookupContent()
          addContentNode(contentPane, title, content)
          hasContent = true
        }
        if (reader.findSynonym()) {
          String synonymType = reader.lookupSynonymType()
          String synonym = reader.lookupSynonym()
          addSynonymNode(synonymPane, synonymType, synonym)
          hasSynonym = true
        }
      }
      modifyBreak(mainPane)
      modifyBreak(contentPane)
      modifyBreak(synonymPane)
      pane.getChildren().add(mainPane)
      if (hasContent) {
        addSeparator(pane)
        pane.getChildren().add(contentPane)
      }
      if (hasSynonym) {
        addSeparator(pane)
        pane.getChildren().add(synonymPane)
      }
    } finally {
      reader.close()
    }
    return ElementPane.new(pane, badgeNodes)
  }

  private void addNameNode(TextFlow pane, String name, String pronunciation) {
    if (!pronunciation.isEmpty()) {
      Text nameText = Text.new(name + " ")
      Text pronunciationText = Text.new(pronunciation)
      Text spaceText = Text.new("  ")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
      pronunciationText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_PRONUNCIATION_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
      pane.getChildren().addAll(nameText, pronunciationText, spaceText)
    } else {
      Text nameText = Text.new(name + "  ")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
      pane.getChildren().add(nameText)
    }
  }
 
  private void addCreationDateNode(TextFlow pane, String sort, String creationDate) {
    Label sortText = Label.new(sort)
    Text creationDateText = Text.new(" " + creationDate)
    Text breakText = Text.new("\n")
    sortText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_SORT_CLASS)
    creationDateText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_CREATION_DATE_CLASS)
    pane.getChildren().addAll(sortText, creationDateText, breakText)
  }

  private void addEquivalentNode(TextFlow pane, String category, String equivalent) {
    Label categoryText = Label.new(category)
    Text spaceText = Text.new(" ")
    Text breakText = Text.new("\n")
    List<Node> equivalentTexts = createRichTexts(equivalent)
    categoryText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_CATEGORY_CLASS)
    spaceText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_EQUIVALENT_CLASS)
    for (Node equivalentText : equivalentTexts) {
      equivalentText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_EQUIVALENT_CLASS)
    }
    pane.getChildren().addAll(categoryText, spaceText)
    pane.getChildren().addAll(equivalentTexts)
    pane.getChildren().add(breakText)
  }

  private void addContentNode(TextFlow pane, String title, String content) {
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation()
    String modifiedContent = (modifiesPunctuation) ? Strings.modifyPunctuation(content) : content
    Label titleText = Label.new(title)
    Text innerBreakText = Text.new("\n")
    Text breakText = Text.new("\n")
    List<Node> contentTexts = createRichTexts(modifiedContent)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_CONTENT_TITLE_CLASS)
    innerBreakText.getStyleClass().addAll(CONTENT_CLASS, SMALL_CLASS)
    for (Node contentText : contentTexts) {
      contentText.getStyleClass().add(CONTENT_CLASS)
    }
    pane.getChildren().addAll(titleText, innerBreakText)
    pane.getChildren().addAll(contentTexts)
    pane.getChildren().add(breakText)
  }

  private void addSynonymNode(TextFlow pane, String synonymType, String synonym) {
    String marker = Setting.getInstance().getRelationMarker()
    Text markerText = Text.new(marker)
    Text markerSpaceText = Text.new(" ")
    Label synonymTypeText = Label.new(synonymType)
    Text spaceText = Text.new(" ")
    Text breakText = Text.new("\n")
    List<Node> synonymTexts = createRichTexts(synonym + " ", true)
    markerText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_MARKER_CLASS)
    markerText.getStyleClass().addAll(CONTENT_CLASS)
    synonymTypeText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_CATEGORY_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    for (Node synonymText : synonymTexts) {
      synonymText.getStyleClass().add(CONTENT_CLASS)
    }
    if (!synonymType.isEmpty()) {
      pane.getChildren().addAll(markerText, markerSpaceText, synonymTypeText, spaceText)
    } else {
      pane.getChildren().addAll(markerText, spaceText)
    }
    pane.getChildren().addAll(synonymTexts)
    pane.getChildren().add(breakText)
  }

  private void addSeparator(Pane pane) {
    Boolean showsSeparator = Setting.getInstance().getShowsSeparator()
    if (showsSeparator) {
      Separator separator = Separator.new()
      separator.getStyleClass().addAll(CONTENT_CLASS, SEPARATOR_CLASS)
      pane.getChildren().add(separator)
    } else {
      VBox box = VBox.new()
      box.getStyleClass().addAll(CONTENT_CLASS, MARGIN_CLASS)
      pane.getChildren().add(box)
    }
  }

  private List<Node> createRichTexts(String string, Boolean decoratesLink) {
    List<Node> texts = ArrayList.new()
    List<Node> unnamedTexts = ArrayList.new()
    StringBuilder currentString = StringBuilder.new()
    StringBuilder currentEscapeString = StringBuilder.new()
    StringBuilder currentName = StringBuilder.new()
    TextMode currentMode = TextMode.NORMAL
    for (int i = 0 ; i < string.length() ; i ++) {
      Char character = string.charAt(i);
      if (currentMode == TextMode.NORMAL && character == START_LINK_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.LINK
      } else if ((currentMode == TextMode.LINK || currentMode == TextMode.LINK_ITALIC) && character == END_LINK_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        if (currentName.length() > 0) {
          String name = currentName.toString()
          for (Node unnamedText : unnamedTexts) {
            unnamedText.addEventHandler(MouseEvent.MOUSE_CLICKED, createLinkEventHandler(name))
          }
          currentName.setLength(0)
          unnamedTexts.clear()
        }
        currentMode = TextMode.NORMAL
      } else if ((currentMode == TextMode.LINK || currentMode == TextMode.LINK_ITALIC) && PUNCTUATIONS.indexOf((Int)character) >= 0) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        if (currentName.length() > 0) {
          String name = currentName.toString()
          for (Node unnamedText : unnamedTexts) {
            unnamedText.addEventHandler(MouseEvent.MOUSE_CLICKED, createLinkEventHandler(name))
          }
          currentName.setLength(0)
          unnamedTexts.clear()
        }
        Text characterText = Text.new(String.valueOf(character))
        characterText.getStyleClass().add(SHALEIA_NAME_CLASS)
        texts.add(characterText)    
      } else if (currentMode == TextMode.LINK && character == START_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        currentMode = TextMode.LINK_ITALIC
      } else if (currentMode == TextMode.LINK_ITALIC && character == END_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_ITALIC_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        currentMode = TextMode.LINK
      } else if (currentMode == TextMode.NORMAL && character == START_NAME_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }      
        currentMode = TextMode.NAME
      } else if ((currentMode == TextMode.NAME || currentMode == TextMode.NAME_ITALIC) && character == END_NAME_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NORMAL
      } else if (currentMode == TextMode.NAME && character == START_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NAME_ITALIC
      } else if (currentMode == TextMode.NAME_ITALIC && character == END_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_ITALIC_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }      
        currentMode = TextMode.NAME
      } else if (currentMode == TextMode.NORMAL && character == START_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NORMAL_ITALIC
      } else if (currentMode == TextMode.NORMAL_ITALIC && character == END_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_ITALIC_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NORMAL
      } else {
        if (character == START_ESCAPE_CHARACTER) {
          StringBuilder escapeString = StringBuilder.new()
          for (; i < string.length() ; i ++) {
            Char escapeCharacter = string.charAt(i)
            if (escapeCharacter == END_ESCAPE_CHARACTER) {
              String convertedEscapeString = CharacterClass.toChars(IntegerClass.parseInt(escapeString.toString(), 16))[0]
              currentString.append(convertedEscapeString)
              currentName.append(convertedEscapeString)
              break
            } else if (escapeCharacter ==~ /[0-9A-Fa-f]/) {
              escapeString.append(escapeCharacter)
            }
          }
        } else if (character == START_HAIRIA_CHARACTER && string.charAt(i + 1) ==~ /[0-9]/) {
          Text text = Text.new(currentString.toString())
          Label hairiaText = Label.new(String.valueOf(START_HAIRIA_CHARACTER))
          hairiaText.getStyleClass().addAll(SHALEIA_HAIRIA_CLASS)
          texts.addAll(text, hairiaText)
          currentString.setLength(0)
          currentName.setLength(0)
        } else {
          currentString.append(character)
          currentName.append(character)
        }
      }
    }
    if (currentString.length() > 0) {
      Text text = Text.new(currentString.toString())
      texts.add(text)
    }
    return texts
  }

  private List<Node> createRichTexts(String string) {
    return createRichTexts(string, false)
  }

  private EventHandler<MouseEvent> createLinkEventHandler(String name) {
    EventHandler<MouseEvent> handler = { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        if ($linkClickType != null && $linkClickType.matches(event)) {
          SearchParameter parameter = NormalSearchParameter.new(name, SearchMode.NAME, true, true)
          $dictionary.getOnLinkClicked().accept(parameter)
        }
      }
    }
    return handler
  }

}


@InnerClass(ShaleiaWordPaneFactory)
@CompileStatic @Ziphilify
private static enum TextMode {

  NORMAL,
  NORMAL_ITALIC,
  NAME,
  NAME_ITALIC,
  LINK,
  LINK_ITALIC

}