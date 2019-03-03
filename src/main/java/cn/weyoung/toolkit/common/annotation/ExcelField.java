///:ExcelField.java
package cn.weyoung.toolkit.common.annotation;

import java.lang.annotation.*;

/**
 * @author icechen1219
 * @date 2019/01/13
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelField {

    String name() default "";

    String value() default "";

}
///:ExcelField.java
