package com.google.appengine.demos.channeltactoe;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MoveServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String gameId = req.getParameter("gamekey");
    int piece = new Integer(req.getParameter("i"));
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Game game = pm.getObjectById(Game.class, KeyFactory.stringToKey(gameId));
    
    String currentUserId = userService.getCurrentUser().getUserId();
    if (!game.makeMove(piece, currentUserId)) {
      resp.setStatus(401);
    }
    pm.close();
  }
}
