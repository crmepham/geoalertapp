package crm.geoalertapp.crm.geoalertapp.utilities;

import android.util.Log;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class RestClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/geoalertserver/api/v1/";
    private MultivaluedMap multivaluedMap;

    public RestClient() {
    }
    public RestClient(MultivaluedMap map) {
        this();
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
