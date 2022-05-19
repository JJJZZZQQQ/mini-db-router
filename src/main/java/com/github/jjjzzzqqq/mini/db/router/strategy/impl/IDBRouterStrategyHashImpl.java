package com.github.jjjzzzqqq.mini.db.router.strategy.impl;

import com.github.jjjzzzqqq.mini.db.router.DBContext;
import com.github.jjjzzzqqq.mini.db.router.DBRouterConfig;
import com.github.jjjzzzqqq.mini.db.router.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDBRouterStrategyHashImpl implements IDBRouterStrategy {

    private Logger logger = LoggerFactory.getLogger(IDBRouterStrategyHashImpl.class);

    private DBRouterConfig dbRouterConfig;

    public IDBRouterStrategyHashImpl(DBRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    @Override
    public void doRouter(String dbKeyAttr) {
        int dbCount = dbRouterConfig.getDbCount();
        int tbCount = dbRouterConfig.getTbCount();
        int size = dbCount * tbCount;

        //扰动函数,低16位和高16位进行异或运算,使Hash结果更加均匀
        int hash = dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16);
        int index = (size - 1) & hash;

        int dbIndex = index / tbCount + 1;
        int tbIndex = index - tbCount * (dbIndex - 1);

        // 设置到该请求的数据库路由上下文对象中
        // 便于后续Spring获取数据源的时候通过数据源上下文对象获取
        DBContext.setDBKey(String.valueOf(dbIndex));
        DBContext.setTBKey(String.valueOf(tbIndex));

    }

    @Override
    public void setDBKey(int dbIdx) {
        DBContext.setDBKey(String.valueOf(dbIdx));
    }

    @Override
    public void setTBKey(int tbIdx) {
        DBContext.setTBKey(String.valueOf(tbIdx));
    }

    @Override
    public int dbCount() {
        return dbRouterConfig.getDbCount();
    }

    @Override
    public int tbCount() {
        return dbRouterConfig.getTbCount();
    }

    @Override
    public void clear() {
        DBContext.clearDBKey();
        DBContext.clearTBKey();
    }
}
