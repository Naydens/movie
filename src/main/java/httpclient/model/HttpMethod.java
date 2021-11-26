package httpclient.model;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    HEAD("HEAD"),
    PATCH("PATCH");
    public String nameRequest;

    private HttpMethod(String name) {
        nameRequest = name;
    }

    public static HttpMethod getTypeOfRequest(String name) {
        for (HttpMethod value : HttpMethod.values()) {
            if (value.nameRequest.equalsIgnoreCase(name))
                return value;

        }
        return null;
    }
}
