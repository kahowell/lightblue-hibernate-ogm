package com.redhat.lightblue.hibernate.ogm;

import java.io.IOException;

import org.junit.runners.model.TestClass;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.integration.test.AbstractLightblueClientCRUDController;
import com.redhat.lightblue.client.response.LightblueResponse;

public class LightblueExternalResource extends BeforeAfterTestRule {

    public interface LightblueTestMethods {

        JsonNode[] getMetadataJsonNodes() throws Exception;

    }

    private final LightblueTestMethods methods;
    private final int httpServerPort;

    private ArtificialCRUDControllerWithRest controller;

    public LightblueExternalResource(LightblueTestMethods methods) {
        this(methods, 8000);
    }

    public LightblueExternalResource(LightblueTestMethods methods, Integer httpServerPort) {
        super(new TestClass(ArtificialCRUDControllerWithRest.class));

        if (methods == null) {
            throw new IllegalArgumentException("Must provide an instance of LightblueTestMethods");
        }
        this.methods = methods;
        this.httpServerPort = httpServerPort;
    }

    protected AbstractLightblueClientCRUDController getControllerInstance() {
        if (controller == null) {
            try {
                controller = new ArtificialCRUDControllerWithRest(httpServerPort);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create test CRUD Controller", e);
            }
        }
        return controller;
    }

    public LightblueClient getLightblueClient() {
        return getControllerInstance().getLightblueClient();
    }

    public LightblueResponse loadData(String entityName, String entityVersion, String resourcePath) throws IOException {
        return getControllerInstance().loadData(entityName, entityVersion, resourcePath);
    }

    public int getHttpPort() {
        return getControllerInstance().getHttpPort();
    }

    public String getDataUrl() {
        return getControllerInstance().getDataUrl();
    }

    public String getMetadataUrl() {
        return getControllerInstance().getMetadataUrl();
    }

    private class ArtificialCRUDControllerWithRest extends AbstractLightblueClientCRUDController {

        public ArtificialCRUDControllerWithRest(int httpServerPort) throws Exception {
            super(httpServerPort);
        }

        @Override
        protected JsonNode[] getMetadataJsonNodes() throws Exception {
            return methods.getMetadataJsonNodes();
        }

    }

}
