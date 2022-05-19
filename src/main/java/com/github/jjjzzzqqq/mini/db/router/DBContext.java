package com.github.jjjzzzqqq.mini.db.router;

public class DBContext {

    /**
     * 为什么ThreadLocal变量是静态的？
     * 对于多个线程来说。虽然Key都是这个静态的ThreadLocal
     * 但是由于不同线程对应的ThreadLocalMap不同，所以还是线程隔离的
     */
    private static final ThreadLocal<String> dbKey = new ThreadLocal<String>();
    private static final ThreadLocal<String> tbKey = new ThreadLocal<String>();

    public static void setDBKey(String dbKeyIdx){
        dbKey.set(dbKeyIdx);
    }

    public static String getDBKey(){
        return dbKey.get();
    }

    public static void setTBKey(String tbKeyIdx){
        tbKey.set(tbKeyIdx);
    }

    public static String getTBKey(){
        return tbKey.get();
    }

    public static void clearDBKey(){
        dbKey.remove();
    }

    public static void clearTBKey(){
        tbKey.remove();
    }
}
