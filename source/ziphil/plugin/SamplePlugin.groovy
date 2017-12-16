package ziphil.plugin

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import ziphil.dictionary.Dictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SamplePlugin implements Plugin {

  private static final String NAME = "サンプル"

  public void call(Dictionary dictionary) {
    println("This is a sample")
  }

  public Boolean isSupported(Dictionary dictionary) {
    return true
  }

  public String getName() {
    return NAME
  }

  public KeyCode getKeyCode() {
    return KeyCode.BACK_SLASH
  }

  public Image getIcon() {
    return null
  }

}