// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.appengine.api.datastore;

import static com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN;
import static com.google.appengine.api.datastore.Query.FilterOperator.LESS_THAN;
import static com.google.appengine.api.datastore.Query.FilterOperator.NOT_EQUAL;

import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.SortPredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class splits a query with a not-equal filter as follows:
 * <p>Create n + 1 queries components that restrict the queries
 * range to values outside the given value. Consider the example:
 * <pre>
 *   select from Person where age != 33
 * </pre>
 * this class would turn this into:
 * <pre>
 *   select from Person where age < 33
 *   select from Person where age > 33
 * <pre>
 *
 * <p>This class can also work for multiple inequality filters. For example:
 * <pre>
 *   select from Person where age != 33 and age != 40 order by age desc
 * </pre>
 * we would turn this into:
 * <pre>
 *   select from Person where age > 40
 *   select from Person where age > 33 and age < 40
 *   select from Person where age < 33
 * </pre>
 *
 * <p>Just like other inequality filters, != can only filters can only
 * be applied to a single property and the first sort order must
 * be on the same property.
 *
 */
class NotEqualQuerySplitter extends BaseQuerySplitter {
  @Override
  public List<QuerySplitComponent> split(List<FilterPredicate> remainingFilters,
                                         List<SortPredicate> sorts) {
    String propertyName = null;
    List<ComparableValue> values = null;

    Iterator<FilterPredicate> itr = remainingFilters.iterator();
    while (itr.hasNext()) {
      FilterPredicate filter = itr.next();
      if (filter.getOperator() == NOT_EQUAL) {
        if (propertyName == null) {
          propertyName = filter.getPropertyName();
          values = new ArrayList<ComparableValue>();
        } else if (!propertyName.equals(filter.getPropertyName())) {
          throw new IllegalArgumentException(
              "Queries with NOT_EQUAL filters on different properties are not supported.");
        }

        values.add(new ComparableValue(filter.getValue()));
        itr.remove();
      }
    }

    if (values != null) {
      ArrayList<QuerySplitComponent> result = new ArrayList<QuerySplitComponent>(values.size() + 1);
      result.add(makeComponent(propertyName, values, sorts));
      return result;
    }
    return Collections.emptyList();
  }

  /**
   * Constructs a {@link QuerySplitComponent} from the given set of not equal
   * filters.
   *
   * @param propertyName the property which != values
   * @param values the list of values to consider
   * @param sorts the sort predicates imposed on this query
   * @return the resulting {@link QuerySplitComponent}
   */
  private QuerySplitComponent makeComponent(String propertyName, List<ComparableValue> values,
                                            List<SortPredicate> sorts) {
    QuerySplitComponent result = new QuerySplitComponent(propertyName, sorts);
    if (!sorts.isEmpty() && (result.getSortIndex() != 0)) {
      throw new IllegalArgumentException(
          "The first sort order must be on the same property as the NOT_EQUAL filter");
    }

    Collections.sort(values, getValueComparator(result.getDirection()));
    Object first = values.get(0).getValue();
    Object last = values.get(values.size() - 1).getValue();
    if (result.getDirection() == SortDirection.DESCENDING) {
      result.addFilters(new FilterPredicate(propertyName, GREATER_THAN, first));
      for (int i = 1; i < values.size(); ++i) {
        Object prev = values.get(i - 1).getValue();
        Object next = values.get(i).getValue();
        result.addFilters(new FilterPredicate(propertyName, LESS_THAN, prev),
                          new FilterPredicate(propertyName, GREATER_THAN, next));
      }
      if (last != null) {
        result.addFilters(new FilterPredicate(propertyName, LESS_THAN, last));
      }
    } else {
      if (first != null) {
        result.addFilters(new FilterPredicate(propertyName, LESS_THAN, first));
      }
      for (int i = 1; i < values.size(); ++i) {
        Object prev = values.get(i - 1).getValue();
        Object next = values.get(i).getValue();
        result.addFilters(new FilterPredicate(propertyName, GREATER_THAN, prev),
                          new FilterPredicate(propertyName, LESS_THAN, next));
      }
      result.addFilters(new FilterPredicate(propertyName, GREATER_THAN, last));
    }
    return result;
  }
}
