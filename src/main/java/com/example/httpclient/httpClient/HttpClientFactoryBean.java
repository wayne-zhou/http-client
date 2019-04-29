package com.example.httpclient.httpClient;

import com.example.httpclient.common.annotation.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouwei on 2019/4/22
 * 参考FeignClientFactoryBean
 **/
public class HttpClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Class<?> type;
    private int readTimeout;
    private int connectTimeout;


    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getObject() throws Exception {
        Map<Method, RequestMappingInfo> requestMappingInfoMap = this.parseHttpClientClass(this.type);
        Object result = Proxy.newProxyInstance(this.type.getClassLoader(), new Class[]{this.type}, new HttpClientInvocationHandler(this.type, requestMappingInfoMap, this.readTimeout, this.connectTimeout));
        return result;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    private Map<Method, RequestMappingInfo> parseHttpClientClass(Class<?> type) {
        Map<Method, RequestMappingInfo> requestMappingInfoMap = new HashMap();
        StandardAnnotationMetadata classMetadata = new StandardAnnotationMetadata(type);
        Map<String, Object> httpClientAttributes = classMetadata.getAnnotationAttributes(HttpClient.class.getName(), false);
        if (httpClientAttributes.get("url") == null) {
            throw new HttpClientException(type.getName() + " url is null!");
        } else {
            String url = getUrl(httpClientAttributes);
            this.readTimeout = (Integer) httpClientAttributes.get("readTimeout");
            this.connectTimeout = (Integer) httpClientAttributes.get("connectTimeout");
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                RequestMappingInfo requestMappingInfo = new RequestMappingInfo();
                requestMappingInfo.addPath(url);
                parseRequestMappingAnnotation(classMetadata.getAnnotationAttributes(RequestMapping.class.getName(), false), requestMappingInfo);
                StandardMethodMetadata methodMetadata = new StandardMethodMetadata(method);
                parseRequestMappingAnnotation(methodMetadata.getAnnotationAttributes(RequestMapping.class.getName(), false), requestMappingInfo);
                requestMappingInfoMap.put(method, requestMappingInfo);
            }

            return requestMappingInfoMap;
        }
    }

    private String getUrl(Map<String, Object> attributes) {
        String url = resolve((String)attributes.get("url"));
        if (StringUtils.hasText(url)) {
            if (!url.contains("://")) {
                url = "http://" + url;
            }

            try {
                new URL(url);
            } catch (MalformedURLException var4) {
                throw new IllegalArgumentException(url + " is malformed", var4);
            }
        }

        return url;
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            try {
                AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext)this.applicationContext;
                value = abstractApplicationContext.getBeanFactory().resolveEmbeddedValue(value);
            } catch (IllegalStateException var3) {
                ;
            }
        }

        return StringUtils.hasText(value) && this.applicationContext instanceof ConfigurableApplicationContext ?
                ((ConfigurableApplicationContext)this.applicationContext).getEnvironment().resolvePlaceholders(value) : value;
    }

    private void parseRequestMappingAnnotation(Map<String, Object> paramsMap, RequestMappingInfo requestMappingInfo) {
        if(paramsMap == null){
            return;
        }

        String[] path = (String[])paramsMap.get("path");
        if (!ObjectUtils.isEmpty(path)) {
            requestMappingInfo.addPath(resolve(path[0]));
        }

        RequestMethod[] method = (RequestMethod[])paramsMap.get("method");
        if (!ObjectUtils.isEmpty(method)) {
            requestMappingInfo.setMethod(method[0].toString());
        }

        String[] params = (String[])paramsMap.get("params");
        if (!ObjectUtils.isEmpty(params)) {
            for (String param : params) {
                requestMappingInfo.addParams(resolve(param));
            }
        }

        String[] headers = (String[])paramsMap.get("headers");
        if (!ObjectUtils.isEmpty(headers)) {
            for (String header : headers) {
                requestMappingInfo.addHeader(resolve(header));
            }
        }

        String[] consumes = (String[])paramsMap.get("consumes");
        if (!ObjectUtils.isEmpty(consumes)) {
            for (String consume : consumes) {
                requestMappingInfo.addHeader("Accept", consume);
            }
        }


        String[] produces = (String[])paramsMap.get("produces");
        if (!ObjectUtils.isEmpty(produces)) {
            for (String produce : produces) {
                requestMappingInfo.addHeader("Content-Type", produce);
            }
        }
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
