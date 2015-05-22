package com.redhat.lightblue.hibernate.ogm;

import java.util.Map;

import org.hibernate.ogm.datastore.spi.BaseDatastoreProvider;
import org.hibernate.ogm.dialect.spi.GridDialect;
import org.hibernate.service.spi.Configurable;

import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.LightblueClientConfiguration;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.hibernate.ogm.config.LightblueClientProperties;

public class LightblueDatastoreProvider extends BaseDatastoreProvider implements Configurable {

    private static final long serialVersionUID = -6339184969088721584L;

    private LightblueClientConfiguration config;

    @Override
    public Class<? extends GridDialect> getDefaultDialect() {
        return LightblueDialect.class;
    }

    public LightblueClient getLightblueClient() {
        return new LightblueHttpClient(config);
    }

    private String getOrDefault(Map config, String name, String defaultValue) {
        Object value = config.get(name);
        if (value == null) {
            return defaultValue;
        }
        else {
            return value.toString();
        }
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public void configure(Map config) {
        String host = (String) config.get(LightblueClientProperties.HOST);
        String port = getOrDefault(config, LightblueClientProperties.PORT, "80");
        String protocol = getOrDefault(config, LightblueClientProperties.PROTOCOL, "https");
        String defaultMetadataServiceURI = String.format("%s://%s:%s/rest/metadata", protocol, host, port);
        String defaultDataServiceURI = String.format("%s://%s:%s/rest/data", protocol, host, port);
        String metadataServiceURI = getOrDefault(config, LightblueClientProperties.METADATA_SERVICE_URI, defaultMetadataServiceURI);
        String dataServiceURI = getOrDefault(config, LightblueClientProperties.DATA_SERVICE_URI, defaultDataServiceURI);
        boolean useCertAuth = Boolean.parseBoolean(getOrDefault(config, LightblueClientProperties.USE_CERT_AUTH, "false"));
        String caFilePath = (String) config.get(LightblueClientProperties.CA_FILE_PATH);
        String certFilePath = (String) config.get(LightblueClientProperties.CERT_FILE_PATH);
        String certPassword = (String) config.get(LightblueClientProperties.CERT_PASSWORD);
        this.config = new LightblueClientConfiguration();
        this.config.setMetadataServiceURI(metadataServiceURI);
        this.config.setDataServiceURI(dataServiceURI);
        this.config.setUseCertAuth(useCertAuth);
        this.config.setCaFilePath(caFilePath);
        this.config.setCertFilePath(certFilePath);
        this.config.setCertPassword(certPassword);
    }
}
