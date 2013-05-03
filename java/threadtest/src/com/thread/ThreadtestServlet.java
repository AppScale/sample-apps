package com.thread;


import java.io.IOException;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class ThreadtestServlet extends HttpServlet
{
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        System.out.println("Request in servlet");
        
        try
        {
            Thread.sleep(15000);
        }
        catch (InterruptedException e)
        {
            System.out.println("Interrupted");
        }
        
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world");
    }
}
