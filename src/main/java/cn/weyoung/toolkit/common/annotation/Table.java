package cn.weyoung.toolkit.common.annotation;

import java.lang.annotation.*;

/**
 * 数据表注解
 *
 * @author sagachen created at 2017年6月19日 下午6:13:09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * 获取表名
     *
     * @return
     */
    String tableName() default "className";
}
