package com.gjermundbjaanes.apps.roommates2.helpers;

import com.parse.ParseQuery;

public class Utils {
    private Utils() {}

    public static void setSafeQueryCaching(ParseQuery query) {
        if (query.hasCachedResult()) {
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        } else {
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        }

    }
}
