package com.redhat.lightblue.hibernate.ogm;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ogm.backendtck.associations.onetoone.Cavalier;
import org.hibernate.ogm.backendtck.associations.onetoone.Horse;
import org.junit.Test;

import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;

@InMemoryMongoServer
public class MyTest extends AbstractLightblueOgmTestCase {

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{
                Horse.class,
                Cavalier.class
        };
    }

    @Test
    public void testTheThing() {
        final Session session = openSession();
        Transaction transaction = session.beginTransaction();
        Horse horse = new Horse("palefrenier");
        horse.setName("Palefrenier");
        Cavalier cavalier = new Cavalier("caroline");
        cavalier.setName("Caroline");
        cavalier.setHorse(horse);
        session.persist(horse);
        session.persist(cavalier);
        transaction.commit();
        session.clear();
        transaction = session.beginTransaction();
        cavalier = (Cavalier) session.get(Cavalier.class, cavalier.getId());
        horse = cavalier.getHorse();
        session.delete(cavalier);
        session.delete(horse);
        transaction.commit();
        session.close();
        checkCleanCache();
    }

}
