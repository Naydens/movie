package httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpclient.model.Movie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MovieApiDownloadInfo {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.themoviedb.org/3/discover/movie").newBuilder();
        urlBuilder.addQueryParameter("api_key", "50e28c2946dd6d45673965da6dadcf42");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response =null;
        for (int i=0; i<10;i++) {
            try {
                response = client.newCall(request).execute();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (response==null)
            throw new IllegalArgumentException("No response after 10 times!");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parent= objectMapper.readTree(response.body().string());
        String content = parent.path("results").toString();
        Movie[] movie = objectMapper.readValue(content, Movie[].class);
        System.out.println(movie.length);
        for (Movie movie1 : movie) {
            System.out.println(movie1);
        }
    }
}
