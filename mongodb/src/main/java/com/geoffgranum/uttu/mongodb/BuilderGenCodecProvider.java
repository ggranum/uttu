package com.geoffgranum.uttu.mongodb;

import com.geoffgranum.uttu.core.base.Buildable;
import com.geoffgranum.uttu.core.exception.FatalException;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author ggranum
 */
@Immutable
@ThreadSafe
public final class BuilderGenCodecProvider<T extends Buildable> implements CodecProvider {

  private final Class<T> buildableClass;
  private final Class<BuilderGenCodec<T>> codecClass;
  private final ToBuildableTransform<T> fromDocumentFn;
  private final ToDocumentTransform<T> toDocumentFn;

  public BuilderGenCodecProvider(Class<T> buildableClass,
                                 Class<BuilderGenCodec<T>> codecClass,
                                 ToBuildableTransform<T> fromDocumentFn,
                                 ToDocumentTransform<T> toDocumentFn
  ) {
    this.buildableClass = buildableClass;
    this.codecClass = codecClass;
    this.fromDocumentFn = fromDocumentFn;
    this.toDocumentFn = toDocumentFn;
  }

  /**
   * Sadly the MongoDB Java API developers haven't yet picked up on the fact that nulls are bad. This method is used
   * to look up Codecs for your Buildable classes. If the class provided as 'clazz' does not match the class
   * registered for this instance of BuilderGenCodecProvider then this method will return null.
   * @param clazz Can be any class that is retrieved or put into from a MongoDB MongoCollection via the typed
   *              getter/setter methods.
   * @param registry Provided by MongoDB invoker
   * @param <X> The actual class type.
   * @return A BuilderGenCodecProvider<T extends Buildable> class, if one is found that matches clazz, otherwise null.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <X> Codec<X> get(Class<X> clazz, CodecRegistry registry) {
    Codec<X> result = null;
    if (clazz == buildableClass) {
      try {
        Constructor<BuilderGenCodec<T>> ctor = codecClass.getConstructor(CodecRegistry.class,
          Class.class,
          ToBuildableTransform.class,
          ToDocumentTransform.class);
        result = (Codec<X>) ctor.newInstance(registry, buildableClass, fromDocumentFn, toDocumentFn);
      } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        throw new FatalException(e, "Could not construct Codec. ");
      }
    }
    return result;
  }
}


