package com.redhat.lightblue.hibernate.ogm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.ogm.model.key.spi.EntityKeyMetadata;

public class LightblueEntityMetadataId {
    public final String entityName;
    public final String entityVersion;
    public static final Pattern LIGHTBLUE_TABLE_PATTERN = Pattern.compile("(.*)(/(.*))??");

    private LightblueEntityMetadataId(String entityName, String entityVersion) {
        if (entityName == null) {
            throw new IllegalArgumentException("Must provide an entityName");
        }
        this.entityName = entityName;
        this.entityVersion = entityVersion;
    }

    static LightblueEntityMetadataId extractEntityInfo(EntityKeyMetadata keyMetadata) {
        Matcher entityVersionMatcher = LIGHTBLUE_TABLE_PATTERN.matcher(keyMetadata.getTable());
        if (!entityVersionMatcher.matches()) {
            throw new IllegalArgumentException("table does not match lightblue table format: {entityName}/{version}");
        }
        String entityVersion = entityVersionMatcher.group(3);
        if (entityVersion == null || entityVersion.length() > 0) {
            entityVersion = null;
        }
        return new LightblueEntityMetadataId(entityVersionMatcher.group(1), entityVersion);
    }
}
