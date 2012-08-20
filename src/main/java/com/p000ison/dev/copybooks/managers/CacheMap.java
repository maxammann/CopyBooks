/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

package com.p000ison.dev.copybooks.managers;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a Cache
 * This class stores books in memory so we don't need to load them soo often.
 */
public class CacheMap<K, V> extends LinkedHashMap<K, V> {
    private final int MAX_SIZE;

    /**
     * Creates a cache to store data.
     *
     * @param maxSize The maximal size this cache can become.
     */
    public CacheMap(int maxSize)
    {
        super(maxSize + 1, 0.75F, true);
        MAX_SIZE = maxSize;
    }

    /**
     * We override this here to set a maximum size
     *
     * @see #removeEldestEntry(java.util.Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest)
    {
        return size() > MAX_SIZE;
    }

//    /**
//     * Stuff to test this class :P
//     *
//     * @param args The arguments
//     */
//    public static void main(String[] args)
//    {
//        CacheMap<Long, Integer> cache = new
//                CacheMap<Long, Integer>(50);
//
//        for (long i = 0; i < 500; i++) {
//            cache.put(i, new Random().nextInt(599));
//        }
//
//        for (Map.Entry entry : cache.entrySet()) {
//            System.out.println(entry.getKey() + "   " + entry.getValue());
//        }
//    }
}
