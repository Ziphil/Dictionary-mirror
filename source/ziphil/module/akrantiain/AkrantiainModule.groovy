package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainModule {

  private static final String PUNCTUATION_IDENTIIER_NAME = "PUNCTUATION"

  private AkrantiainModuleName $name = AkrantiainModuleName.new()
  private Set<AkrantiainEnvironment> $environments = Collections.synchronizedSet(EnumSet.of(AkrantiainEnvironment.CUSTOM))
  private List<AkrantiainDefinition> $definitions = Collections.synchronizedList(ArrayList.new())
  private List<AkrantiainRule> $rules = Collections.synchronizedList(ArrayList.new())
  private List<AkrantiainModuleName> $moduleChain = Collections.synchronizedList(ArrayList.new())

  public String convert(String input, AkrantiainRoot root) {
    String currentOutput = input
    currentOutput = convertByRule(currentOutput, root)
    currentOutput = convertByModuleChain(currentOutput, root)
    return currentOutput
  }

  private String convertByRule(String input, AkrantiainRoot root) {
    if (!$rules.isEmpty()) {
      AkrantiainElementGroup currentGroup = AkrantiainElementGroup.create(input, this)
      for (AkrantiainRule rule : $rules) {
        currentGroup = rule.apply(currentGroup, this)
      }
      List<AkrantiainElement> invalidElements = currentGroup.invalidElements(this)
      if (invalidElements.isEmpty() || $environments.contains(AkrantiainEnvironment.FALL_THROUGH)) {
        return currentGroup.createOutput(this)
      } else {
        throw AkrantiainException.new("No rules that can handle some characters", invalidElements)
      }
    } else {
      return input
    }
  }

  private String convertByModuleChain(String input, AkrantiainRoot root) {
    String currentOutput = input
    for (AkrantiainModuleName moduleName : $moduleChain) {
      AkrantiainModule module = root.findModuleOf(moduleName)
      if (module != null) {
        currentOutput = module.convert(currentOutput, root)
      } else {
        throw AkrantiainException.new("This cannot happen")
      }
    }
    return currentOutput
  }

  public AkrantiainDefinition findDefinitionOf(String identifierName) {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return definition
      }
    }
    return null
  }

  public AkrantiainMatchable findContentOf(String identifierName) {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return definition.getContent()
      }
    }
    return null
  }

  public AkrantiainMatchable findPunctuationContent() {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == PUNCTUATION_IDENTIIER_NAME) {
        return definition.getContent()
      }
    }
    return AkrantiainDisjunction.EMPTY_DISJUNCTION
  }

  public AkrantiainToken findUnknownIdentifier() {
    for (AkrantiainDefinition definition : $definitions) {
      AkrantiainToken unknownIdentifier = definition.findUnknownIdentifier(this)
      if (unknownIdentifier != null) {
        return unknownIdentifier
      }
    }
    for (AkrantiainRule rule : $rules) {
      AkrantiainToken unknownIdentifier = rule.findUnknownIdentifier(this)
      if (unknownIdentifier != null) {
        return unknownIdentifier
      }
    }
    return null
  }

  public AkrantiainToken findCircularIdentifier() {
    for (AkrantiainDefinition definition : $definitions) {
      AkrantiainToken circularIdentifier = definition.findCircularIdentifier(this)
      if (circularIdentifier != null) {
        return circularIdentifier
      }
    }
    return null
  }

  public AkrantiainModuleName findUnknownModuleName(AkrantiainRoot root) {
    for (AkrantiainModuleName moduleName : $moduleChain) {
      if (!root.containsModuleOf(moduleName)) {
        return moduleName
      }
    }
    return null
  }

  public AkrantiainModuleName findCircularModuleName(List<AkrantiainModuleName> moduleNames, AkrantiainRoot root) {
    AkrantiainModuleName containedModuleName = null
    for (AkrantiainModuleName moduleName : moduleNames) {
      if ($name == moduleName) {
        containedModuleName = moduleName
        break
      }
    }
    if (containedModuleName != null) {
      return containedModuleName
    } else {
      ArrayList nextModuleNames = ArrayList.new(moduleNames)
      nextModuleNames.add($name)
      for (AkrantiainModuleName moduleName : $moduleChain) {
        AkrantiainModule module = root.findModuleOf(moduleName)
        AkrantiainModuleName circularModuleName = module.findCircularModuleName(nextModuleNames, root)
        if (circularModuleName != null) {
          return circularModuleName
        }
      }
      return null
    }
  }

  public AkrantiainModuleName findCircularModuleName(AkrantiainRoot root) {
    List<AkrantiainModuleName> moduleNames = ArrayList.new()
    return findCircularModuleName(moduleNames, root)
  }

  public List<AkrantiainModuleName> findUsedModuleNames(AkrantiainRoot root) {
    List<AkrantiainModuleName> usedModuleNames = ArrayList.new()
    for (AkrantiainModuleName moduleName : $moduleChain) {
      AkrantiainModule module = root.findModuleOf(moduleName)
      if (module != null) {
        usedModuleNames.add(module.getName())
        usedModuleNames.addAll(module.findUsedModuleNames(root))
      }
    }
    return usedModuleNames
  }

  public Boolean containsEnvironment(AkrantiainEnvironment environment) {
    return $environments.contains(environment)
  } 

  public Boolean containsDefinitionOf(String identifierName) {
    if (identifierName != PUNCTUATION_IDENTIIER_NAME) {
      for (AkrantiainDefinition definition : $definitions) {
        if (definition.getIdentifier().getText() == identifierName) {
          return true
        }
      }
      return false
    } else {
      return false
    }
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("% ")
    string.append($name)
    string.append("{")
    string.append("\nenvironments:")
    for (Int i = 0 ; i < $environments.size(); i ++) {
      string.append("\n  ")
      string.append($environments[i])
    }
    string.append("\ndefinitions:")
    for (Int i = 0 ; i < $definitions.size(); i ++) {
      string.append("\n  ")
      string.append($definitions[i])
    }
    string.append("\nrules:")
    for (Int i = 0 ; i < $rules.size(); i ++) {
      string.append("\n  ")
      string.append($rules[i])
    }
    string.append("\nmodule chain:")
    for (Int i = 0 ; i < $moduleChain.size(); i ++) {
      string.append("\n  ")
      string.append($moduleChain[i])
    }
    string.append("\n}")
    return string.toString()
  }

  public AkrantiainModuleName getName() {
    return $name
  }

  public void setName(AkrantiainModuleName name) {
    $name = name
  }

  public Set<AkrantiainEnvironment> getEnvironments() {
    return $environments
  }

  public void setEnvironments(Set<AkrantiainEnvironment> environments) {
    $environments = environments
  }

  public List<AkrantiainDefinition> getDefinitions() {
    return $definitions
  }

  public void setDefinitions(List<AkrantiainDefinition> definitions) {
    $definitions = definitions
  }

  public List<AkrantiainRule> getRules() {
    return $rules
  }

  public void setRules(List<AkrantiainRule> rules) {
    $rules = rules
  }

  public List<AkrantiainModuleName> getModuleChain() {
    return $moduleChain
  }

  public void setModuleChain(List<AkrantiainModuleName> moduleChain) {
    $moduleChain = moduleChain
  }

}