package com.redhat.lightblue.hibernate.ogm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.engine.query.spi.NamedParameterDescriptor;
import org.hibernate.engine.query.spi.ParameterMetadata;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.ogm.dialect.query.spi.BackendQuery;
import org.hibernate.ogm.dialect.query.spi.ClosableIterator;
import org.hibernate.ogm.dialect.query.spi.ParameterMetadataBuilder;
import org.hibernate.ogm.dialect.query.spi.QueryableGridDialect;
import org.hibernate.ogm.dialect.spi.AssociationContext;
import org.hibernate.ogm.dialect.spi.AssociationTypeContext;
import org.hibernate.ogm.dialect.spi.BaseGridDialect;
import org.hibernate.ogm.dialect.spi.ModelConsumer;
import org.hibernate.ogm.dialect.spi.NextValueRequest;
import org.hibernate.ogm.dialect.spi.TupleContext;
import org.hibernate.ogm.model.key.spi.AssociationKey;
import org.hibernate.ogm.model.key.spi.AssociationKeyMetadata;
import org.hibernate.ogm.model.key.spi.EntityKey;
import org.hibernate.ogm.model.key.spi.EntityKeyMetadata;
import org.hibernate.ogm.model.spi.Association;
import org.hibernate.ogm.model.spi.Tuple;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.type.StringType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.lightblue.client.enums.ExpressionOperation;
import com.redhat.lightblue.client.expression.query.Query;
import com.redhat.lightblue.client.expression.query.ValueQuery;
import com.redhat.lightblue.client.projection.FieldProjection;
import com.redhat.lightblue.client.projection.Projection;
import com.redhat.lightblue.client.request.data.DataDeleteRequest;
import com.redhat.lightblue.client.request.data.DataFindRequest;
import com.redhat.lightblue.client.request.data.DataInsertRequest;
import com.redhat.lightblue.client.request.data.DataSaveRequest;
import com.redhat.lightblue.client.response.LightblueResponse;
import com.redhat.lightblue.hibernate.ogm.LightblueTupleSnapshot.OperationType;

public class LightblueDialect extends BaseGridDialect implements QueryableGridDialect<String> {

    private LightblueDatastoreProvider provider;

    private ObjectMapper mapper = new ObjectMapper();

    public LightblueDialect(LightblueDatastoreProvider provider) {
        this.provider = provider;
    }

