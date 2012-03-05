package com.http

import scala.actors.Actor._

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
  def checkConnectionLinger(): Boolean = ((java.lang.System.currentTimeMillis() - creationTime) > com.http.config.ServerConfiguration.maxConnectionLingerTime)
  
  val remoteAddress = socket.getRemoteSocketAddress;
  
  def act = {
    
    while(checkConnectionLinger) {
      // Receive Incoming connection data
      receive {
        
        case _ => {
          // Unknown Message -> TODO: Logging
        }
      }
      
      
    }
    
  }
  
  
}