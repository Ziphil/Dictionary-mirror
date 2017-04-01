package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  public static final String PUNCTUATION_IDENTIIER_NAME = "PUNCTUATION"

  private AkrantiainSetting $setting = AkrantiainSetting.new()
  private String $source = null

  public void load(File file, Boolean keepsSource) {
    if (keepsSource) {
      $source = file.getText()
    }
    Reader reader = InputStreamReader.new(FileInputStream.new(file), "UTF-8")
    parse(reader)
  }

  public void load(String string, Boolean keepsSource) {
    if (keepsSource) {
      $source = string
    }
    Reader reader = StringReader.new(string)
    parse(reader)
  }

  public void load(File file) {
    load(file, false)
  }

  public void load(String string) {
    load(string, false)
  }

  private void parse(Reader reader) {
    AkrantiainLexer lexer = AkrantiainLexer.new(reader)
    List<AkrantiainToken> currentTokens = ArrayList.new()
    for (AkrantiainToken token ; (token = lexer.nextToken()) != null ;) {
      if (token.getType() != AkrantiainTokenType.SEMICOLON) {
        currentTokens.add(token)
      } else {
        AkrantiainSentenceParser parser = AkrantiainSentenceParser.new(currentTokens)
        if (parser.isEnvironmentSentence()) {
          AkrantiainEnvironment environment = parser.parseEnvironment()
          $setting.getEnvironments().add(environment)
        } else if (parser.isDefinitionSentence()) {
          AkrantiainDefinition definition = parser.parseDefinition()
          if (!$setting.containsIdentifier(definition.getIdentifier())) {
            $setting.getDefinitions().add(definition)
          } else {
            Integer lineNumber = currentTokens[0].getLineNumber()
            throw AkrantiainParseException.new("Duplicate identifier", lineNumber)
          }
        } else if (parser.isRuleSentence()) {
          AkrantiainRule rule = parser.parseRule()
          $setting.getRules().add(rule)
        } else {
          Integer lineNumber = (!currentTokens.isEmpty()) ? currentTokens[0].getLineNumber() : null
          throw AkrantiainParseException.new("Invalid sentence", lineNumber)
        }
        currentTokens.clear()
      }
    }
    reader.close()
    lexer.close()
  }

  public String convert(String input) {
    AkrantiainElementGroup currentGroup = AkrantiainElementGroup.create(input)
    for (AkrantiainRule rule : $setting.getRules()) {
      currentGroup = rule.apply(currentGroup, $setting)
    }
    AkrantiainElement invalidElement = currentGroup.firstInvalidElement($setting)
    if (invalidElement == null) {
      return currentGroup.createOutput()
    } else {
      throw AkrantiainException.new("No rules that can handle \"${invalidElement.getPart()}\"")
    }
  }

  public String getSource() {
    return $source
  }

}