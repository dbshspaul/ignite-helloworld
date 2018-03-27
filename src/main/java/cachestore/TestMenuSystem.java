package cachestore;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;

import java.util.Scanner;

public class TestMenuSystem {
    public static void menuSystem(IgniteCache cache) {
        while (true) {
            System.out.println("1. All data");
            System.out.println("2. Load data");
            System.out.println("Enter menu choice");
            Scanner sc = new Scanner(System.in);
            switch (sc.next()) {
                case "1":
                    System.out.println(">>>>>>>>>>>>>>>>>>>Start<<<<<<<<<<<<<<<<<<<");
                    try (QueryCursor cursor = cache.query(new ScanQuery((k, p) -> true))) {
                        for (Object p : cursor) {
                            System.out.println("String data======>" + p.toString());
                        }
                    }
                    System.out.println(">>>>>>>>>>>>>>>>>>>End<<<<<<<<<<<<<<<<<<<");
                    break;
                case "2":
                    System.out.println("enter key");
                    String key = sc.next();
                    System.out.println("enter value");
                    String value = sc.next();
                    cache.put(key, value);
                    break;
                case "3":
                    break;
                default:
                    continue;
            }
        }
    }
}
