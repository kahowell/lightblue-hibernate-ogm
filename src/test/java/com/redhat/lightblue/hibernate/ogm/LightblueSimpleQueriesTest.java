package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import org.hibernate.ogm.backendtck.queries.SimpleQueriesTest;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.hibernate.ogm.LightblueOgmJUnitRunner.LightblueTestMethods;
import com.redhat.lightblue.hibernate.ogm.test.model.Helicopter;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;

@RunWith(LightblueOgmJUnitRunner.class)
@InMemoryMongoServer
public class LightblueSimpleQueriesTest extends SimpleQueriesTest implements LightblueTestMethods {

    private LightblueClient client;

    @Override
    public void setLightblueClient(LightblueClient client) {
        this.client = client;
    }

    @Override
    public JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{
                loadJsonNode("./metadata/helicopter.json")
        };
    }

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{
                Helicopter.class
        };
    }

}
