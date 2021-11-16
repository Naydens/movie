package httpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpclient.model.Movie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MovieApiDownloadInfo {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String root = "https://api.themoviedb.org/3/discover/movie";
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("api_key", "50e28c2946dd6d45673965da6dadcf42");

        JsonNode parent = objectMapper.readTree(info(client, root, queryParameters).body().string());
        int pagesCount = parent.path("total_pages").asInt();

        for (int i = 1; i <= pagesCount; i++) {
            queryParameters.put("page", String.valueOf(i));
            parent = objectMapper.readTree(info(client, root, queryParameters).body().string());
            String content = parent.path("results").toString();
            Movie[] movieP = objectMapper.readValue(content, Movie[].class);
            for (Movie movie : movieP) {
                System.out.println(movie);
            }
        }
    }

    public static Response info(OkHttpClient client, String urlApi, Map<String, String> queryParameters) throws IOException {
        HttpUrl.Builder url = HttpUrl.parse(urlApi).newBuilder();
        Iterator<Map.Entry<String, String>> parameters = queryParameters.entrySet().iterator();
        while (parameters.hasNext()) {
            Map.Entry<String, String> param = parameters.next();
            url.addQueryParameter(param.getKey(), param.getValue());
        }
        String urlFull = url.build().toString();
        Request request = new Request.Builder()
                .url(urlFull)
                .build();
        Response response = response(client, request);
        if (response == null)
            throw new IllegalArgumentException("No response after 10 times!");

        if (response.body() == null)
            throw new IllegalArgumentException("body of response didn't have info");
        return response;
    }

    public static Response response(OkHttpClient client, Request request) {
        Response response = null;
        for (int i = 0; i < 10; i++) {
            try {
                response = client.newCall(request).execute();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
/*
to write info to comp
    BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\nadya\\Desktop\\oldComp\\task4.txt", true));
            writer.append(' ');
                    writer.append(content);
                    writer.close();*/
