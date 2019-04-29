package com.example.httpclient.httpClient;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;

/**
 * Created by zhouwei on 2019/4/29
 **/
public class MapPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
    private Map<String, Object> values;

    public MapPlaceholderResolver(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return this.values.get(placeholderName) != null ? this.values.get(placeholderName) + "" : null;
    }

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
