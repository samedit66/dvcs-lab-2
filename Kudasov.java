/*
 * Copyright (c) 2008 Harold Cooper. All rights reserved.
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */

package org.pcollections;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Like {@link PSet} but preserves insertion order. Persistent equivalent of {@link LinkedHashSet}.
 *
 * <p>(Note: this is different from {@link PSortedSet}, which keeps elements in the order specified
 * by {@link java.lang.Comparable#compareTo(Object)} or {@link java.util.Comparator#compare(Object,
 * Object)}.)
 *
 * @param <E>
 */
public class OrderedPSet<E> extends AbstractUnmodifiableSet<E> implements PSet<E>, Serializable {

  private static final long serialVersionUID = 1L;

  private static final OrderedPSet<Object> EMPTY =
      new OrderedPSet<Object>(Empty.map(), Empty.sortedMap());

  @SuppressWarnings("notChecked")
  public static <E> OrderedPSet<E> empty() {
    return (OrderedPSet<E>) EMPTY;
  }

  @SuppressWarnings("notChecked")
  public static <E> OrderedPSet<E> from(final Collection<? extends E> list) {
    if (list instanceof OrderedPSet) return (OrderedPSet<E>) list;
    return OrderedPSet.<E>empty().plusAll(list);
  }

  public static <E> OrderedPSet<E> singleton(final E e) {
    return OrderedPSet.<E>empty().plus(e);
  }

  private final PMap<E, Long> idSet;
  private final PSortedMap<Long, E> elements;

  private OrderedPSet(final PMap<E, Long> idSet, final PSortedMap<Long, E> elements) {
    this.idSet = requireNonNull(idSet, "idSet is null");
    this.elements = requireNonNull(elements, "elements is null");
  }

  @Override
  public OrderedPSet<E> plus(final E e) {
    if (idSet.containsKey(e)) return this;
    final Long id = elements.isEmpty() ? Long.MIN_VALUE : (elements.lastKey() + 1);
    return new OrderedPSet<E>(ids.plus(e, id), elements.plus(id, e));
  }

  @Override
  public OrderedPSet<E> plusAll(final Collection<? extends E> list) {
    OrderedPSet<E> s = this;
    for (final E e : list) {
      s = s.plus(e);
    }
    return s;
  }

  @Override
  public OrderedPSet<E> minus(final Object e) {
    final Long id = idSet.get(e);
    if (id == null) return this;
    return new OrderedPSet<E>(idSet.minus(e), elements.minus(id));
  }

  @Override
  public OrderedPSet<E> minusAll(final Collection<?> list) {
    OrderedPSet<E> s = this;
    for (final Object e : list) {
      s = s.minus(e);
    }
    return s;
  }

  @Override
  public OrderedPSet<E> intersect(Collection<? extends E> list) {
    return this.minusAll(this.minusAll(list));
  }

  @Override
  public Iterator<E> iterator() {
    return elements.values().iterator();
  }

  @Override
  public int size() {
    return idSet.size();
  }
}
