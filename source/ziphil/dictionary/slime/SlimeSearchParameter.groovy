package ziphil.dictionary.slime

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic
import ziphil.dictionary.Badge
import ziphil.dictionary.BadgePreference
import ziphil.dictionary.DetailedSearchParameter
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SearchType
import ziphilib.transform.Ziphilify


@JsonIgnoreProperties(ignoreUnknown=true)
@CompileStatic @Ziphilify
public class SlimeSearchParameter implements DetailedSearchParameter<SlimeWord> {

  private Int $number
  private String $name
  private SearchType $nameSearchType
  private String $equivalentName
  private String $equivalentTitle
  private SearchType $equivalentSearchType
  private String $informationText
  private String $informationTitle
  private SearchType $informationSearchType
  private String $tag
  private Badge $badge
  private Boolean $hasNumber = false
  private Boolean $hasName = false
  private Boolean $hasEquivalent = false
  private Boolean $hasInformation = false
  private Boolean $hasTag = false
  private Boolean $hasBadge = false
  private Dictionary $dictionary

  public SlimeSearchParameter(Int number) {
    $number = number
    $hasNumber = true
  }

  public SlimeSearchParameter(String name) {
    $name = name
    $hasName = true
  }

  public SlimeSearchParameter() {
  }

  public void preprocess(Dictionary dictionary) {
    $dictionary = dictionary
  }

  public Boolean matches(SlimeWord word) {
    Boolean predicate = true
    Int number = word.getNumber()
    String name = word.getName()
    List<SlimeEquivalent> equivalents = word.getRawEquivalents()
    List<SlimeInformation> informations = word.getInformations()
    List<String> tags = word.getTags()
    if ($hasNumber) {
      if (number != $number) {
        predicate = false
      }
    }
    if ($hasName) {
      if (!$nameSearchType.matches(name, $name)) {
        predicate = false
      }
    }
    if ($hasEquivalent) {
      Boolean equivalentPredicate = false
      for (SlimeEquivalent equivalent : equivalents) {
        String equivalentTitle = equivalent.getTitle()
        for (String equivalentName : equivalent.getNames()) {
          if ($equivalentSearchType.matches(equivalentName, $equivalentName ?: "") && ($equivalentTitle == null || equivalentTitle == $equivalentTitle)) {
            equivalentPredicate = true
          }
        }
      }
      if (!equivalentPredicate) {
        predicate = false
      }
    }
    if ($hasInformation) {
      Boolean informationPredicate = false
      for (SlimeInformation information : informations) {
        String informationText = information.getText()
        String informationTitle = information.getTitle()
        if ($informationSearchType.matches(informationText, $informationText ?: "") && ($informationTitle == null || informationTitle == $informationTitle)) {
          informationPredicate = true
        }
      }
      if (!informationPredicate) {
        predicate = false
      }
    }
    if ($hasTag) {
      Boolean tagPredicate = false
      for (String tag : tags) {
        if (tag == $tag) {
          tagPredicate = true
        }
      }
      if (!tagPredicate) {
        predicate = false
      }
    }
    if ($hasBadge) {
      BadgePreference preference = $dictionary.getIndividualSetting().getBadgePreference()
      if (!preference.contains(word, $badge)) {
        predicate = false
      }
    }
    return predicate
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($hasNumber) {
      string.append("ID[")
      string.append($number)
      string.append("], ")
    }
    if ($hasName) {
      string.append("単語[")
      string.append($name)
      string.append("], ")
    }
    if ($hasEquivalent) {
      string.append("訳語[")
      if ($equivalentTitle != null) {
        string.append($equivalentTitle)
        string.append(": ")
      }
      string.append($equivalentName ?: "")
      string.append("], ")
    }
    if ($hasInformation) {
      string.append("内容[")
      if ($informationTitle != null) {
        string.append($informationTitle)
        string.append(": ")
      }
      string.append($informationText ?: "")
      string.append("], ")
    }
    if ($hasTag) {
      string.append("タグ[")
      string.append($tag)
      string.append("], ")
    }
    if ($hasBadge) {
      string.append("マーカー[")
      string.append($badge)
      string.append("], ")
    }
    if (string.length() >= 2) {
      string.setLength(string.length() - 2)
    }
    return string.toString()
  }

  public Int getNumber() {
    return $number
  }

  public void setNumber(Int number) {
    $number = number
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public SearchType getNameSearchType() {
    return $nameSearchType
  }

  public void setNameSearchType(SearchType nameSearchType) {
    $nameSearchType = nameSearchType
  }

  public String getEquivalentName() {
    return $equivalentName
  }

  public void setEquivalentName(String equivalentName) {
    $equivalentName = equivalentName
  }

  public String getEquivalentTitle() {
    return $equivalentTitle
  }

  public void setEquivalentTitle(String equivalentTitle) {
    $equivalentTitle = equivalentTitle
  }

  public SearchType getEquivalentSearchType() {
    return $equivalentSearchType
  }

  public void setEquivalentSearchType(SearchType equivalentSearchType) {
    $equivalentSearchType = equivalentSearchType
  }

  public String getInformationText() {
    return $informationText
  }

  public void setInformationText(String informationText) {
    $informationText = informationText
  }

  public String getInformationTitle() {
    return $informationTitle
  }

  public void setInformationTitle(String informationTitle) {
    $informationTitle = informationTitle
  }

  public SearchType getInformationSearchType() {
    return $informationSearchType
  }

  public void setInformationSearchType(SearchType informationSearchType) {
    $informationSearchType = informationSearchType
  }

  public String getTag() {
    return $tag
  }

  public void setTag(String tag) {
    $tag = tag
  }

  public Badge getBadge() {
    return $badge
  }

  public void setBadge(Badge badge) {
    $badge = badge
  }

  @JsonGetter("hasNumber")
  public Boolean hasNumber() {
    return $hasNumber
  }

  public void setHasNumber(Boolean hasNumber) {
    $hasNumber = hasNumber
  }

  @JsonGetter("hasName")
  public Boolean hasName() {
    return $hasName
  }

  public void setHasName(Boolean hasName) {
    $hasName = hasName
  }

  @JsonGetter("hasEquivalent")
  public Boolean hasEquivalent() {
    return $hasEquivalent
  }

  public void setHasEquivalent(Boolean hasEquivalent) {
    $hasEquivalent = hasEquivalent
  }

  @JsonGetter("hasInformation")
  public Boolean hasInformation() {
    return $hasInformation
  }

  public void setHasInformation(Boolean hasInformation) {
    $hasInformation = hasInformation
  }

  @JsonGetter("hasTag")
  public Boolean hasTag() {
    return $hasTag
  }

  public void setHasTag(Boolean hasTag) {
    $hasTag = hasTag
  }

  @JsonGetter("hasBadge")
  public Boolean hasBadge() {
    return $hasBadge
  }

  public void setHasBadge(Boolean hasBadge) {
    $hasBadge = hasBadge
  }

}