package com.redhat.lightblue.hibernate.ogm;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import com.redhat.lightblue.hibernate.ogm.test.model.User;

public class PersistenceTest {

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("lightblue.jpa");
    private EntityManager entityManager = entityManagerFactory.createEntityManager();

    public void persist(String id) {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
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

    private User fetch() {
        User user = entityManager.find(User.class, "foo");
        return user;
    }

    @Test
    public void testPersistence() {
        persist(null);
    }

    @Test
    public void testRetrieval() {
        entityManager.getTransaction().begin();
        User user = fetch();
        assertEquals("bob", user.getFirstName());
        User b = entityManager.find(User.class, "546feb01e4b0c4747d300299");
        entityManager.getTransaction().commit();
    }

    @Test
    public void testRetrieval_outsideSession() {
        User user = fetch();
        assertEquals("bob", user.getFirstName());
    }

    @Test
    public void testDeletion() {
        User user = fetch();
        entityManager.remove(user);
    }

    @Test
    public void queryTest_native() {
        List<?> resultList = entityManager.createNativeQuery("{\"field\":\"login\", \"op\": \"=\", \"rvalue\": \"root\"}", User.class).setParameter("entityName", "user").setParameter("entityVersion", "1.0.0").getResultList();
        System.out.println(resultList);
    }

    @Test
    public void queryTest_jpql() {
        entityManager.createQuery("select user from User user where user.login = 'bserdar'").getResultList();
    }

    @Test
    public void testUpdate() {
        entityManager.getTransaction().begin();
        User user = fetch();
        user.setLogin(user.getLogin() + "!");
        entityManager.persist(user);
        entityManager.flush();
        entityManager.getTransaction().commit();
    }
}
