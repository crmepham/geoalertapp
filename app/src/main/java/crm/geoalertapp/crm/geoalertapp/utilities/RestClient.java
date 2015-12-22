package crm.geoalertapp.crm.geoalertapp.utilities;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class RestClient {

    private String baseUrl;
    private MultivaluedMap multivaluedMap;

    public RestClient() {
        this.baseUrl = "http://10.0.2.2:8080/geoalertserver/api/v1/";
    }
    public RestClient(MultivaluedMap map) {
        this();
        this.multivaluedMap = map;
    }

    public String postForString(String url){
        Client client = Client.create();
        WebResource webResource = client.resource(baseUrl + url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                        .post(ClientResponse.class, multivaluedMap);
        return response.getEntity(String.class);
    }

    public int postForResponseCode(String url){
        int responseCode = 0;
        Client client = Client.create();
        WebResource webResource = client.resource(baseUrl + url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, multivaluedMap);
        return response.getStatus();
    }

    public JSONObject postForJsonObject(String url){
        JSONObject json = new JSONObject();
        return json;
    }

}
