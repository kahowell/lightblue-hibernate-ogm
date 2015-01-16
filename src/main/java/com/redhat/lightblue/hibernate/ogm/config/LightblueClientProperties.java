package com.redhat.lightblue.hibernate.ogm.config;

import org.hibernate.ogm.cfg.OgmProperties;

public interface LightblueClientProperties extends OgmProperties {
    /* Non-standard */
    public static final String PORT = "com.redhat.lightblue.port";
    public static final String PROTOCOL = "com.redhat.lightblue.protocol";

    /* Standard */
    public static final String CERT_PASSWORD = "com.redhat.lightblue.certPassword";
    public static final String CA_FILE_PATH = "com.redhat.lightblue.caFilePath";
    public static final String CERT_FILE_PATH = "com.redhat.lightblue.certFilePath";
    public static final String USE_CERT_AUTH = "com.redhat.lightblue.useCertAuth";
    public static final String DATA_SERVICE_URI = "com.redhat.lightblue.dataServiceURI";
    public static final String METADATA_SERVICE_URI = "com.redhat.lightblue.metadataServiceURI";
}
