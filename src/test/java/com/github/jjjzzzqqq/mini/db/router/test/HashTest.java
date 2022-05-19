package com.github.jjjzzzqqq.mini.db.router.test;

import org.junit.Test;

public class HashTest {

    @Test
    public void test_db_hash() {
        String key = "jjjzzzqqq";

        int dbCount = 2, tbCount = 4;
        int size = dbCount * tbCount;
        // 散列
        int idx = (size - 1) & (key.hashCode() ^ (key.hashCode() >>> 16));
        System.out.println("idx = " + idx );

        int dbIdx = idx / tbCount + 1;
        int tbIdx = idx - tbCount * (dbIdx - 1);

        System.out.println(dbIdx);
        System.out.println(tbIdx);

        System.out.println(String.format("%02d", dbIdx));
        System.out.println(String.format("%03d", tbIdx));
    }

}