    public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
        return null; // lightblue does not support locking
    }

    public Tuple getTuple(EntityKey key, TupleContext tupleContext) {
        ObjectNode object = fetchObject(key, tupleContext);
        if (object != null) {
            return new Tuple(new LightblueTupleSnapshot(object, key.getMetadata(), OperationType.UPDATE));
        }
        if (isInQueue(key, tupleContext)) {
            return createTuple(key, tupleContext);
        }
        return null;
    }

    private boolean isInQueue(EntityKey key, TupleContext tupleContext) {
        return tupleContext.getOperationsQueue() != null && tupleContext.getOperationsQueue().contains(key);
    }

    private ObjectNode fetchObject(EntityKey key, TupleContext tupleContext) {
        LightblueEntityMetadataId entityId = LightblueEntityMetadataId.extractEntityInfo(key.getMetadata());
        DataFindRequest request = new DataFindRequest(entityId.entityName, entityId.entityVersion);
        request.where(new ValueQuery("_id", ExpressionOperation.EQUALS, key.getColumnValues()[0].toString()));
        List<Projection> projections = new ArrayList<Projection>(Arrays.asList(projectionsFromColumns(tupleContext.getSelectableColumns())));
        projections.add(new FieldProjection("_id", true, true));
        request.select(projections);
        return (ObjectNode) provider.getLightblueClient().data(request).getJson().get("processed").get(0);
    }

    private Projection[] projectionsFromColumns(Collection<String> columns) {
        Projection[] projections = new Projection[columns.size()];
        Iterator<String> columnsIter = columns.iterator();
        for (int i = 0; i < projections.length; i++) {
            projections[i] = new FieldProjection(columnsIter.next(), true, true);
        }
        return projections;
    }



    public Tuple createTuple(EntityKey key, TupleContext tupleContext) {
        return new Tuple(new LightblueTupleSnapshot(mapper.createObjectNode(), key.getMetadata(), OperationType.INSERT));
    }

    public void insertOrUpdateTuple(EntityKey key, Tuple tuple, TupleContext tupleContext) {
        LightblueTupleSnapshot snapshot = (LightblueTupleSnapshot) tuple.getSnapshot();
        snapshot.update(tuple);
        if (snapshot.operationType == OperationType.INSERT) {
            DataInsertRequest request = new DataInsertRequest(snapshot.entityName, snapshot.entityVersion);
            request.create(snapshot.getNode());
            request.returns(new FieldProjection("_id", true, true));
            System.out.println(snapshot.getNode());
            System.out.println(provider.getLightblueClient().data(request).getJson());
        }
        else {
            DataSaveRequest request = new DataSaveRequest(snapshot.entityName, snapshot.entityVersion);
            request.create(snapshot.getNode());
            request.returns(projectionsFromColumns(snapshot.getColumnNames()));
            request.setUpsert(false);
            System.out.println(request.getBody());
            System.out.println(provider.getLightblueClient().data(request).getJson());
        }
    }

    public void removeTuple(EntityKey key, TupleContext tupleContext) {
        LightblueEntityMetadataId entityId = LightblueEntityMetadataId.extractEntityInfo(key.getMetadata());
        DataDeleteRequest request = new DataDeleteRequest(entityId.entityName, entityId.entityVersion);
        request.where(new ValueQuery("_id", ExpressionOperation.EQUALS, key.getColumnValues()[0].toString()));
        try {
        LightblueResponse data = provider.getLightblueClient().data(request);
        System.out.println(data.getJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Association getAssociation(AssociationKey key, AssociationContext associationContext) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public Association createAssociation(AssociationKey key, AssociationContext associationContext) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public void insertOrUpdateAssociation(AssociationKey key, Association association, AssociationContext associationContext) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public void removeAssociation(AssociationKey key, AssociationContext associationContext) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public boolean isStoredInEntityStructure(AssociationKeyMetadata associationKeyMetadata, AssociationTypeContext associationTypeContext) {
        return true;
        // TODO figure out
    }

    public Number nextValue(NextValueRequest request) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public void forEachTuple(ModelConsumer consumer, EntityKeyMetadata... entityKeyMetadatas) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public ClosableIterator<Tuple> executeBackendQuery(BackendQuery<String> query, QueryParameters queryParameters) {
        final String queryString = query.getQuery();
        String entityName = queryParameters.getNamedParameters().get("entityName").getValue().toString();
        String entityVersion = queryParameters.getNamedParameters().get("entityVersion").getValue().toString();
        DataFindRequest request = new DataFindRequest(entityName, entityVersion);
        request.select(new FieldProjection("*", true, true)); // FIXME dummy projection for broken client
        request.where(new Query() {

            public String toJson() {
                return queryString;
            }
        });
        JsonNode jsonNode = provider.getLightblueClient().data(request).getJson().get("processed");
        List<Tuple> tuples = new ArrayList<Tuple>(jsonNode.size());
        for (int i = 0; i < jsonNode.size(); i++) {
            tuples.add(tupleFromNode((ObjectNode) jsonNode.get(i), entityName, entityVersion));
        }
        final Iterator<Tuple> iter = tuples.iterator();
        return new ClosableIterator<Tuple>() {

            public Tuple next() {
                return iter.next();
            }

            public boolean hasNext() {
                return iter.hasNext();
            }

            public void close() {
                // NO-OP
            }
        };
    }

    private Tuple tupleFromNode(ObjectNode objectNode, String entityName, String entityVersion) {
        return new Tuple(new LightblueTupleSnapshot(objectNode, entityName, entityVersion, OperationType.UPDATE));
    }

    public ParameterMetadataBuilder getParameterMetadataBuilder() {
        return new ParameterMetadataBuilder() {

            public ParameterMetadata buildParameterMetadata(String nativeQuery) {
                Map<String, NamedParameterDescriptor> namedParams = new HashMap<String, NamedParameterDescriptor>();
                namedParams.put("entityName", new NamedParameterDescriptor("entityName", StringType.INSTANCE, null, false));
                namedParams.put("entityVersion", new NamedParameterDescriptor("entityVersion", StringType.INSTANCE, null, false));
                return new ParameterMetadata(null, namedParams);
            }
        };
    }

    public String parseNativeQuery(String nativeQuery) {
        return nativeQuery;
    }

}
