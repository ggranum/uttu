package com.geoffgranum.uttu.mongodb;

import com.geoffgranum.uttu.core.persistence.id.IdGenerator;
import com.geoffgranum.uttu.core.persistence.id.TypedId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

/**
 * @author ggranum
 */
@SuppressWarnings("rawtypes")
public class TypedIdCodec implements Codec<TypedId> {

  private final IdGenerator idGen;

  public TypedIdCodec(IdGenerator idGen) {
    this.idGen = idGen;
  }

  @Override
  public void encode(final BsonWriter writer, final TypedId id, final EncoderContext encoderContext) {
    writer.writeObjectId(new ObjectId(idGen.toBytes(id)));
  }

  @Override
  public TypedId decode(final BsonReader reader, final DecoderContext decoderContext) {
    return new TypedId(reader.readObjectId().toHexString());
  }

  @Override
  public Class<TypedId> getEncoderClass() {
    return TypedId.class;
  }
}
