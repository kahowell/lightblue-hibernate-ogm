package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import org.hibernate.ogm.backendtck.queries.SimpleQueriesTest;
import org.junit.Before;
import org.junit.ClassRule;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.hibernate.ogm.LightblueExternalResource.LightblueTestMethods;
import com.redhat.lightblue.hibernate.ogm.test.model.Helicopter;

public class LightblueSimpleQueriesTest extends SimpleQueriesTest {

    @ClassRule
    public static LightblueExternalResource lightblue = new LightblueExternalResource(new LightblueTestMethods() {

        @Override
        public JsonNode[] getMetadataJsonNodes() throws Exception {
            return new JsonNode[]{
                    loadJsonNode("./metadata/helicopter.json")
            };
        }

    });

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{
                Helicopter.class
        };
    }

    @Before
    public void before() {
        lightblue.getLightblueClient();
    }

}
