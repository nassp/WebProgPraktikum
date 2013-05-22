package de.quiz.User;


import javax.servlet.http.HttpSession;

import de.fhwgt.quiz.application.Player;



/**
 * this class is the interface for the UserConfig
 * 
 * @author Patrick Na§
 */
public interface IUser {
    
    /*##################################
     * setter
     *##################################*/
    
    /**
     * set user id
     * 
     * @param id	the user's id/alias
     */
    public void setID(String id);
    
    
    /**
     * set user session
     * 
     * @param session	the user's session
     */
    public void setSession(HttpSession session);
    
    /**
     * set the name
     * 
     * @param	_firstname	the user's name
     */
    public void setName (String _firstname);
    
    /**
     * set the player object
     * 
     * @param _player
     */
    public void setPlayerObject(Player _player);
    
    /**
     * set the websocket id
     * 
     * @param id
     */
    public void setWSID (int id);
    
    
    /*##################################
     * getter
     *##################################*/
    

    
    /**
     * getter for session
     */
    public HttpSession getSession(); 
    
    /**
     * getter for the user ID
     * 
     */
    public String getUserID ();
    
    /**
     * getter for the user's name
     * 
     */
    public String getName ();
    
    /**
     * getter for the player object
     * 
     */
    public Player getPlayerObject ();
    
    /**
     * getter for the websocket id
     * 
     * @return int
     */
    public int getWSID ();
    
    
    
    /**
     * checks if two user objects have the same ID
     */
    public boolean hasSameID(Object object);
}
