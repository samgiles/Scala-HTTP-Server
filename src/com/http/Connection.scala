package com.http

import scala.actors.Actor._
import com.http.fieldparsers._
import scala.collection.immutable.Queue

case class ReceivedLine(line: String)
case class SendResponse(response: String)
case object ForceClose

/**
 * A connection.
 * 
 * See <a href="http://ftp.ics.uci.edu/pub/ietf/http/draft-ietf-http-connection-00.txt">HTTP Connection Management</a> for information on HTTP Connection Management
 */
class Connection(socket: java.net.Socket) extends scala.actors.Actor {
  
  /**
   * The time the connection was created.
   */
  val creationTime = java.lang.System.currentTimeMillis();
  
  var requestQueue: Queue[ReceivedLine] = Queue();
  
  /**
   * Performs a check to see whether this connection has been open for too long.
   */
  def keepAlive(): Boolean = ((java.lang.System.currentTimeMillis() - creationTime) < com.http.config.ServerConfiguration.maxConnectionLingerTime)
  
  val remoteAddress = socket.getRemoteSocketAddress
  
  com.logging.Logger.debug("Incoming connection from: " + remoteAddress);
  
  private object ConnectionManager extends scala.actors.Actor {
    def act = {
      val sleepTime = com.http.config.ServerConfiguration.maxConnectionLingerTime;  // rather than the thread blasting itself polling the time do it only once.
      while(keepAlive){
        Thread.sleep(sleepTime);
      }
      Connection.this ! ForceClose
    }
  }
  
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
    	  Connection.this ! ReceivedLine(line)
    	  line = readLine;
      	}
      }
    }
  }
  
  private object RequestProcessor extends scala.actors.Actor {
    def act = {
      while (!socket.isClosed) {
        if (requestQueue.length > 0) {
	        var (requestLine, q) = requestQueue.dequeue;
	        requestQueue = q;
	
	        val topline = RequestFieldParser(requestLine.line);
	        
	        if (topline == null) {
	          val string = requestLine.line.split(":");
	          
	          if (string.length == 0) {
	            
	          } else {
	        	  string(0) match {
	            	case "Host" => {}
	            	case "Accept" => {}
	            	case "Accept-Language" => {}
	            	case "Accept-Encoding" => {}
	            	case "Connection" => {}
	            		case _ => {}
	          		}
	          	}
	          
	        } else {
	        	val request: RequestLine = topline.asInstanceOf[RequestLine];
	        	var response = 	"HTTP/1.1 200 OK\n" +
	        					"Content-Length: 88\n" + 
	        					"Connection: close\n" +
	        					"Content-Type: text/html; charset=iso-8859-1\n\n" +
	        					"<html><head><title>Hello!</title></head><body><h1>It Works!</h1></body></html>\n";

	        	
	        	
	        	Connection.this ! SendResponse(response); // \n"	
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
        case requestLine: ReceivedLine => {
          // We Received a line from the client!
          com.logging.Logger.debug("Receieved: " + requestLine.line);
          requestQueue = requestQueue.enqueue(requestLine);
        }
        
        case respond: SendResponse => {
          com.logging.Logger.debug("Sending response: " + respond.response)
          sendResponse(respond.response)
        }
        
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
  
  
  def sendResponse(response: String): Unit = {
    
    val os = socket.getOutputStream
    val out = new java.io.PrintStream(os)
    
    out.println(response);
    out.flush();
  }
  
  // Self start the actors.
  this.start
  IncomingRequestHandler.start
  ConnectionManager.start
  RequestProcessor.start
}