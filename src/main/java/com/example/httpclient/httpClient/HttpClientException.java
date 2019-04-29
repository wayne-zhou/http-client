package com.example.httpclient.httpClient;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by zhouwei on 2019/4/22
 **/
public class HttpClientException extends NestedRuntimeException {
    private String errorCode;

    public HttpClientException(String msg) {
        super(msg);
    }

    public HttpClientException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public HttpClientException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public HttpClientException(String errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
