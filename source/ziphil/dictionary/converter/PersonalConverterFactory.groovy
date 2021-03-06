package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.ConverterFactory
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.IdentityConverter
import ziphil.dictionary.Loader
import ziphil.dictionary.personal.BinaryDictionary
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalConverterFactory extends ConverterFactory {

  public Loader create(DictionaryFactory factory, Dictionary sourceDictionary) {
    Loader converter = null
    if (factory.getDictionaryClass() == PersonalDictionary) {
      if (sourceDictionary instanceof BinaryDictionary) {
        converter = IdentityConverter.new((Dictionary)sourceDictionary)
      } else if (sourceDictionary instanceof ShaleiaDictionary) {
        converter = PersonalShaleiaConverter.new(sourceDictionary)
      } else if (sourceDictionary instanceof SlimeDictionary) {
        converter = PersonalSlimeConverter.new(sourceDictionary)
      }
    }
    return converter
  }

  public Boolean isAvailable(DictionaryFactory factory, Dictionary sourceDictionary) {
    if (factory.getDictionaryClass() == PersonalDictionary) {
      if (sourceDictionary instanceof BinaryDictionary) {
        return true
      } else if (sourceDictionary instanceof ShaleiaDictionary) {
        return true
      } else if (sourceDictionary instanceof SlimeDictionary) {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }

}