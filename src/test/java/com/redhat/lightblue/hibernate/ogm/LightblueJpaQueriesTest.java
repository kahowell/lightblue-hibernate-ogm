package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import org.hibernate.ogm.backendtck.queries.JpaQueriesTest;
import org.junit.Before;
import org.junit.ClassRule;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.hibernate.ogm.LightblueExternalResource.LightblueTestMethods;

public class LightblueJpaQueriesTest extends JpaQueriesTest {

    @ClassRule
    public static LightblueExternalResource lightblue = new LightblueExternalResource(new LightblueTestMethods() {

        @Override
        public JsonNode[] getMetadataJsonNodes() throws Exception {
            return new JsonNode[]{
                    loadJsonNode("./metadata/helicopter.json")
            };
        }

    });

    @Before
    public void before() {
        lightblue.getLightblueClient();
    }
}
