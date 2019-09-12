///:ExcelField.java
package cn.weyoung.toolkit.common.annotation;

import java.lang.annotation.*;

/**
 * excel列注解。用于excel通用导入工具。
 *
 * @author icechen1219
 * @date 2019/01/13
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelField {

    /**
     * excel列标题
     *
     * @return
     */
    String name() default "";

    String value() default "";

}
///:ExcelField.java
