package gaeexample.blobstore;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class Upload extends HttpServlet {

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");
        BlobKeyCache bc = BlobKeyCache.getBlobKeyCache();

        String contextPath = req.getScheme().toString() + "://" + req.getServerName().toString() + ":" + String.valueOf(req.getServerPort());

        if (blobKey == null)
            System.out.println("blobkey is null");
        else {
            System.out.println("blobkey is " + blobKey.getKeyString());
            bc.add(blobKey);
            res.sendRedirect(res.encodeRedirectURL(contextPath + "/serve.jsp?blob-key=" + blobKey.getKeyString() + "&fromUpdate=1"));
        }
    }
}
