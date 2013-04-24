package com.javachannel;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import java.util.UUID;

public class CreateChannel extends HttpServlet
{
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        String random = UUID.randomUUID().toString().substring(0, 5);
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String token = channelService.createChannel(random);
        resp.setContentType("text/html");
        resp.getWriter().write("Appid created: " + random);
        resp.getWriter().write(" Token created: " + token);
    }
}
