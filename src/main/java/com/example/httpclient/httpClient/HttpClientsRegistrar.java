package com.example.httpclient.httpClient;

import com.example.httpclient.common.annotation.EnableHttpClients;
import com.example.httpclient.common.annotation.HttpClient;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouwei on 2019/4/19
 * 参考 FeignClientsRegistrar
 **/
public class HttpClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {

    private ResourceLoader resourceLoader;

    private ClassLoader beanClassLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(HttpClient.class));
        Set<String> basePackages = getBasePackages(importingClassMetadata);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata metadata = beanDefinition.getMetadata();
                    Assert.isTrue(metadata.isInterface(),"@HttpClient can only be specified on an interface");

                    Map<String, Object> attributes = metadata.getAnnotationAttributes(HttpClient.class.getCanonicalName());
                    registerFeignClient(registry, metadata, attributes);
                }
            }
        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (!beanDefinition.getMetadata().isIndependent()) {
                    return false;
                }

                if (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().getInterfaceNames().length == 1 && Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {
                    try {
                        Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), HttpClientsRegistrar.this.beanClassLoader);
                        return !target.isAnnotation();
                    } catch (Exception var3) {
                        this.logger.error("Could not load target class: " + beanDefinition.getMetadata().getClassName(), var3);
                    }
                }

                return true;
            }
        };
    }

    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata){
        Set<String> basePackages = new HashSet<>();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableHttpClients.class.getName()));

        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        return basePackages;
    }

    private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String beanName = annotationMetadata.getClassName();
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }

        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(HttpClient.class);
        definition.addPropertyValue("type", beanName);
        if (attributes.get("readTimeout") != null) {
            definition.addPropertyValue("readTimeout", attributes.get("readTimeout"));
        }

        if (attributes.get("connectTimeout") != null) {
            definition.addPropertyValue("connectTimeout", attributes.get("connectTimeout"));
        }

        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        String alias = beanName + "HttpClient";
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

}
