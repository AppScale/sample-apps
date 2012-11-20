package guestbook;


import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;


public class DatastoreServlet extends HttpServlet
{

    public void doGet( HttpServletRequest req, HttpServletResponse resp )
    {
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        System.out.println(datastore.getClass().getCanonicalName());
        Entity employee = new Entity("Employee");

        employee.setProperty("firstName", "Antonio");
        employee.setProperty("lastName", "Salieri");

        Date hireDate = new Date();
        employee.setProperty("hireDate", hireDate);

        employee.setProperty("attendedHrTraining", true);
        System.out.println("Executing put");
        datastore.put(employee);
        System.out.println("Done executing put");
        
        Key key = employee.getKey();
        try
        {
            System.out.println("Doing get");
            datastore.get(key);
            System.out.println("Done with get");
        }
        catch (EntityNotFoundException e)
        {
            System.out.println("Exception");
        }
        
    }
}
