package com.redhat.lightblue.hibernate.ogm;

import java.io.Serializable;

public class LightblueQueryDescriptor implements Serializable {

    public final String query;

    public LightblueQueryDescriptor(String query) {
        this.query = query;
    }
}
