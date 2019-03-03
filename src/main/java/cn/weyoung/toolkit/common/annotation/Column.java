package cn.weyoung.toolkit.common.annotation;

import java.lang.annotation.*;

/**
 * 数据库字段注解
 *
 * @author sagachen created at 2017年6月19日 下午6:13:41
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * 获取字段名
     *
     * @return
     */
    String columnName() default "fieldName";

    /**
     * 是否主键，默认false
     *
     * @return
     */
    boolean isPk() default false;
}
