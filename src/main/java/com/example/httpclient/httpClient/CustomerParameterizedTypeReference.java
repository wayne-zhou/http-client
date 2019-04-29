package com.example.httpclient.httpClient;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;

/**
 * Created by zhouwei on 2019/4/29
 **/
public class CustomerParameterizedTypeReference extends ParameterizedTypeReference<CustomerParameterizedTypeReference> {
    private Type customerType = null;

    public CustomerParameterizedTypeReference(Type type) {
        this.customerType = type;
    }

    public Type getType() {
        return this.customerType;
    }

    public Type getCustomerType() {
        return this.customerType;
    }

    public void setCustomerType(Type customerType) {
        this.customerType = customerType;
    }
}
