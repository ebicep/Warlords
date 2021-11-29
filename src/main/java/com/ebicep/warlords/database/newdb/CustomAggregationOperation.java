package com.ebicep.warlords.database.newdb;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import javax.annotation.Nonnull;

public class CustomAggregationOperation implements AggregationOperation {

    private final String jsonOperation;

    public CustomAggregationOperation(String jsonOperation) {
        this.jsonOperation = jsonOperation;
    }

    @Nonnull
    @Override
    public org.bson.Document toDocument(AggregationOperationContext aggregationOperationContext) {
        return aggregationOperationContext.getMappedObject(org.bson.Document.parse(jsonOperation));
    }
}