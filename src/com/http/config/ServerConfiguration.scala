package com.http.config

/**
 * Contains details of server configuration.
 * TODO: FUTURE: Load this from file (I'm thinking JSON format)
 */
object ServerConfiguration {
  
  val maxConnectionLingerTime = 5 * (1000); // Connection linger time in milliseconds.
  
}