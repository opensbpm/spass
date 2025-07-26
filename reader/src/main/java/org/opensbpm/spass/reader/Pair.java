package org.opensbpm.spass.reader;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

interface Pair<K, V> {
    static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<K, V>() {
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }
        };
    }

     static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Pair::getKey, Pair::getValue);
    }

    K getKey();

    V getValue();
}
