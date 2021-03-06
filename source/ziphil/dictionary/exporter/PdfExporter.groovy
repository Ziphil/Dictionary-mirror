package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.XMLOutputFactory
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Saver
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class PdfExporter<D extends Dictionary, C extends PdfExportConfig> extends Saver<D> {

  private static final String CONFIG_PATH = "resource/xsl/config.xml"

  protected C $config

  public PdfExporter(C config) {
    $config = config
  }

  protected BooleanClass save() {
    saveTemporary()
    updateProgress(1, 2)
    transformFormat()
    executeExternalCommand()
    deleteTemporary()
    updateProgress(2, 2)
    return true
  }

  private void transformFormat() {
    File file = File.new($path.replaceAll(/\.\w+$/, "_fo.fo"))
    File temporaryFile = File.new($path.replaceAll(/\.\w+$/, "_temp.xml"))
    BufferedOutputStream stream = file.newOutputStream()
    BufferedInputStream temporaryStream = temporaryFile.newInputStream()
    try {
      Result result = StreamResult.new(stream)
      Source source = StreamSource.new(temporaryStream)
      Transformer transformer = createTransformer()
      transformer.transform(source, result)
    } finally {
      stream.close()
      temporaryStream.close()
    }
  }

  private void executeExternalCommand() {
    String[] command = $config.getExternalCommand().split(/\s+/)
    for (Int i = 0 ; i < command.length ; i ++) {
      String commandFragment = command[i]
      commandFragment = commandFragment.replaceAll(/%F/, $path.replaceAll(/\.\w+$/, "_fo.fo").replaceAll("\\\\", "\\\\\\\\"))
      commandFragment = commandFragment.replaceAll(/%P/, $path.replaceAll("\\\\", "\\\\\\\\"))
      command[i] = commandFragment
    }
    ProcessBuilder builder = ProcessBuilder.new(command)
    Process process = builder.start()
    process.waitFor()
    if (process.exitValue() != 0) {
      throw IllegalStateException.new("External command finished with nonzero exit value")
    }
  }

  private void saveTemporary() {
    File file = File.new($path.replaceAll(/\.\w+$/, "_temp.xml"))
    BufferedWriter bufferedWriter = file.newWriter("UTF-8")
    XMLOutputFactory factory = XMLOutputFactory.newInstance()
    XMLStreamWriter rawWriter = factory.createXMLStreamWriter(bufferedWriter)
    PrettyPrintHandler handler = PrettyPrintHandler.new(rawWriter)
    XMLStreamWriter writer = (XMLStreamWriter)Proxy.newProxyInstance(XMLStreamWriter.getClassLoader(), (Class[])[XMLStreamWriter].toArray(), handler)
    Boolean result = false
    try {
      writeTemporary(writer)
    } finally {
      writer.close()
      bufferedWriter.close()
    }
  }

  private void deleteTemporary() {
    File xmlFile = File.new($path.replaceAll(/\.\w+$/, "_temp.xml"))
    if (xmlFile.exists()) {
      xmlFile.delete()
    }
    File foFile = File.new($path.replaceAll(/\.\w+$/, "_fo.fo"))
    if (foFile.exists()) {
      foFile.delete()
    }
  }

  protected abstract void writeTemporary(XMLStreamWriter writer)

  private Transformer createTransformer() {
    Source xsltSource = StreamSource.new(getClass().getClassLoader().getResourceAsStream(getXsltPath()))
    TransformerFactory factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
    Transformer transformer = factory.newTransformer(xsltSource)
    setupTransformer(transformer)
    return transformer
  }

  protected abstract void setupTransformer(Transformer transformer)

  protected abstract String getXsltPath()

}


@InnerClass(PdfExporter)
@CompileStatic @Ziphilify
private static class PrettyPrintHandler implements InvocationHandler {

  private XMLStreamWriter $target
  private Int $depth = 0
  private Map<IntegerClass, BooleanClass> $childFlags = HashMap.new()

  public PrettyPrintHandler(XMLStreamWriter target) {
    $target = target
  }

  public Object invoke(Object proxy, Method method, Object[] args) {
    String name = method.getName()
    if (name == "writeStartElement") {
      if ($depth > 0) {
        $childFlags[$depth - 1] = true
      }
      $childFlags[$depth] = false
      $target.writeCharacters("\n")
      $target.writeCharacters(Strings.repeat("  ", $depth))
      $depth ++
    } else if (name == "writeEndElement") {
      $depth --
      if ($childFlags[$depth]) {
        $target.writeCharacters("\n")
        $target.writeCharacters(Strings.repeat("  ", $depth))
      }
    } else if (name == "writeEmptyElement") {
      if ($depth > 0) {
        $childFlags[$depth - 1] = true
      }
      $target.writeCharacters("\n")
      $target.writeCharacters(Strings.repeat("  ", $depth))
    }
    method.invoke($target, args)
    return null
  }

}