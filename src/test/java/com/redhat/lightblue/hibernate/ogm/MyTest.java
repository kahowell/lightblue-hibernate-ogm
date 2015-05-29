package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.client.expression.query.NaryLogicalQuery.and;
import static com.redhat.lightblue.client.expression.query.ValueQuery.withValue;
import static com.redhat.lightblue.client.projection.FieldProjection.includeFieldRecursively;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.expression.query.Query;
import com.redhat.lightblue.client.request.data.DataFindRequest;
import com.redhat.lightblue.hibernate.ogm.LightblueOgmJUnitRunner.LightblueTestMethods;
import com.redhat.lightblue.hibernate.ogm.test.model.User;

public class MyTest extends AbstractLightblueOgmTestCase implements LightblueTestMethods {

    private LightblueClient client;

    @Override
    public void setLightblueClient(LightblueClient client) {
        this.client = client;
    }

    @Override
    public JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{
                loadJsonNode("./metadata/user.json")
        };
    }

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{
                User.class
        };
    }

    @Test
    public void testTheThing() {
        User user = new User();
        user.setFirstName("frank");
        user.setLogin("fjones");
        user.setNumberSites(2);
        user.setUserId("1234");

        final Session session = openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(user);
        transaction.commit();
        session.clear();
        checkUserStored();

        transaction = session.beginTransaction();
        User retrievedUser = (User) session.get(User.class, user.getUserId());
        session.delete(retrievedUser);
        transaction.commit();
        session.close();
        //checkCleanCache();

    }

    @Override
    protected void checkCleanCache() {
        try {
            assertEquals("There should be no entities left!", 0, getNumberOfEntities());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void checkUserStored() {
        try {
            assertEquals("There should a user persisted", 1, getNumberOfEntities());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private int getNumberOfEntities() throws Exception {
        JsonNode userMetadata = getMetadataJsonNodes()[0].get("schema");
        String metadataVersion = userMetadata.get("version").get("value").asText();
        String metadataType = userMetadata.get("name").asText();
        DataFindRequest request = new DataFindRequest(metadataType, metadataVersion);
        List<Query> conditions = new ArrayList<Query>();
        conditions.add(withValue("firstName = frank"));
        conditions.add(withValue("login = fjones"));
        conditions.add(withValue("_id = 1234"));

        request.where(and(conditions));
        request.select(includeFieldRecursively("*"));

        User[] users = client.data(request, User[].class);

        return users.length;
    }

}
