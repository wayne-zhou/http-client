package com.example.httpclient.httpClient;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by zhouwei on 2019/4/22
 **/
public class HttpClientInvocationHandler implements InvocationHandler {
    private Class type;
    private RestTemplate restTemplate;
    private Map<Method, RequestMappingInfo> requestMappingInfoMap = new HashMap();
    private PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("{", "}", ":", true);

    public HttpClientInvocationHandler(Class type, Map<Method, RequestMappingInfo> requestMappingInfoMap, int readTimeout, int connectTimeout) {
        this.type = type;
        this.requestMappingInfoMap = requestMappingInfoMap;
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(readTimeout <= 0 ? 300000 : readTimeout);
        clientHttpRequestFactory.setConnectTimeout(connectTimeout <= 0 ? 300000 : connectTimeout);
        this.restTemplate = new RestTemplate(clientHttpRequestFactory);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMappingInfo requestMappingInfo =requestMappingInfoMap.get(method);
        MethodArgumentInfo methodArgumentInfo = parseMethodArgument(method, args);
        String path = helper.replacePlaceholders(requestMappingInfo.getPath(), new MapPlaceholderResolver(methodArgumentInfo.getPathVariableNamedMap()));
        Object[] pathVariableNotNamedArray = methodArgumentInfo.getPathVariableNotNamedMap().values().toArray();
        StringBuilder sb = new StringBuilder();

        queryParamHandle(sb, requestMappingInfo.getParams());
        queryParamHandle(sb, methodArgumentInfo.getRequestParamMap());
        if (sb.length() > 1) {
            if (path.indexOf("?") != -1) {
                path = path.endsWith("&") ? path + sb.toString() : path + "&" + sb.toString();
            } else {
                path = path + "?" + sb.toString();
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        copyMultiValueMap(requestMappingInfo.getHeaders(), httpHeaders);
        copyMultiValueMap(methodArgumentInfo.getRequestHeaderMap(), httpHeaders);

        Object otherContent = null;
        if (!"GET".equals(requestMappingInfo.getMethod()) && methodArgumentInfo.getOthersMap().size() > 0) {
            otherContent = methodArgumentInfo.getOthersMap().values().toArray()[0];
            if (otherContent != null && MediaType.APPLICATION_FORM_URLENCODED.equals(requestMappingInfo.getHeaders().getContentType())) {
                Map<String, Object> otherContentTemp = (Map)JsonUtils.convertToObject(JsonUtils.convertToJson(otherContent), Map.class);
                MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap();
                Iterator var13 = otherContentTemp.entrySet().iterator();

                while(true) {
                    while(var13.hasNext()) {
                        Map.Entry entry = (Map.Entry)var13.next();
                        if (entry.getValue() instanceof List) {
                            ((List)entry.getValue()).stream().forEach(item -> multiValueMap.add(entry.getKey() + "", item + ""));
                        } else {
                            multiValueMap.add(entry.getKey() + "", entry.getValue() + "");
                        }
                    }

                    otherContent = multiValueMap;
                    break;
                }
            }
        }

        ResponseEntity responseEntity = restTemplate.exchange(path, HttpMethod.valueOf(requestMappingInfo.getMethod()), new HttpEntity(otherContent, httpHeaders), new CustomerParameterizedTypeReference(method.getGenericReturnType()), pathVariableNotNamedArray);
        return responseEntity.getBody();
    }

    private MethodArgumentInfo parseMethodArgument(Method method, Object[] args) {
        MethodArgumentInfo methodArgumentInfo = new MethodArgumentInfo();
        if(args == null){
            return methodArgumentInfo;
        }

        for(int i = 0; i < args.length; i++) {
            Annotation[] annotations = method.getParameterAnnotations()[i];
            if (ObjectUtils.isEmpty(annotations)) {
                methodArgumentInfo.getOthersMap().put(i + "", args[i]);
                continue;
            }

            for (Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    this.setAnnotationValue(((PathVariable)annotation).value(), args[i], i, methodArgumentInfo.getPathVariableNamedMap(), methodArgumentInfo.getPathVariableNotNamedMap());
                } else if (annotation instanceof RequestParam) {
                    this.setAnnotationValue(((RequestParam)annotation).value(), args[i], methodArgumentInfo.getRequestParamMap());
                } else if (annotation instanceof RequestHeader) {
                    this.setAnnotationValue(((RequestHeader)annotation).value(), args[i], methodArgumentInfo.getRequestHeaderMap());
                }
            }
        }
        return methodArgumentInfo;
    }

    private void setAnnotationValue(String value, Object arg, MultiValueMap<String, String> map) {
        if (StringUtils.hasText(value)) {
            map.add(value, arg != null ? arg.toString() : null);
            return;
        }

        try {
            Map<String, Object> pathVariableArgMap = (Map)JsonUtils.convertToObject(JsonUtils.convertToJson(arg), Map.class);
            pathVariableArgMap.forEach((k, v) -> {
                map.add(k, v != null ? v.toString() : null);
            });
        } catch (Exception exception) {}
    }

    private void setAnnotationValue(String value, Object arg, int i, LinkedHashMap<String, Object> namedMap, LinkedHashMap<String, Object> notNamedMap) {
        if (StringUtils.hasText(value)) {
            if (namedMap != null) {
                namedMap.put(value, arg);
            }
            return;
        }

        try {
            Map<String, Object> pathVariableArgMap = (Map)JsonUtils.convertToObject(JsonUtils.convertToJson(arg), Map.class);
            pathVariableArgMap.forEach((k, v) -> {
                if (namedMap != null) {
                    namedMap.put(k, v);
                }
            });
        } catch (Exception exception) {
            if (notNamedMap != null) {
                notNamedMap.put(i + "", arg);
            }
        }
    }

    private void queryParamHandle(StringBuilder sb, MultiValueMap<String, String> annotationRequestParamMap) throws Exception {
        Iterator var3 = annotationRequestParamMap.entrySet().iterator();

        while(true) {
            Map.Entry entry;
            do {
                if (!var3.hasNext()) {
                    return;
                }

                entry = (Map.Entry)var3.next();
            } while(entry.getValue() == null);


            if (sb.length() > 0) {
                sb.append("&");
            }

            Iterator var5 = ((List)entry.getValue()).iterator();

            while(var5.hasNext()) {
                String obj = (String)var5.next();
                sb.append((String)entry.getKey()).append("=").append(obj != null ? obj : "");
            }
        }
    }

    private void copyMultiValueMap(MultiValueMap<String, String> source, MultiValueMap<String, String> target) {
        Iterator var3 = source.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var3.next();
            Iterator var5 = ((List)entry.getValue()).iterator();

            while(var5.hasNext()) {
                String value = (String)var5.next();
                target.add(entry.getKey(), value);
            }
        }
    }

}
