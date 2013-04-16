package com.google.appengine.demos.channeltactoe;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TicTacToeServlet extends HttpServlet {
  private String getGameUriWithGameParam(HttpServletRequest req,
      String gameKey) throws IOException {    
    try {
      String query;
      if (gameKey == null) {
        query = "";
      } else {
        query = "g=" + gameKey;
      }
      URI thisUri = new URI(req.getRequestURL().toString());
      URI uriWithOptionalGameParam = new URI(thisUri.getScheme(),
          thisUri.getUserInfo(),
          thisUri.getHost(),
          thisUri.getPort(),
          thisUri.getPath(),
          query,
          "");
      return uriWithOptionalGameParam.toString();
    } catch (URISyntaxException e) {
      throw new IOException(e.getMessage());
    }
    
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {    
    final UserService userService = UserServiceFactory.getUserService();
    final URI uriWithOptionalGameParam; 
    String gameKey = req.getParameter("g");
    if (userService.getCurrentUser() == null) {
      String thisURL = req.getRequestURL().toString();      
      resp.getWriter().println("<p>Please <a href=\"" +
          userService.createLoginURL(getGameUriWithGameParam(req, gameKey)) + "\">sign in</a>.</p>");
      
      return;
    }
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    Game game = null;
    String userId = userService.getCurrentUser().getUserId();
    if (gameKey != null) {
      game = pm.getObjectById(Game.class, KeyFactory.stringToKey(gameKey));
      if (game.getUserO() == null && !userId.equals(game.getUserX())) {
        game.setUserO(userId);
      }
    } else {
      game = new Game(userId, null, "         ", true);
      pm.makePersistent(game);
      gameKey = KeyFactory.keyToString(game.getKey());
    }
    pm.close();
    
    ChannelService channelService = ChannelServiceFactory.getChannelService();
    String token = channelService.createChannel(game.getChannelKey(userId));

    FileReader reader = new FileReader("index-template");
    CharBuffer buffer = CharBuffer.allocate(16384);
    reader.read(buffer);
    String index = new String(buffer.array());
    index = index.replaceAll("\\{\\{ game_key \\}\\}", gameKey);
    index = index.replaceAll("\\{\\{ me \\}\\}", userId);
    index = index.replaceAll("\\{\\{ token \\}\\}", token);
    index = index.replaceAll("\\{\\{ initial_message \\}\\}", game.getMessageString());
    index = index.replaceAll("\\{\\{ game_link \\}\\}", getGameUriWithGameParam(req, gameKey));
    
    resp.setContentType("text/html");
    resp.getWriter().write(index);
  }
}
