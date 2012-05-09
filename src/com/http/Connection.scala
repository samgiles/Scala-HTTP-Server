package com.http

import scala.actors.Actor._
import com.http.fieldparsers._
import scala.collection.immutable.Queue

case class ReceivedLine(line: String)
case class SendResponse(response: Array[Byte])
case object ForceClose

/**
 * A connection.
 * 
 * See <a href="http://ftp.ics.uci.edu/pub/ietf/http/draft-ietf-http-connection-00.txt">HTTP Connection Management</a> for information on HTTP Connection Management
 */
class Connection(socket: java.net.Socket) extends scala.actors.Actor {
  
  /**
   * The time the connection was created. This is stored as the Connection is created
   */
  val creationTime = java.lang.System.currentTimeMillis();
  
  /**
   * The connection contains a Queue of incoming requests in the form of lines, a request header is made up of many lines terminated by two "\n\n" characters.
   */
  var requestQueue: Queue[ReceivedLine] = Queue();
  
  /**
   * Performs a check to see whether this connection has been open for too long.  This is used to close the connection after a specific amount of time.
   */
  def keepAlive(): Boolean = ((java.lang.System.currentTimeMillis() - creationTime) < com.http.config.ServerConfiguration.maxConnectionLingerTime)
  
  /**
   * Stores the remote address of the connection.
   */
  val remoteAddress = socket.getRemoteSocketAddress
  
  /**
   * Log that a connection was received.
   */
  com.logging.Logger.debug("Incoming connection from: " + remoteAddress);
  
  /**
   * The connection manager object, the Connection manager manages the length of the connection, this prevents a connection from remaining open for a very long time.
   */
  private object ConnectionManager extends scala.actors.Actor {
    def act = {
      // Get the pre configured time that we are allowed to keep the connection open for
      val sleepTime = com.http.config.ServerConfiguration.maxConnectionLingerTime;
      do {
        // Make the thread sleep for the amount of time.
        Thread.sleep(sleepTime);
      } while(keepAlive);
      // Log the connection closing.
      com.logging.Logger.debug("Closing connection from: " + remoteAddress);
      // Send a ForceClose message to the main connection object which will forcefully close the connection.
      Connection.this ! ForceClose
    }
  }
  
  /**
   * The incoming request handler handles incoming requests and forwards the data to the connection.
   */
  private object IncomingRequestHandler extends scala.actors.Actor {
    def act = {
      if (!socket.isClosed) {
    	val istream = socket.getInputStream
      	val reader = new java.io.BufferedReader(new java.io.InputStreamReader(istream))
        
    	def readLine: String = {
    	  var line: String = null;
    	  try {  // We could be blocked waiting for the next line when the connection is closed.
    		  line = reader.readLine
    	  } catch {
    	    case e: java.net.SocketException => {
    	      line = null
    	    }
    	  }
    	  return line;
    	}
    	
      	var line = readLine;
    	
      	while(line != null && !socket.isClosed) {
      	  // as we recieve a line forard it to the main connection object.
    	  Connection.this ! ReceivedLine(line)
    	  line = readLine;
      	}
      }
    }
  }
  
  /**
   * The request processor deals with a resource request and appropriately returns a result.
   */
  private object RequestProcessor extends scala.actors.Actor {
    def act = {
      // While connection is not closed
      while (!socket.isClosed) {
        // if the request queue is not empty
        if (requestQueue.length > 0) {
          // de queue the head of the queue.
	        var (requestLine, q) = requestQueue.dequeue;
	        requestQueue = q;
	
	        // try and parse the head of the queue as a top line of an HTTP request, if this fails then it will return null.
	        val topline = RequestFieldParser(requestLine.line);
	        
	        if (topline != null) {      
	            //  Get the files that is being referred to by the top line and read it into a response, if the file doesn't exit return a 404 header and message.
	        	val request: RequestLine = topline.asInstanceOf[RequestLine];
	        	
	        	var requestFile = request.uri.drop(1); // remove the prefixed slash
	        	
	        	// if the request length is 0 then return the default file, index.html.
	        	if (requestFile.length() == 0) {
	        	 requestFile = "index.html"; 
	        	}
	        	
	        	// open the file
	        	val file = new java.io.File(requestFile);
	        	
	        	var response = "";
	        	var bytes : Array[Byte] = null;
	        	
	        	// if the file doesnt exist return a 404 header and message
	        	if (!file.exists()) {
	        		
	        		response = 	"HTTP/1.1 404 Not Found\n" +
	        						"Content-Length: 88\n" + 
	        						"Connection: close\n" +
	        						"Content-Type: text/html; charset=iso-8859-1\n\n" +
	        						"<html><head><title>Hello!</title></head><body><h1>404</h1><h3>Not found!</h3></body></html>\n";
	        		bytes = response.getBytes();
	        	} else {
	        	    // the file exists so return a 200 ok message with the file length and the mime type
	        		val buffer = com.io.FileOps.getFileContents(file);
	        		response = "HTTP/1.1 200 OK\n" +
	        				   "Content-Length: " + buffer.length + "\n" +
	        				   "Content-Type: " + com.http.config.MimeTypes.types(com.io.FileOps.getExtension(file)) + "\n" +
	        				   "Connection: close\n\n";
	        		
	        		val respBytes = response.getBytes();
	        		bytes = respBytes ++ buffer;
	        	}
	        	// send the response back.
	        	Connection.this ! SendResponse(bytes);
	        }
        }
      }
    }
  }
  
  def act = {
    var close = false
    while(!close) {
      // Receive Incoming connection data
      receive {
        // as a line is receieved from the connection queue it in the requestQueue.
        case requestLine: ReceivedLine => {
          // We Received a line from the client!
          com.logging.Logger.debug("Receieved: " + requestLine.line);
          requestQueue = requestQueue.enqueue(requestLine);
        }
        
        // send a response back
        case respond: SendResponse => {
          com.logging.Logger.debug("Sending response: " + respond.response)
          sendResponse(respond.response)
        }
        
        // force the connection to close.
        case ForceClose => {
          close = true;
        }
        
        case _ => {
          com.logging.Logger.debug("Incorrect case message receieved in Connection Actor receive block.")
        }
      }
    }
    socket.close
  }
  
  /**
   * Send the response bacj to the connection.
   */
  def sendResponse(response: Array[Byte]): Unit = {
    
    val os = socket.getOutputStream
    val out = new java.io.PrintStream(os)
    out.write(response)
    out.flush();
  }
  
  // Self start all the actors.
  this.start
  IncomingRequestHandler.start
  ConnectionManager.start
  RequestProcessor.start
}