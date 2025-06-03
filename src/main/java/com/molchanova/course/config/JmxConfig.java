package com.molchanova.course.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.jmx.JmxEndpointAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.MBeanServerFactoryBean;

import javax.management.MBeanServer;

@Configuration
@EnableConfigurationProperties
@Import(JmxEndpointAutoConfiguration.class)
public class JmxConfig {

    @Bean
    public MBeanServerFactoryBean mBeanServerFactoryBean() {
        MBeanServerFactoryBean factory = new MBeanServerFactoryBean();
        factory.setLocateExistingServerIfPossible(true);
        factory.setDefaultDomain("coffee-shop-api");
        return factory;
    }

    @Bean
    public MBeanExporter mBeanExporter(MBeanServer mBeanServer) {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setServer(mBeanServer);
        exporter.setAssembler(new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource()));
        exporter.setNamingStrategy(new MetadataNamingStrategy(new AnnotationJmxAttributeSource()));
        exporter.setAutodetect(true);
        exporter.setExcludedBeans("dataSource", "hikariDataSource");
        return exporter;
    }
} 