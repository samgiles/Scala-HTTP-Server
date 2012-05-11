package com.logging.viewer
import scala.xml.XML
import scala.xml.Node
import scala.collection.JavaConversions._

class LogViewer(errType: String) {

  var logs = XML.loadFile("errors.xml")

  def filter(n: Node): Boolean = {
    var result = XmlHelper.getTags("Type", n);
    return (result.size > 0 && result.get(0).text == errType);
  }
  
  var items = XmlHelper.getTags("LogItem", logs, filter);
  
  def outputItems() {
    for (item <- items) {
      val name = XmlHelper.getTags("Name", item).get(0).text;
      val etype = errType;
      val details = XmlHelper.getTags("Details", item).get(0).text;
      val stackTrace = XmlHelper.getTags("StackTrace", item).get(0);
      println("------");
      println(name + " - " + etype + "\n" + details);
      printStackTrace(item);
    }
  }
  
  private def printStackTrace(stackTrace: Node): Unit = {
    
    var traceElems = XmlHelper.getTags("TraceElem", stackTrace);
    
    if (traceElems.size <= 0) {
      return;
    }
    
    println("Stacktrace:");
    for (elem <- traceElems) {
      val className = XmlHelper.getTags("Classname", elem).get(0).text;
      val methodName = XmlHelper.getTags("Methodname", elem).get(0).text;
      val lineNumber = XmlHelper.getTags("LineNumber", elem).get(0).text;
      val fileName = XmlHelper.getTags("Classname", elem).get(0).text;
      println(className + "." + methodName + "() line: " + lineNumber + " - " + fileName);
    }
  }
}

/*



<Name>{ error }</Name>
    			<Type>{ errorType }</Type>
    			<Details>{ details }</Details>
    			<StackTrace>
    			{
    			  if (stackTrace != null && stackTrace.length  > 0) {
    			    
    			    for (traceElem <- stackTrace) yield
    			    <TraceElem>
    			    	<Classname>{ traceElem.getClassName() }</Classname>
    			    	<Methodname>{ traceElem.getMethodName() }</Methodname>
    			    	<LineNumber>{ traceElem.getLineNumber() }</LineNumber>
    			    	<FileName>{ traceElem.getFileName() }</FileName>
    			    </TraceElem>
    			  }
    			}
    			</StackTrace>*/