package httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpclient.model.Movie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MovieApiDownloadInfo {

    static String baseUrl;
    static String posterSize;
    static String root = "https://api.themoviedb.org/3";
    static String apiKey = "50e28c2946dd6d45673965da6dadcf42";
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("api_key", apiKey);
        JsonNode parent = objectMapper.readTree(getHttpBody(client, root + "/discover/movie", queryParameters));
        int pagesCount = parent.path("total_pages").asInt();
        queryParameters.remove("page");
        parent = objectMapper.readTree(getHttpBody(client, root + "/configuration", queryParameters));
        baseUrl = parent.path("images").path("base_url").asText();
        Iterator<JsonNode> elements = parent.path("images").withArray("poster_sizes").elements();

        while (elements.hasNext()) {
            String tmpSize = elements.next().asText();
            if (Integer.valueOf(tmpSize.substring(1)) > 300) {
                posterSize = tmpSize;
                break;
            }
        }
        Movie[] movieP = null;
        for (int i = 1; i <= pagesCount; i++) {
            movieP = getArrayOfFilmsFromPages(client, root, queryParameters, i);
            for (Movie movie : movieP) {
                File tempFile = new File("C:\\Users\\nadya\\Desktop\\imges\\" + movie.getPosterPath());
                if (tempFile.exists()) {
                    continue;
                }
                OutputStream outputStream = new FileOutputStream("C:\\Users\\nadya\\Desktop\\imges\\" + movie.getPosterPath());
                InputStream inputStream = getHttpBodyInputStream(client, baseUrl + posterSize + "/" + movie.getPosterPath(), queryParameters);
                copy(inputStream, outputStream);
                outputStream.close();
            }
        }
    }

    static Movie[] getArrayOfFilmsFromPages(OkHttpClient client, String urlApi, Map<String, String> queryParameters, int numberOfPage) throws IOException {
        queryParameters.put("page", String.valueOf(numberOfPage));
        JsonNode parent = objectMapper.readTree(getHttpBody(client, urlApi + "/discover/movie", queryParameters));
        String content = parent.path("results").toString();
        Movie[] movieArray = objectMapper.readValue(content, Movie[].class);
        return movieArray;
    }

    static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    static String getHttpBody(OkHttpClient client, String urlApi, Map<String, String> queryParameters) throws IOException {
        Response response = makeGetRequest(client, urlApi, queryParameters);
        if (response.body() == null)
            throw new IllegalArgumentException("body of response didn't have info");

        return response.body().string();
    }

    static InputStream getHttpBodyInputStream(OkHttpClient client, String urlApi, Map<String, String> queryParameters) throws IOException {
        Response response = makeGetRequest(client, urlApi, queryParameters);
        if (response.body() == null)
            throw new IllegalArgumentException("body of response didn't have info");

        return response.body().byteStream();
    }

    static Response makeGetRequest(OkHttpClient client, String urlApi, Map<String, String> queryParameters) throws IOException {
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
        Response response = getResponse(client, request);
        if (response == null)
            throw new IllegalArgumentException("No response after 10 times!");
        return response;
    }

    static Response getResponse(OkHttpClient client, Request request) {
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

