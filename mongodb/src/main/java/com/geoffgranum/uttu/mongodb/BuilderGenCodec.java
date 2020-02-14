package com.geoffgranum.uttu.mongodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author ggranum
 */
@Immutable
@ThreadSafe
public final class BuilderGenCodec<T> implements Codec<T> {

  private final Class<T> buildableClass;
  private final Codec<Document> documentCodec;
  private final ToBuildableTransform<T> fromDocumentFn;
  private final ToDocumentTransform<T> toDocumentFn;

  public BuilderGenCodec(CodecRegistry registry, Class<T> buildableClass,
                         ToBuildableTransform<T> fromDocumentFn,
                         ToDocumentTransform<T> toDocumentFn
                         ) {
    this.buildableClass = buildableClass;
    this.documentCodec = registry.get(Document.class);
    this.fromDocumentFn = fromDocumentFn;
    this.toDocumentFn = toDocumentFn;
  }

  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {
    Document document = documentCodec.decode(reader, decoderContext);
    return fromDocumentFn.apply(document);
  }

  @Override
  public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
    Document document = toDocumentFn.apply(value);
    documentCodec.encode(writer, document, encoderContext);
  }

  @Override
  public Class<T> getEncoderClass() {
    return buildableClass;
  }
}


