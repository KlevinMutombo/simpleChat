package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;
import ocsf.client.AbstractClient; 

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI; 
  AbstractClient client;
  public boolean closed;

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	
    String message = (String) msg;
    System.out.println("Message received: " + msg + " from " + client.getInfo("LoginID") + ".");
    
    // Send the received message to all clients
    this.sendToAllClients(msg);

    // Check if the message is a login command
    if (message.startsWith("#login")){
      
      String[] earlymessage = message.split(" "); 

      if (client.getInfo("LoginID") == null){
        client.setInfo("LoginID", earlymessage[1] ); 
        serverUI.display(earlymessage[1] + " has logged on.");
      }

      else if (!client.isAlive()){
        client.setInfo("LoginID", msg); 
        serverUI.display(msg + " has logged on");
      }

      else{
        try{
          client.sendToClient("You can only log in once.");
          client.close(); // Close the client connection if already logged in
        }
        catch (IOException e){
          serverUI.display("unknownerror");
        }
      }


    }else {
    	// For non-login messages, display the client's LoginID and the message
    	sendToAllClients(client.getInfo("LoginID")+ " > " + msg);
    }


  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	 
      System.out.println("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * This method is called when a new client connects to the server.
   * 
   * @param client The new client that has connected.
   */

  @Override
  protected void clientConnected(ConnectionToClient client){
    System.out.println("A new client has connected to the server.");

  }

  /**
   * This method is called when a client disconnects from the server.
   * 
   * @param client The client that has disconnected.
   */
  
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client){
    System.out.println(client.getInfo("LoginID")+ " has disconnected.");

  }
  
  /**
   * This method is called when a client disconnects unexpectedly or due to an exception.
   * 
   * @param client The client that has disconnected.
   * @param exception The exception that caused the disconnection.
   */
  
  
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		
		  System.out.println(client.getInfo("LoginID") + " has disconnected.");
	  }
  

  /**
   * This method handles messages from the server UI.
   * 
   * @param message The message entered by the server administrator.
   */
  
  public void handleMessageFromServerUI(String message)
  {
	  try
	    {
		  // check what the message starts with
	      if(message.startsWith("#")) {
	    	 handleCommand(message);
	      }
	      else {
	    	  // if it's not a command and the client exists, send the message to be processed
	    	  if(this.client != null) {
	    		  client.sendToServer(message);
	    	  }
	    	  // also display in server
	    	  serverUI.display(message);
	    	  
	    	  sendToAllClients("SERVER MSG> " + message);
	      }
	    }
	    catch(IOException e)
	    {
	      serverUI.display
	        ("Could not send message.");
	    }
    
  }
  
  /**
   * Processes server commands.
   * 
   * @param command The command string entered by the server administrator.
   */

  private void handleCommand(String command){
    
    String[] messagegiven = command.split(" "); 

    
    //Quits server with #quit we quit the server
    if (messagegiven[0].equals("#quit")){
      serverUI.display("Quitting server...");
      System.exit(0);
    }

    //Sever stop listening for clients
    else if (messagegiven[0].equals("#stop")){
      stopListening();
    }

    //Server stops listening for clients and disconnects client
    else if (messagegiven[0].equals("#close")){
      serverUI.display("No longer listening for new clients, everyone will now be disconnected.");
      try {
        close();
        closed = true; 
      } catch (IOException e) {
        // TODO Auto-generated catch block
        serverUI.display("Unknown error"); 
      } 
    }

    //Lets the user set a port 
    else if (messagegiven[0].equals("#setport")){
      if (closed){
        setPort(Integer.parseInt(messagegiven[1])); 

      }
      
      //Error message if user attempts to set port if server isn't closed.
      else{
        serverUI.display("Cannot set port if server is not closed.");
      }
    }

    //Starts the server if it isn't listening yet.
    else if (messagegiven[0].equals("#start")){
      if (!isListening()){
        try {
          listen();
        	
        } catch (IOException e) {
          // TODO Auto-generated catch block
        	e.printStackTrace();
          serverUI.display("Unknown error.");
          
        }
      }

      else{
        serverUI.display("Server is already listening.");
      }

    }

    else if (messagegiven[0].equals("#getport")){
      serverUI.display(String.valueOf(getPort())); 
    }


  }


  public static void main (String args[]) {
	  System.out.print("Server Starting");
	  
  }
    
    
    
    
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  

//End of EchoServer class

