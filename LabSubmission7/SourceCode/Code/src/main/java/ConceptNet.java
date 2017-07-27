import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shalin on 7/2/2017.
 */
public class ConceptNet {
    public static List<String> GetConcept(String data) {
        HttpClient httpClient = new DefaultHttpClient();
        String line = "";
        List<String> conceptArray=new ArrayList<>();
        try {
            HttpGet httpGetReq = new HttpGet("http://api.conceptnet.io/c/en/"+data);
            HttpResponse httpRes = httpClient.execute(httpGetReq);

            HttpEntity entity = httpRes.getEntity();

            byte[] buffer = new byte[1024];
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                int bytesRead = 0;
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                while ((bytesRead = bis.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, bytesRead);
                    line += chunk;
                }

                inputStream.close();
            }
            JSONParser jParser = new JSONParser();
            JSONObject jsonData = (JSONObject) jParser.parse(line);
            JSONArray ja = (JSONArray) jsonData.get("edges");
            for (int i = 0; i < ja.size(); i++) {
                JSONObject ob = (JSONObject) ja.get(i);
                conceptArray.add(ob.get("surfaceText").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
            return conceptArray;
        }


    }
}
