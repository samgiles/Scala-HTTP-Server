package com.logging
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem

object Logger {
  
  val errorFile = "errors.xml"
  val debugFile = new java.io.File("debug_log.log")
  
  val file = new java.io.File(errorFile);
  
  var errorLog = <Log></Log>;
  
  if (file.exists()) {
     errorLog = XML.loadFile(file)
  }
  
  
  def getLogEntry(error: String, errorType: String, details: String, stackTrace: Array[java.lang.StackTraceElement] = null): Node = {
    return <LogItem>
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
    			</StackTrace>
    	   </LogItem>
  }
  
  def error(error: String, description: String, includeStackTrace: Boolean, errType: String): Unit = {
    var stackTrace: Array[java.lang.StackTraceElement] = null;
    if (includeStackTrace) {
      stackTrace = Thread.currentThread().getStackTrace();
    }
   
    errorLog = addErrorItem(getLogEntry(error,errType,description, stackTrace));
  }
  
  def addErrorItem(newChild: Node): Elem = {
    	return Elem(errorLog.prefix, errorLog.label, errorLog.attributes, errorLog.scope, errorLog.child ++ newChild : _*)
  }

  
  def debug(debug: String, description: String): Unit = {
    write(debugFile, debug)
  }
  
  def outputErrorLog(): Unit = {
    XML.saveFull(errorFile, errorLog, "UTF-8", true, null);
  }
  
  private def write(file: java.io.File, message: String): Unit = {
    com.io.FileOps.fprint(file)(out => {
      out.write(new java.util.Date().toString + " - " + message + "\n")
    })
  }
  
}