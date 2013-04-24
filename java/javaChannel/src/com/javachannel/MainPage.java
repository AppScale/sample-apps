package com.javachannel;


import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import javax.servlet.http.*;


@SuppressWarnings("serial")
public class MainPage extends HttpServlet
{
    public static final String receiverId = "receiverappid";
    public static final String senderId = "senderappid";
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        FileReader reader = new FileReader("index.html");
        CharBuffer buffer = CharBuffer.allocate(16384);
        reader.read(buffer);
        String index = new String(buffer.array());
        index = index.replaceAll("\\{\\{ receiverId \\}\\}", receiverId);
        index = index.replaceAll("\\{\\{ senderId \\}\\}", senderId);
        resp.setContentType("text/html");
        resp.getWriter().write(index);
    }
}
