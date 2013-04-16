// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.appengine.demos.channeltactoe;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import org.json.JSONObject;

import org.mortbay.util.ajax.JSON;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
/**
 * @author moishel@google.com (Your Name Here)
 *
 */
@PersistenceCapable
public class Game {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  @Persistent
  private String userX;

  @Persistent
  private String userO;

  @Persistent
  private String board;

  @Persistent
  private Boolean moveX;
  
  @Persistent
  private String winner;
  
  @Persistent
  private String winningBoard;

  static final Pattern[] XWins = {
      Pattern.compile("XXX......"),
      Pattern.compile("...XXX..."),
      Pattern.compile("......XXX"),
      Pattern.compile("X..X..X.."),
      Pattern.compile(".X..X..X."),
      Pattern.compile("..X..X..X"),
      Pattern.compile("X...X...X"),
      Pattern.compile("..X.X.X..")
    };

  static final Pattern[] OWins = {
    Pattern.compile("OOO......"),
    Pattern.compile("...OOO..."),
    Pattern.compile("......OOO"),
    Pattern.compile("O..O..O.."),
    Pattern.compile(".O..O..O."),
    Pattern.compile("..O..O..O"),
    Pattern.compile("O...O...O"),
    Pattern.compile("..O.O.O..")
  };

  Game(String userX, String userO, String board, boolean moveX) {
    this.userX = userX;
    this.userO = userO;
    this.board = board;
    this.moveX = moveX;
  }

  public Key getKey() {
    return key;
  }

  public String getUserX() {
    return userX;
  }

  public String getUserO() {
    return userO;
  }

  public void setUserO(String userO) {
    this.userO = userO;
  }

  public String getBoard() {
    return board;
  }

  public void setBoard(String board) {
    this.board = board;
  }

  public boolean getMoveX() {
    return moveX;
  }

  public void setMoveX(boolean moveX) {
    this.moveX = moveX;
  }

  public String getMessageString() {
    Map<String, String> state = new HashMap<String, String>();
    state.put("userX", userX);
    if (userO == null) {
      state.put("userO", "");
    } else {
      state.put("userO", userO);
    }
    state.put("board", board);
    state.put("moveX", moveX.toString());
    state.put("winner", winner);
    if (winner != null && winner != "") {
      state.put("winningBoard", winningBoard);
    }
    JSONObject message = new JSONObject(state);
    return message.toString();
  }

  public String getChannelKey(String user) {
      String channelkey = user + KeyFactory.keyToString(key);
      int length = channelkey.length();
      if(length > 64){
      channelkey = channelkey.substring(length - 64, length);
      }
      return channelkey;
  }

  private void sendUpdateToUser(String user) {
    if (user != null) {
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      String channelKey = getChannelKey(user);
      channelService.sendMessage(new ChannelMessage(channelKey, getMessageString()));
    }
  }

  public void sendUpdateToClients() {
    sendUpdateToUser(userX);
    sendUpdateToUser(userO);
  }

  public void checkWin() {
    final Pattern[] wins;
    if (moveX) {
      wins = XWins; 
    } else {
      wins = OWins;
    }
    
    for (Pattern winPattern: wins) {
      if (winPattern.matcher(board).matches()) {
        if (moveX) {
          winner = userX;
        } else {
          winner = userO;
        }
        winningBoard = winPattern.toString();
      }
    }
  }

  public boolean makeMove(int position, String user) {
    String currentMovePlayer;
    char value;
    if (getMoveX()) {
      value = 'X';
      currentMovePlayer = getUserX();
    } else {
      value = 'O';
      currentMovePlayer = getUserO();
    }
    System.out.println("USER: " + user);
    System.out.println("CurrentMovePlayer: " + currentMovePlayer);
    if (currentMovePlayer.equals(user)) {
      System.out.println("CURRENTMOVEPLAYER EQUALS USER");
      char[] boardBytes = getBoard().toCharArray();
      boardBytes[position] = value;
      setBoard(new String(boardBytes));
      checkWin();
      setMoveX(!getMoveX());
      sendUpdateToClients();
      return true;
    }
    System.out.println("CURRENT MOVE PLAYER NOT EQUAL TO USER");
    
    return false;
  }
}
