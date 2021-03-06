package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeRelation {

  private String $title = ""
  private SlimeWord $word

  public SlimeRelation(String title, SlimeWord word) {
    $title = title
    $word = word
  }

  public SlimeRelation() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public SlimeWord getWord() {
    return $word
  }

  public void setWord(SlimeWord word) {
    $word = word
  }

}