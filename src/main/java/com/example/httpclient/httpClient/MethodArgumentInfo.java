package com.example.httpclient.httpClient;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;

/**
 * 参数分类
 * Created by zhouwei on 2019/4/29
 **/
public class MethodArgumentInfo {
    LinkedHashMap<String, Object> pathVariableNamedMap = new LinkedHashMap();
    LinkedHashMap<String, Object> pathVariableNotNamedMap = new LinkedHashMap();
    MultiValueMap<String, String> requestParamMap = new LinkedMultiValueMap();
    MultiValueMap<String, String> requestHeaderMap = new LinkedMultiValueMap();
    LinkedHashMap<String, Object> othersMap = new LinkedHashMap();

    public MethodArgumentInfo() {
    }

    public MethodArgumentInfo(LinkedHashMap<String, Object> pathVariableNamedMap, LinkedHashMap<String, Object> pathVariableNotNamedMap, MultiValueMap<String, String> requestParamMap, MultiValueMap<String, String> requestHeaderMap, LinkedHashMap<String, Object> othersMap) {
        this.pathVariableNamedMap = pathVariableNamedMap;
        this.pathVariableNotNamedMap = pathVariableNotNamedMap;
        this.requestParamMap = requestParamMap;
        this.requestHeaderMap = requestHeaderMap;
        this.othersMap = othersMap;
    }

    public LinkedHashMap<String, Object> getPathVariableNamedMap() {
        return pathVariableNamedMap;
    }

    public void setPathVariableNamedMap(LinkedHashMap<String, Object> pathVariableNamedMap) {
        this.pathVariableNamedMap = pathVariableNamedMap;
    }

    public LinkedHashMap<String, Object> getPathVariableNotNamedMap() {
        return pathVariableNotNamedMap;
    }

    public void setPathVariableNotNamedMap(LinkedHashMap<String, Object> pathVariableNotNamedMap) {
        this.pathVariableNotNamedMap = pathVariableNotNamedMap;
    }

    public MultiValueMap<String, String> getRequestParamMap() {
        return requestParamMap;
    }

    public void setRequestParamMap(MultiValueMap<String, String> requestParamMap) {
        this.requestParamMap = requestParamMap;
    }

    public MultiValueMap<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public void setRequestHeaderMap(MultiValueMap<String, String> requestHeaderMap) {
        this.requestHeaderMap = requestHeaderMap;
    }

    public LinkedHashMap<String, Object> getOthersMap() {
        return othersMap;
    }

    public void setOthersMap(LinkedHashMap<String, Object> othersMap) {
        this.othersMap = othersMap;
    }
}
