package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import org.hibernate.ogm.backendtck.queries.JpaQueriesTest;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.hibernate.ogm.LightblueJUnitRunner.LightblueTestMethods;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;

@RunWith(LightblueJUnitRunner.class)
@InMemoryMongoServer
public class LightblueJpaQueriesTest extends JpaQueriesTest implements LightblueTestMethods {

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

}
