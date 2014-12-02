package com.redhat.lightblue.hibernate.ogm;

import org.hibernate.ogm.datastore.spi.BaseDatastoreProvider;
import org.hibernate.ogm.dialect.spi.GridDialect;
import org.hibernate.ogm.query.spi.QueryParserService;

import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.http.LightblueHttpClient;

public class LightblueDatastoreProvider extends BaseDatastoreProvider {

    public Class<? extends GridDialect> getDefaultDialect() {
        return LightblueDialect.class;
    }

    public LightblueClient getLightblueClient() {
        return new LightblueHttpClient();
    }
}
