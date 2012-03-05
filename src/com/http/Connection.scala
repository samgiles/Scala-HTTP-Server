package com.http

import scala.actors.Actor._

case class ReceivedLine(line: String)
case class SendResponse(response: String)

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
  
  /**
   * Performs a check to see whether this connection has been open for too long.
   */
  def keepAlive(): Boolean = ((java.lang.System.currentTimeMillis() - creationTime) < com.http.config.ServerConfiguration.maxConnectionLingerTime)
  
  val remoteAddress = socket.getRemoteSocketAddress;
  
  
  object IncomingRequestHandler extends scala.actors.Actor {
    
    def act = {
      if (!socket.isClosed) {
    	val istream = socket.getInputStream
      	val reader = new java.io.BufferedReader(new java.io.InputStreamReader(istream))
      
      	var line = reader.readLine;
      	while(line != null && !socket.isClosed) {
    	  Connection.this ! ReceivedLine(line)
          line = reader.readLine
      	}
      }
    }
  }
  
  def act = {
    var close = false
    while(keepAlive && !close) {
      // Receive Incoming connection data
      receive {
        case requestLine: ReceivedLine => {
          // We Received a line from the client!
          com.logging.Logger.debug("Receieved: " + requestLine.line)
        }
        
        case sendResponse: SendResponse => {
          com.logging.Logger.debug("Sending response: " + sendResponse)
          close = true
        }
        
        case _ => {
          com.logging.Logger.debug("Incorrect case message receieved in Connection Actor receive block.")
        }
      }
    }
    socket.close
  }
  
  // Self start the actors.
  this.start
  IncomingRequestHandler.start
  
  
}