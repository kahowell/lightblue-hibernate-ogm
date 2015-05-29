package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.hibernate.ogm.LightblueOgmJUnitRunner.LightblueTestMethods;
import com.redhat.lightblue.hibernate.ogm.test.model.User;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;

@InMemoryMongoServer
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
        transaction = session.beginTransaction();
        User retrievedUser = (User) session.get(User.class, user.getUserId());
        session.delete(retrievedUser);
        transaction.commit();
        session.close();
        checkCleanCache();

    }

}
