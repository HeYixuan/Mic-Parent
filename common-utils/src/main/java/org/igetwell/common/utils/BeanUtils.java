package org.igetwell.common.utils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.lang.reflect.Field;
import java.util.*;

public class BeanUtils {

    /**
     * 将bean按照@XStreamAlias标识的字符串内容生成以之为key的map对象
     *
     * @param bean 包含@XStreamAlias的xml bean对象
     * @return map对象
     */
    public static Map<String, String> xmlBean2Map(Object bean) {
        Map<String, String> result = new HashMap<>();
        List<Field> fields = new ArrayList<>(Arrays.asList(bean.getClass().getDeclaredFields()));
        fields.addAll(Arrays.asList(bean.getClass().getSuperclass().getDeclaredFields()));
        for (Field field : fields) {
            try {
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                if (field.get(bean) == null) {
                    field.setAccessible(isAccessible);
                    continue;
                }
                if (field.isAnnotationPresent(XStreamAlias.class)) {
                    result.put(field.getAnnotation(XStreamAlias.class).value(), field.get(bean).toString());
                }
                field.setAccessible(isAccessible);
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return result;
    }
}
