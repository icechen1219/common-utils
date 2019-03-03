package cn.weyoung.toolkit.util;

import cn.weyoung.toolkit.common.annotation.Column;
import cn.weyoung.toolkit.common.annotation.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类，提供诸如自动映射实体类等方法
 *
 * @author Chen Bin
 * @date 2017年6月4日 下午12:24:35
 * @version 1.0
 */
public class ReflectionUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 映射实体类对象的注解版实现，不要求数据表字段名跟对象属性名一致
     *
     * @param entityClass
     * @param mapList
     * @return
     */
    public static <E> E convert2EntityByAnnotation(Class<E> entityClass, List<Map<String, Object>> mapList) {
        if (entityClass == null) {
            return null;
        }

        // 如果结果集有多余1行记录，则只返回第一行记录
        if (!mapList.isEmpty()) {
            return mappingObject(entityClass, mapList.get(0));
        }
        return null;
    }

    /**
     * 映射实体对象集合的注解版实现，不要求数据表字段名跟对象属性名一致
     *
     * @param entityClass
     * @param mapList
     * @return
     */
    public static <E> List<E> convert2EntityListByAnnotation(Class<E> entityClass, List<Map<String, Object>> mapList) {
        if (entityClass == null) {
            return Collections.emptyList();
        }

        List<E> list = new ArrayList<E>();
        for (Map<String, Object> resultMap : mapList) {
            E entity = mappingObject(entityClass, resultMap);
            if (entity != null) {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 获取getter方法
     *
     * @param entityClass
     * @param field
     * @return
     */
    public static Method guessGetMethod(Class<?> entityClass, Field field) {
        String prefix = "get";
        if (boolean.class.equals(field.getType()) || Boolean.class.equals(field.getType())) {
            prefix = "is";
        }
        String methodName = prefix + StringUtil.upperFirstChar(field.getName());

        try {
            return entityClass.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            methodName = prefix + field.getName();
            try {
                return entityClass.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException | SecurityException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
        } catch (SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取setter方法
     *
     * @param entityClass
     * @param field
     * @return
     */
    public static Method guessSetMethod(Class<?> entityClass, Field field) {
        String methodName = "set" + StringUtil.upperFirstChar(field.getName());

        try {
            return entityClass.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            methodName = "set" + field.getName();
            try {
                return entityClass.getDeclaredMethod(methodName, field.getType());
            } catch (NoSuchMethodException | SecurityException e1) {
                LOGGER.error(e.getMessage(), e);
            }
        } catch (SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据属性名和Column注解猜字段名
     *
     * @param prop
     * @param column
     * @return
     */
    public static String guessColumnName(Field prop, Column column) {
        if (column != null && !"fieldName".equals(column.columnName())) {
            return column.columnName();
        } else {
            // 如果注解Column没提供字段名，则默认为属性名
            return StringUtil.camelToUnderline(prop.getName());
        }
    }

    /**
     * 根据类名和Table注解猜数据库的表名
     *
     * @param entityClass
     * @param table
     * @return
     */
    public static String guessTableName(Class<?> entityClass, Table table) {
        if (table != null && !"className".equals(table.tableName())) {
            return table.tableName();
        } else {
            // 如果Table注解没提供表名，则表名默认为类名
            return StringUtil.camelToUnderline(entityClass.getSimpleName());
        }
    }

    /**
     * 通过反射机制，将数据库记录自动映射实体类对象<br>
     * 通过属性猜set方法的实现，并要求属性声明@Column注解
     *
     * @param entityClass
     * @param resultMap
     * @return
     */
    private static <E> E mappingObject(Class<E> entityClass, Map<String, Object> resultMap) {
        try {
            // 利用反射机制创建一个实体类的对象
            E entity = entityClass.newInstance();
            Field[] fields = entityClass.getDeclaredFields();
            /*------------封装实体类对象开始----------------*/
            for (Field prop : fields) {
                Column column = prop.getDeclaredAnnotation(Column.class);
                if (column == null) {
                    LOGGER.debug("警告：属性" + prop.getName() + "缺少@Column注解！");
                }
                Method setMethod = guessSetMethod(entityClass, prop);
                setMethod.setAccessible(true);
                String columnName = guessColumnName(prop, column);
                // FIXME: 这里偶尔会出现类型异常，目前出现过的就是把实体类的Integer查询出来变成Long了，然后再映射为实体时就出错
                setMethod.invoke(entity, resultMap.get(columnName));
            }
            /*------------封装实体类对象结束----------------*/
            return entity;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }


}
