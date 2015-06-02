package com.redhat.lightblue.hibernate.ogm;

import static com.redhat.lightblue.util.JsonUtils.json;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.integration.test.AbstractLightblueClientCRUDController;
import com.redhat.lightblue.hibernate.ogm.test.model.Helicopter;
import com.redhat.lightblue.hibernate.ogm.test.model.User;

public class PersistenceTest extends AbstractLightblueClientCRUDController {

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("lightblue.jpa");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();

    public PersistenceTest() throws Exception {
        super();
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{json(loadResource("/metadata/user.json", true)), json(loadResource("/metadata/helicopter.json", true))};
    }

    @Test
    public void testHelicopterPersistence() {
        entityManager.getTransaction().begin();
        Helicopter heli = new Helicopter();
        heli.setMake("make");
        heli.setName("name");
        String id = UUID.randomUUID().toString();
        heli.setUUID(id);
        entityManager.persist(heli);
        entityManager.flush();
        entityManager.getTransaction().commit();

        System.out.println("helicopter>>>" + entityManager.find(Helicopter.class, id));
    }

    private void persist(String id) {
        entityManager.getTransaction().begin();
        User user = new User();
        user.setFirstName("bob");
        user.setUserId(id);
        user.setLogin("root");
        user.setNumberSites(4);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    private User fetch(String id) {
        User user = entityManager.find(User.class, id);
        return user;
    }

    @Test
    public void testPersistence() {
        String id = UUID.randomUUID().toString();
        persist(id);
    }

    @Test
    public void testRetrieval() {
        entityManager.getTransaction().begin();
        String id = UUID.randomUUID().toString();
        User user = new User();
        user.setFirstName("bob");
        user.setUserId(id);
        user.setLogin("root");
        user.setNumberSites(3);
        entityManager.persist(user);
        entityManager.flush();
        User user2 = entityManager.find(User.class, id);
        assertEquals("bob", user2.getFirstName());
        entityManager.getTransaction().commit();
    }

    @Test
    public void testRetrieval_outsideSession() {
        String id = UUID.randomUUID().toString();
        persist(id);
        User user = fetch(id);
        assertEquals("bob", user.getFirstName());
    }

    @Test
    public void testDeletion() {
        String id = UUID.randomUUID().toString();
        persist(id);
        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Test
    public void queryTest_native() {
        List<?> resultList = entityManager.createNativeQuery("{\"field\":\"login\", \"op\": \"=\", \"rvalue\": \"root\"}", User.class).setParameter("entityName", "user").setParameter("entityVersion", "1.0.0").getResultList();
        System.out.println(resultList);
    }

    //@Test
    public void queryTest_jpql() {
        entityManager.createQuery("select user from User user where user.login = 'bserdar'").getResultList();
    }

    @Test
    public void testUpdate() {
        String id = UUID.randomUUID().toString();
        persist(id);
        User user = fetch(id);
        assertEquals("root", user.getLogin());
        entityManager.getTransaction().begin();
        user.setLogin(user.getLogin() + "!");
        entityManager.persist(user);
        entityManager.flush();
        entityManager.getTransaction().commit();
        user = fetch(id);
        assertEquals("root!", user.getLogin());
    }

}
