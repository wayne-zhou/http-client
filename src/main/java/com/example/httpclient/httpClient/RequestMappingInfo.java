package com.example.httpclient.httpClient;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Created by zhouwei on 2019/4/22
 **/
public class RequestMappingInfo {
    private String path = "";
    private String method = "";
    private HttpHeaders headers = new HttpHeaders();
    private MultiValueMap<String, String> params = new LinkedMultiValueMap();

    public void addPath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (path.startsWith("/")) {
            this.path = this.path + path;
        } else if (this.path.length() == 0) {
            this.path = this.path + path;
        } else {
            this.path = this.path + "/" + path;
        }

    }

    public void addHeader(String header) {
        if (header != null) {
            String[] headers = header.split("=");
            if (headers.length == 2) {
                this.headers.add(headers[0].trim(), headers[1]);
            }

        }
    }

    public void addHeader(String key, String value) {
        this.headers.add(key, value);
    }

    public void addParams(String param) {
        if (param != null) {
            String[] params = param.split("=");
            if (params.length == 2) {
                this.params.add(params[0].trim(), params[1]);
            }

        }
    }

    public void addParams(String key, String value) {
        this.params.add(key, value);
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public MultiValueMap<String, String> getParams() {
        return this.params;
    }

    public void setParams(MultiValueMap<String, String> params) {
        this.params = params;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
