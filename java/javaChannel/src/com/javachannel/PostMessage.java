package com.javachannel;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class PostMessage extends HttpServlet
{
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        ChannelMessage message = new ChannelMessage(MainPage.receiverId, "helloworld");
        channelService.sendMessage(message);
        resp.setContentType("text/html");
        resp.getWriter().write("message sent");
        
    }
}
