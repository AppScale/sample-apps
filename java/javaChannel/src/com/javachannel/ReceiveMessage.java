package com.javachannel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ReceiveMessage extends HttpServlet
{
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String token = channelService.createChannel(MainPage.receiverId);
        
        FileReader reader = new FileReader("receiveMessage.html");
        CharBuffer buffer = CharBuffer.allocate(16384);
        reader.read(buffer);
        String index = new String(buffer.array());
        index = index.replaceAll("\\{\\{ token \\}\\}", token);
        index = index.replaceAll("\\{\\{ appid \\}\\}", MainPage.receiverId);
        resp.setContentType("text/html");
        resp.getWriter().write(index);
    }
}
