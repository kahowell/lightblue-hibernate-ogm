package com.redhat.lightblue.hibernate.ogm;

import org.hibernate.ogm.utils.OgmTestCase;
import org.junit.runner.RunWith;

import com.redhat.lightblue.mongo.test.MongoServerExternalResource.InMemoryMongoServer;

@RunWith(LightblueOgmJUnitRunner.class)
@InMemoryMongoServer
public abstract class AbstractLightblueOgmTestCase extends OgmTestCase {

}
