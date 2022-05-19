package com.github.jjjzzzqqq.mini.db.router.strategy;

/**
 * 路由策略接口<br>
 * 使用了策略模式<br>
 * 可拓展多个策略,通过配置文件确定具体的路由策略<br>
 */
public interface IDBRouterStrategy {

    /**
     * 路由计算
     *
     * @param dbKeyAttr 路由字段属性
     */
    void doRouter(String dbKeyAttr);

    /**
     * 手动设置分库路由
     *
     * @param dbIdx 路由库，需要在配置范围内
     */
    void setDBKey(int dbIdx);

    /**
     * 手动设置分表路由
     *
     * @param tbIdx 路由表，需要在配置范围内
     */
    void setTBKey(int tbIdx);

    /**
     * 获取分库数
     *
     * @return 数量
     */
    int dbCount();

    /**
     * 获取分表数
     *
     * @return 数量
     */
    int tbCount();

    /**
     * 清除路由
     */
    void clear();

}
