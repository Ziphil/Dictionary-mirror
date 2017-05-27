package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import java.text.Normalizer
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainElementGroup {

  private List<AkrantiainElement> $elements = ArrayList.new()

  public static AkrantiainElementGroup create(String input, AkrantiainModule module) {
    AkrantiainElementGroup group = AkrantiainElementGroup.new()
    if (!module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE) && !module.containsEnvironment(AkrantiainEnvironment.CONSERVE_CASE)) {
      input = input.toLowerCase()
    }
    if (module.containsEnvironment(AkrantiainEnvironment.USE_NFD)) {
      input = Normalizer.normalize(input, Normalizer.Form.NFD)
    }
    for (Integer i : 0 ..< input.length()) {
      AkrantiainElement element = AkrantiainElement.new(input[i], i + 1)
      group.getElements().add(element)
    }
    return group
  }

  public AkrantiainElementGroup plus(AkrantiainElementGroup group) {
    AkrantiainElementGroup newGroup = AkrantiainElementGroup.new()
    newGroup.getElements().addAll($elements)
    newGroup.getElements().addAll(group.getElements())
    return newGroup
  }

  // インデックスが from から to まで (to は含まない) の要素を 1 つに合成した要素を返します。
  // 返される要素の part の値は、合成前の part をインデックス順に繋げたものになります。
  // 返される要素の result の値は、合成前の各要素の result の値に関わらず null になります。
  public AkrantiainElement merge(Integer from, Integer to) {
    StringBuilder mergedPart = StringBuilder.new()
    for (Integer i : from ..< to) {
      mergedPart.append($elements[i].getPart())
    }
    Integer columnNumber = null
    if (from < $elements.size()) {
      columnNumber = $elements[from].getColumnNumber()
    } else {
      columnNumber = $elements[-1].getColumnNumber()
    }
    return AkrantiainElement.new(mergedPart.toString(), columnNumber)
  }

  // インデックスが from から to まで (to は含まない) の要素を 1 文字ごとに分割した要素グループを返します。
  // 返される要素グループに含まれる全ての要素の result の値は、常に null になります。
  public AkrantiainElementGroup devide(Integer from, Integer to) {
    AkrantiainElementGroup group = AkrantiainElementGroup.new()
    for (Integer i : from ..< to) {
      group.getElements().addAll($elements[i].devide().getElements())
    }
    return group
  }

  public List<AkrantiainElement> invalidElements(AkrantiainModule module) {
    List<AkrantiainElement> invalidElements = ArrayList.new()
    for (AkrantiainElement element : $elements) {
      if (!element.isValid(module)) {
        invalidElements.add(element)
      }
    }
    return invalidElements
  }

  // 各要素の変換後の文字列を連結し、出力文字列を作成します。
  // 変換がなされていない要素が含まれていた場合は、句読点類であればスペース 1 つを連結し、そうでなければ変換前の文字列を連結します。
  // したがって、このメソッドを実行する前に、全ての要素が正当であるかどうかを invalidElements メソッドなどで確認してください。
  public String createOutput(AkrantiainModule module) {
    StringBuilder output = StringBuilder.new()
    for (AkrantiainElement element : $elements) {
      if (element.getResult() != null) {
        output.append(element.getResult())
      } else {
        if (element.isValid(module)) {
          output.append(" ")
        } else {
          output.append(element.getPart())
        }
      }
    }
    String outputString = output.toString()
    if (module.containsEnvironment(AkrantiainEnvironment.USE_NFD)) {
      outputString = Normalizer.normalize(outputString, Normalizer.Form.NFC)
    }
    return outputString
  }

  public Boolean isAllConverted(Integer from, Integer to) {
    Boolean allConverted = true
    for (Integer i : from ..< to) {
      if (!$elements[i].isConverted()) {
        allConverted = false
        break
      }
    }
    return allConverted
  }

  public Boolean isNoneConverted(Integer from, Integer to) {
    Boolean noneConverted = true
    for (Integer i : from ..< to) {
      if ($elements[i].isConverted()) {
        noneConverted = false
        break
      }
    }
    return noneConverted
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("[")
    for (Integer i : 0 ..< $elements.size()) {
      string.append($elements[i])
      if (i < $elements.size() - 1) {
        string.append(", ")
      }
    }
    string.append("]")
    return string.toString()
  }

  public List<AkrantiainElement> getElements() {
    return $elements
  }

  public void setElements(List<AkrantiainElement> elements) {
    $elements = elements
  }

}