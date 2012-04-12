package com.http
import scala.actors.Actor

class ControlHandler(listener: Actor) extends Actor {
	var running = true;
	
	def act = {
	  val scanner: java.util.Scanner = new java.util.Scanner(System.in);
	  
	  while (running) {
	    System.out.print("#");
	    val string = scanner.nextLine();
	    
	    string match {
	      case "terminate" => {
	        System.out.println("Shutting down server...");
	        listener ! ServerCommands.Terminate;
	        running = false;
	      }
	      case "help" => {
	        System.out.println("Not Yet Implemented... Sorry");
	      }
	      case _ => {
	        System.out.println("Unknown command: " + string + ".  Use 'help' command for list of commands");
	      }
	    }
	  }
	}
}