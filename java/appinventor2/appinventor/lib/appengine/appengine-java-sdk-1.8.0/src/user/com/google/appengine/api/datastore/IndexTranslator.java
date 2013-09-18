// Copyright 2013 Google Inc. All Rights Reserved.
package com.google.appengine.api.datastore;

import com.google.apphosting.api.AppEngineInternal;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.storage.onestore.v3.OnestoreEntity;

import java.util.List;

/**
 * Helper class to translate between {@link Index} to {@link OnestoreEntity.Index}.
 *
 */
@AppEngineInternal
public class IndexTranslator {

  public static OnestoreEntity.Index convertToPb(Index index) {
    OnestoreEntity.Index value = new OnestoreEntity.Index();
    value.setEntityType(index.getKind());
    value.setAncestor(index.isAncestor());
    for (Index.Property property : index.getProperties()) {
      value.mutablePropertys().add(convertToPb(property));
    }
    return value;
  }

  public static OnestoreEntity.Index.Property convertToPb(Index.Property property) {
    OnestoreEntity.Index.Property value = new OnestoreEntity.Index.Property();
    value.setName(property.getName());
    value.setDirection(
        OnestoreEntity.Index.Property.Direction.valueOf(property.getDirection().name()));
    return value;
  }

  public static Index convertFromPb(OnestoreEntity.CompositeIndex ci) {
    OnestoreEntity.Index index = ci.getDefinition();
    List<Index.Property> properties = ImmutableList.copyOf(Iterables.transform(index.propertys(),
        new Function<OnestoreEntity.Index.Property, Index.Property>() {
          @Override public Index.Property apply(OnestoreEntity.Index.Property property) {
            return convertFromPb(property);
          }
        }));
    return new Index(ci.getId(), index.getEntityType(), index.isAncestor(), properties);
  }

  public static Index.Property convertFromPb(OnestoreEntity.Index.Property property) {
    return new Index.Property(
        property.getName(), Query.SortDirection.valueOf(property.getDirectionEnum().name()));
  }

  public static Index convertFromPb(OnestoreEntity.Index index) {
    return convertFromPb(new OnestoreEntity.CompositeIndex().setId(0).setDefinition(index));
  }
}
