package com.mrgrd56.javalearningspringboot.services;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectionsService {
    public void start() {
        var set2 = new TreeSet<Integer>(Comparator.comparingInt(Math::abs));
        set2.add(123);
        set2.add(1);
        set2.add(-24);
        set2.add(22);
        set2.add(24);
        set2.add(20);

        var map2 = new TreeMap<Integer, Object>(Comparator.comparingInt(value -> (int) value).reversed());
        map2.put(12, "hello");
        map2.put(-13, "world");
        map2.put(-16, "is");
        map2.put(66, "mine");
        map2.put(0, "craft");

        var list = List.of(
                new Entry(42, 1, 12),
                new Entry(43, 2, 23),
                new Entry(45, 2, 34),
                new Entry(47, 1, 45),
                new Entry(42, 3, 56),
                new Entry(48, 2, 67)
        );

        var result = list.stream()
                .collect(Collectors.groupingBy(Entry::category))
                .entrySet()
                .stream()
                .map(entry -> {
                    return new Entry(entry.getKey(), null, entry.getValue().stream().mapToInt(Entry::count).sum(), entry.getValue());
                })
                .toList();
    }

    record Entry(
            Integer key,
            Integer category,
            Integer count,
            List<Entry> items
    ) {
        Entry(Integer key, Integer category, Integer count, List<Entry> items) {
            this.key = key;
            this.category = category;
            this.count = count;
            this.items = items;
        }

        public Entry(Integer key, Integer category, Integer count) {
            this(key, category, count, List.of());
        }
    }
}
