package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionaryFactory extends DictionaryFactory {

  private static final String NAME = "シャレイア語辞典形式"
  private static final String EXTENSION = "xdc"
  private static final String ICON_PATH = "resource/icon/xdc_dictionary.png"

  protected Dictionary create(File file, DictionaryLoader loader) {
    if (loader != null) {
      Dictionary dictionary = ShaleiaDictionary.new(file.getName(), file.getPath(), loader)
      return dictionary
    } else {
      Dictionary dictionary = ShaleiaDictionary.new(file.getName(), file.getPath())
      return dictionary
    }
  }

  protected DictionaryLoader createLoader(File file) {
    ShaleiaDictionaryLoader loader = ShaleiaDictionaryLoader.new(file.getPath())
    return loader
  }

  protected DictionarySaver createSaver() {
    ShaleiaDictionarySaver saver = ShaleiaDictionarySaver.new()
    return saver
  }

  public Image createIcon() {
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream(ICON_PATH))
    return icon
  }

  public Boolean isCreatable() {
    return true
  }

  public String getName() {
    return NAME
  }

  public String getExtension() {
    return EXTENSION
  }

  public Class<? extends Dictionary> getDictionaryClass() {
    return ShaleiaDictionary
  }

}