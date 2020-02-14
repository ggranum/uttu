package com.geoffgranum.uttu.mongodb;

import org.bson.Document;

import java.util.function.Function;


/**
 * Convert a Buildable instance into a MongoDb Document type.
 *
 * @todo Determine if it's possible to create an interface that's compatible with both Azure Cosmos,
 * Amazon DocumentDB and Mongo.
 * If not, we'll need an adapter pattern, because writing n different libraries to handle API compatible services
 * is stupid.
 * @author ggranum
 */
public interface ToDocumentTransform<F> extends Function<F, Document> {

}
