package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify 
public class Dictionaries {

  public static Dictionary loadDictionary(File file) {
    if (file != null) {
      if (file.exists() && file.isFile()) {
        Dictionary dictionary
        String fileName = file.getName()
        String filePath = file.getPath()
        if (filePath.endsWith(".xdc")) {
          dictionary = ShaleiaDictionary.new(fileName, filePath)
        } else if (filePath.endsWith(".csv")) {
          dictionary = PersonalDictionary.new(fileName, filePath)
        } else if (filePath.endsWith(".json")) {
          dictionary = SlimeDictionary.new(fileName, filePath)
        }
        return dictionary
      } else {
        return null
      }
    } else {
      return null
    }
  }

  public static Dictionary loadEmptyDictionary(File file, String extension) {
    if (file != null) {
      Dictionary dictionary
      String fileName = file.getName()
      String filePath = file.getPath()
      if (extension == "xdc") {
        dictionary = ShaleiaDictionary.new(fileName, null)
        dictionary.setPath(filePath)
      } else if (extension == "csv") {
        dictionary = PersonalDictionary.new(fileName, null)
        dictionary.setPath(filePath)
      } else if (extension == "json") {
        dictionary = SlimeDictionary.new(fileName, null)
        dictionary.setPath(filePath)
      }
      return dictionary
    } else {
      return null
    }
  }

  public static Dictionary convertDictionary(File file, String newExtension, Dictionary oldDictionary) {
    if (file != null) {
      Dictionary dictionary = null
      String fileName = file.getName()
      String filePath = file.getPath()
      if (newExtension == "xdc") {
        dictionary = ShaleiaDictionary.new(fileName, filePath, oldDictionary)
      } else if (newExtension ==  "csv") {
        dictionary = PersonalDictionary.new(fileName, filePath, oldDictionary)
      } else if (newExtension == "json") {
        dictionary = SlimeDictionary.new(fileName, filePath, oldDictionary)
      }
      return dictionary
    } else {
      return null
    }
  }

  public static String getExtension(Dictionary dictionary) {
    String extension
    if (dictionary instanceof ShaleiaDictionary) {
      extension = "xdc"
    } else if (dictionary instanceof PersonalDictionary) {
      extension = "csv"
    } else if (dictionary instanceof SlimeDictionary) {
      extension = "json"
    }
    return extension
  }

}