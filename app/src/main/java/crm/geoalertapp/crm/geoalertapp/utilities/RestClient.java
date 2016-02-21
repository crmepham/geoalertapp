package crm.geoalertapp.crm.geoalertapp.utilities;

import android.graphics.Bitmap;
import android.util.Log;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class RestClient {

    //private static final String BASE_URL = "http://10.0.2.2:8080/geoalertserver/api/v1/";
    private static final String BASE_URL = "http://crmepham.no-ip.biz:8080/geoalertserver/api/v1/";
    private MultivaluedMap multivaluedMap;

    public RestClient() {
    }
    public RestClient(MultivaluedMap map) {
        this();
        this.multivaluedMap = map;
    }

    public void updateMap(MultivaluedMap map) {
        this.multivaluedMap = map;
    }

    public String postForString(String url){
        Client client = Client.create();
        WebResource webResource = client.resource(BASE_URL + url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                        .post(ClientResponse.class, multivaluedMap);
        return response.getEntity(String.class);
    }

    public int postForResponseCode(String url){
        int responseCode = 0;
        Client client = Client.create();
        WebResource webResource = client.resource(BASE_URL + url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, multivaluedMap);
        return response.getStatus();
    }

    public int postFileForResponseCode(String user, String url, Bitmap bitmap){
        int responseCode = 0;


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(BASE_URL + url);

        String boundary = "-------------" + System.currentTimeMillis();

        httpPost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);

        ByteArrayBody image = new ByteArrayBody(imageBytes, "profile-img.png");
        StringBody username = new StringBody(user, ContentType.TEXT_PLAIN);

        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary)
                .addPart("username", username)
                .addPart("image", image)
                .build();

        httpPost.setEntity(entity);

        try {
            HttpResponse response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();

        } catch (Exception e){
            Log.e("", e.getMessage());
        }


        /*Client client = Client.create();
        WebResource webResource = client.resource(BASE_URL + url);
        ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, multivaluedMap);
        return response.getStatus();*/

        return responseCode;
    }

    public JSONObject postForJsonObject(String url){
        JSONObject json = new JSONObject();
        Client client = Client.create();
        WebResource webResource = client.resource(BASE_URL + url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, multivaluedMap);


        return json; // not correct
    }

    public byte[] postForImage(String url){
        byte[] array =  null;
        Client client = Client.create();
        WebResource webResource = client.resource(BASE_URL + url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, multivaluedMap);

        InputStream input = (InputStream)response.getEntity(InputStream.class);
        try{
            array = IOUtils.toByteArray(input);
        }catch (IOException e) {
            Log.e("", e.getMessage());
        }

        return array;
    }

}
