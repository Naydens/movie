package httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpclient.model.HttpMethod;
import httpclient.model.Movie;
import okhttp3.*;

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
    static String discoverMovie = "/discover/movie";
    static String configuration = "/configuration";
    static String pageQP = "page";
    static String apiKeyQP = "api_key";
    static String resultsPath = "results";
    static String imagesPath = "images";
    static String totalPagesPath = "total_pages";
    static String baseUrlPath = "base_url";
    static String posterSizesPath = "poster_sizes";
    static String pathToImagesLocal = "C:\\Users\\nadya\\Desktop\\imges\\";
    static int minImagesSize = 300;
    static OkHttpClient client = new OkHttpClient();
    static Map<String, String> queryParameters = new HashMap<>();

    public static void main(String[] args) throws IOException {
/*        //36-68 lines - find max values for all key movie
        int tempSize = 0;
        int currentSize = 0;
        Map<String, Integer> map = new HashMap<>();
        for (int i = 1; i <= pagesCount; i++) {
            queryParameters.put(pageQP, String.valueOf(i));
            JsonNode response = objectMapper.readTree(getHttpBody(root + discoverMovie, queryParameters));
            Iterator<JsonNode> results = response.path(resultsPath).elements();
            while (results.hasNext()) {
                Iterator<Map.Entry<String, JsonNode>> fields = results.next().fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> next = fields.next();
                    if (next.getValue().isTextual()) {
                        if (!map.containsKey(next.getKey())) {
                            map.put(next.getKey(), next.getValue().asText().length());
                        } else {
                            currentSize = next.getValue().asText().length();
                            tempSize = map.get(next.getKey());
                            if (currentSize > tempSize) {
                                map.put(next.getKey(), currentSize);
                            }
                        }

                    }
                }

            }
        }
        Iterator<Map.Entry<String, Integer>> mapMax = map.entrySet().iterator();
        while (mapMax.hasNext()) {
            Map.Entry<String, Integer> param = mapMax.next();
            System.out.println(param);
        }

        //70-96 lines - download 9939 images, size more minImagesSize
        queryParameters.remove(pageQP);
        parent = objectMapper.readTree(getHttpBody( root + configuration, queryParameters));
        baseUrl = parent.path(imagesPath).path(baseUrlPath).asText();
        Iterator<JsonNode> elements = parent.path(imagesPath).withArray(posterSizesPath).elements();

        while (elements.hasNext()) {
            String tmpSize = elements.next().asText();
            if (Integer.valueOf(tmpSize.substring(1)) >  minImagesSize) {
                posterSize = tmpSize;
                break;
            }
        }
        Movie[] movieP = null;
        for (int i = 1; i <= pagesCount; i++) {
            movieP = getArrayOfFilmsFromPages( root, queryParameters, i);
            for (Movie movie : movieP) {
                File tempFile = new File(pathToImagesLocal + movie.getPosterPath());
                if (tempFile.exists()) {
                    continue;
                }
                OutputStream outputStream = new FileOutputStream(pathToImagesLocal + movie.getPosterPath());
                InputStream inputStream = getHttpBodyInputStream( baseUrl + posterSize + "/" + movie.getPosterPath(), queryParameters);
                copy(inputStream, outputStream);
                outputStream.close();
            }
        }*/
    }

    public static int countPages() throws IOException {
        queryParameters.put(apiKeyQP, apiKey);
        JsonNode parent = objectMapper.readTree(getHttpBody(root + discoverMovie, queryParameters));
        return parent.path(totalPagesPath).asInt();
    }

    static public Movie[] getArrayOfFilmsFromPages(int numberOfPage) throws IOException {
        queryParameters.put(pageQP, String.valueOf(numberOfPage));
        JsonNode parent = objectMapper.readTree(getHttpBody(root + discoverMovie, queryParameters));
        String content = parent.path(resultsPath).toString();
       return objectMapper.readValue(content, Movie[].class);
    }

    static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    static String getHttpBody(String urlApi, Map<String, String> queryParameters) throws IOException {
        Response response = makeGetRequest(urlApi, queryParameters);
        if (response.body() == null)
            throw new IllegalArgumentException("body of response didn't have info");

        return response.body().string();
    }

    static InputStream getHttpBodyInputStream(String urlApi, Map<String, String> queryParameters) throws IOException {
        Response response = makeGetRequest(urlApi, queryParameters);
        if (response.body() == null)
            throw new IllegalArgumentException("body of response didn't have info");

        return response.body().byteStream();
    }

    static Response makeGetRequest(String urlApi, Map<String, String> queryParameters) throws IOException {
        return makeRequest(HttpMethod.GET, null, urlApi, queryParameters);
    }

    static Response makeRequest(HttpMethod method, RequestBody body, String urlApi, Map<String, String> queryParameters) throws IOException {
        assert method != null : "Method should be defined!";

        HttpUrl.Builder url = HttpUrl.parse(urlApi).newBuilder();
        Iterator<Map.Entry<String, String>> parameters = queryParameters.entrySet().iterator();
        while (parameters.hasNext()) {
            Map.Entry<String, String> param = parameters.next();
            url.addQueryParameter(param.getKey(), param.getValue());
        }
        String urlFull = url.build().toString();
        Request request = new Request.Builder()
                .url(urlFull).method(method.nameRequest, body)
                .build();
        Response response = getResponse(request);

        if (response == null)
            throw new IllegalArgumentException("No response after 10 times!");
        return response;
    }

    static Response makeRequest(String method, RequestBody body, String urlApi, Map<String, String> queryParameters) throws IOException {
        assert method != null : "Method should be defined!";
        assert HttpMethod.getTypeOfRequest(method) != null : "Method not found!";
        return makeRequest(HttpMethod.getTypeOfRequest(method), body, urlApi, queryParameters);
    }

    static Response getResponse(Request request) {
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

