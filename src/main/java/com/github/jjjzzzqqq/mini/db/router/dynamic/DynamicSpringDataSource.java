package com.github.jjjzzzqqq.mini.db.router.dynamic;

import com.github.jjjzzzqqq.mini.db.router.DBContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Spring提供的动态数据路由功能
 * <br><br>
 * 通过继承AbstractRoutingDataSource，并重写determineCurrentLookupKey方法
 *  <br><br>
 * Spring会根据返回的结果再所有注册的数据源Map中寻找对应的数据源，并使用对应的数据源获取数据库连接
 *  <br><br>
 */
public class DynamicSpringDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return "db" + DBContext.getDBKey();
    }
}
