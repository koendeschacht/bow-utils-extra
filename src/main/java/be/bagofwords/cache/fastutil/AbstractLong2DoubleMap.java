package be.bagofwords.cache.fastutil;

/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* Primitive-type-only definitions (values) */
/*
 * Copyright (C) 2002-2014 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by Koen Deschacht (koendeschacht@gmail.com), 2014-11-11
 */

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.longs.AbstractLongIterator;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Iterator;
import java.util.Map;

/**
 * An abstract class providing basic methods for maps implementing a type-specific interface.
 * <p>Optional operations just throw an {@link
 * UnsupportedOperationException}. Generic versions of accessors delegate to
 * the corresponding type-specific counterparts following the interface rules
 * (they take care of returning <code>null</code> on a missing key).
 * <p>As a further help, this class provides a {@link BasicEntry BasicEntry} inner class
 * that implements a type-specific version of {@link java.util.Map.Entry}; it
 * is particularly useful for those classes that do not implement their own
 * entries (e.g., most immutable maps).
 */
public abstract class AbstractLong2DoubleMap implements Long2DoubleMap, java.io.Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2DoubleMap() {
    }


    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * This class provides a basic but complete type-specific entry class for all those maps implementations
     * that do not have entries on their own (e.g., most immutable maps).
     * <p>This class does not implement {@link java.util.Map.Entry#setValue(Object) setValue()}, as the modification
     * would not be reflected in the base map.
     */
    public static class BasicEntry implements Long2DoubleMap.Entry<Double> {
        protected long key;
        protected double value;

        public BasicEntry(final Long key, final Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(final long key, final double value) {
            this.key = key;
            this.value = value;
        }

        public long getKey() {
            return key;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(final Double value) {
            this.value = value;
        }

        public boolean equals(final Object o) {
            if (!(o instanceof Entry)) return false;
            Entry<?> e = (Entry<?>) o;
            return key == e.getKey() && ((value) == ((((Double) (e.getValue())))));
        }

        public int hashCode() {
            return it.unimi.dsi.fastutil.HashCommon.long2int(key) ^ it.unimi.dsi.fastutil.HashCommon.double2int(value);
        }

        public String toString() {
            return key + "->" + value;
        }
    }

    /**
     * Returns a type-specific-set view of the keys of this map.
     * <p>The view is backed by the set returned by {@link #entrySet()}. Note that
     * <em>no attempt is made at caching the result of this method</em>, as this would
     * require adding some attributes that lightweight implementations would
     * not need. Subclasses may easily override this policy by calling
     * this method and caching the result, but implementors are encouraged to
     * write more efficient ad-hoc implementations.
     *
     * @return a set view of the keys of this map; it may be safely cast to a type-specific interface.
     */
    public LongSet keySet() {
        return new AbstractLongSet() {
            public boolean contains(final long k) {
                return containsKey(k);
            }

            public int size() {
                return AbstractLong2DoubleMap.this.size();
            }

            public void clear() {
                AbstractLong2DoubleMap.this.clear();
            }

            public LongIterator iterator() {
                return new AbstractLongIterator() {
                    final ObjectIterator<Entry<Double>> i = entrySet().iterator();

                    public long nextLong() {
                        return ((Long2DoubleMap.Entry) i.next()).getKey();
                    }

                    ;

                    public boolean hasNext() {
                        return i.hasNext();
                    }
                };
            }
        };
    }

    /**
     * Returns a type-specific-set view of the values of this map.
     * <p>The view is backed by the set returned by {@link #entrySet()}. Note that
     * <em>no attempt is made at caching the result of this method</em>, as this would
     * require adding some attributes that lightweight implementations would
     * not need. Subclasses may easily override this policy by calling
     * this method and caching the result, but implementors are encouraged to
     * write more efficient ad-hoc implementations.
     *
     * @return a set view of the values of this map; it may be safely cast to a type-specific interface.
     */
    public DoubleCollection values() {
        return new AbstractDoubleCollection() {
            public boolean contains(final double k) {
                return containsValue(k);
            }

            public int size() {
                return AbstractLong2DoubleMap.this.size();
            }

            public void clear() {
                AbstractLong2DoubleMap.this.clear();
            }

            public DoubleIterator iterator() {
                return new AbstractDoubleIterator() {
                    final ObjectIterator<Entry<Double>> i = entrySet().iterator();

                    public double nextDouble() {
                        return i.next().getValue();
                    }

                    public boolean hasNext() {
                        return i.hasNext();
                    }
                };
            }
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ObjectSet<Entry<Double>> entrySet() {
        return (ObjectSet) long2DoubleEntrySet();
    }

    /**
     * Returns a hash code for this map.
     * The hash code of a map is computed by summing the hash codes of its entries.
     *
     * @return a hash code for this map.
     */
    public int hashCode() {
        int h = 0, n = size();
        final ObjectIterator<? extends Entry<Double>> i = entrySet().iterator();
        while (n-- != 0) h += i.next().hashCode();
        return h;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Map)) return false;
        Map<?, ?> m = (Map<?, ?>) o;
        if (m.size() != size()) return false;
        return entrySet().containsAll(m.entrySet());
    }

}
