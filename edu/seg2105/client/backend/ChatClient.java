// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String currentcommand; 
  private boolean connected;
  private boolean loggedoff; 

  String loginId;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if (message.startsWith("#")){
        handleCommand(message);
      }
      
      else{
        sendToServer(message);
      }
        
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  private void handleCommand (String command) {

    //we will split the message to allow us to get other messages from the user other than the command
    String[] messagegiven = command.split(" "); 


    currentcommand = messagegiven[0]; 
    
      if (messagegiven[0].equals("#quit")){
        quit();
      }
  
      else if (messagegiven[0].equals("#logoff")){
      
          
            try {
              closeConnection();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              clientUI.display("Unknown error ");
            }
            
          
        
      }

      else if (messagegiven[0].equals("#sethost")){
        if (loggedoff){
          setHost(messagegiven[1]);
        }
        else{
          clientUI.display("You haven't logged off! Unable to sethost."); 
        }
      }

      else if (messagegiven[0].equals("#setport")){
        if (loggedoff){
          setPort(Integer.parseInt(messagegiven[1]));
        }
        else{
          clientUI.display("You haven't logged off! Unableto setport.");
        }
      }

      else if (messagegiven[0].equals("#login")){
        if (!isConnected()){
          try {
            openConnection();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            clientUI.display("Unable to connect to server.");
          }
        }

        else{
          clientUI.display("You are already connected");
        }
      }

      else if (command.equals("#gethost")){
        clientUI.display(getHost());
      }

      else if (command.equals("#getport")){
        
        clientUI.display(String.valueOf(getPort())); 
      }
  
    

    
     
    
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
   * Implements the hook method called each time an exception is thrown by the client's thread
   * that is waiting for messages from the server. 
   */

  @Override
  protected void connectionException(Exception exception){
    
      clientUI.display("The server has shut down ");
      System.exit(0);
    
    
  }

  /**
   * Hook method called after the conncetion has been closed. The default implementation
   * does nothing. 
   */
  @Override
  protected void connectionClosed(){
    if ("#quit".equals(currentcommand)){
      connected = false;
      clientUI.display("The server has shut down ");
      System.exit(0);
    }
    else if ("#logoff".equals(currentcommand)){
      connected = false; 
      loggedoff = true; 
      clientUI.display("Disconnected from server, program still running...");
    }

    
    
    
  }

  @Override
  protected void connectionEstablished(){

    clientUI.display("Connected"); 

    try {
      sendToServer("#login " + loginId);
      
    }
    catch (IOException e){
      clientUI.display("unknown error");
      quit(); 
    }

  }

 





}

  


//End of ChatClient class

