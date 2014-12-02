package com.redhat.lightblue.hibernate.ogm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.hibernate.ogm.model.key.spi.EntityKeyMetadata;
import org.hibernate.ogm.model.spi.Tuple;
import org.hibernate.ogm.model.spi.TupleOperation;
import org.hibernate.ogm.model.spi.TupleSnapshot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LightblueTupleSnapshot implements TupleSnapshot {

    public static enum OperationType {
        INSERT,
        UPDATE
    }

    private final ObjectNode node;
    public final String entityName;
    public final String entityVersion;
    public final OperationType operationType;
    private final Set<String> columnNames;

    public LightblueTupleSnapshot(ObjectNode node, EntityKeyMetadata keyMetadata, OperationType operationType) {
        this.node = node;
        this.operationType = operationType;
        LightblueEntityMetadataId metadataId = LightblueEntityMetadataId.extractEntityInfo(keyMetadata);
        this.entityName = metadataId.entityName;
        this.entityVersion = metadataId.entityVersion;
        columnNames = findLeafNodeFieldNames("", node);
    }

    public LightblueTupleSnapshot(ObjectNode node, String entityName, String entityVersion, OperationType operationType) {
        this.node = node;
        this.entityName = entityName;
        this.entityVersion = entityVersion;
        this.columnNames = findLeafNodeFieldNames("", node);
        this.operationType = operationType;
    }

    public Object get(String column) {
        JsonNode context = node;
        for (String path : column.split("\\.")) {
            context = context.get(path);
        }
        if (context == null) {
            return null;
        }
        if (context.isNumber()) {
            return context.numberValue();
        }
        else if (context.isBoolean()) {
            return context.booleanValue();
        }
        else if (context.isNull()) {
            return null;
        }
        return context.asText();
    }

    public boolean isEmpty() {
        return node.isNull();
    }

    public Set<String> getColumnNames() {
        return columnNames;
    }

    private Set<String> findLeafNodeFieldNames(String path, ObjectNode node) {
        Set<String> names = new HashSet<String>();
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode current = node.get(fieldName);
            if (current.isObject()) {
                names.addAll(findLeafNodeFieldNames(path + fieldName + ".", (ObjectNode) current));
            }
            else {
                names.add(path + fieldName);
            }
        }
        return names;
    }

    public JsonNode getNode() {
        return node;
    }

    public void update(Tuple tuple) {
        for (TupleOperation operation : tuple.getOperations()) {
            columnNames.add(operation.getColumn());
            ObjectNode context = node;
            String[] components = operation.getColumn().split("\\.");
            for (int i = 0; i < components.length - 1; i++) {
                context = ensureNode(context, components[i]);
            }
            String fieldName = components[components.length - 1];
            switch (operation.getType()) {
            case PUT:
                context.putPOJO(fieldName, operation.getValue());
                break;
            default:
                context.putNull(fieldName);
            }
        }
    }

    private ObjectNode ensureNode(ObjectNode context, String fieldName) {
        if (context.has(fieldName)) {
            return (ObjectNode) context.get(fieldName);
        }
        else {
            return context.putObject(fieldName);
        }
    }

}
