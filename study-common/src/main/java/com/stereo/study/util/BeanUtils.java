package com.stereo.study.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.CollectionUtils;
import sun.misc.Unsafe;

/**
 * Created by liuj-ai on 2018/10/24.
 */
public class BeanUtils {

    private static final Unsafe _unsafe;

    static {
        Unsafe unsafe = null;
        try {
            Class<?> _unsafe_class = Class.forName("sun.misc.Unsafe");
            Field[] fields = _unsafe_class.getDeclaredFields();
            Field theUnsafe = null;
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    theUnsafe = fields[i];
                }
            }
            if (theUnsafe != null) {
                theUnsafe.setAccessible(true);
                unsafe = (Unsafe) theUnsafe.get(null);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        _unsafe = unsafe;
    }

    public static <O, D> D copyToClassInstance(O orig,
        Class<D> destCls) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (orig != null) {
            D dest = destCls.newInstance();
            org.apache.commons.beanutils.BeanUtils.copyProperties(dest, orig);
            return dest;
        } else
            return null;
    }

    public static <O, D> List<D> copyToClassInstance(List<O> origList,
        Class<D> destCls) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (CollectionUtils.isEmpty(origList)) {
            return null;
        } else {
            List<D> destList = new ArrayList<>();
            for (O orig : origList) {
                destList.add(copyToClassInstance(orig, destCls));
            }
            return destList;
        }
    }

    public static void setFieldValue(Object object, Field field, Object value) {
        long _offset = _unsafe.objectFieldOffset(field);
        if (_offset == Unsafe.INVALID_FIELD_OFFSET)
            throw new IllegalStateException();
        _unsafe.putObject(object, _offset, value);
    }

    /**
     * set对象属性
     *
     * @param object
     * @param fieldname
     * @param value
     * @throws Exception
     */
    public static void setFieldValue(Object object, String fieldname, Object value)
        throws Exception {
        setFieldValue(object, getDeclaredField(object, fieldname), value);
    }

    /**
     * 获取对象属性
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        return getDeclaredField(object.getClass(), fieldName);
    }

    /**
     * 获取对象属性
     *
     * @param cls
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Class<?> cls, String fieldName) {
        Field field = null;
        Class<?> clazz = cls;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * get对象属性值
     *
     * @param field
     * @param object
     * @return
     */
    protected Object getFieldValue(Field field, Object object) {
        long _offset = _unsafe.objectFieldOffset(field);
        if (_offset == Unsafe.INVALID_FIELD_OFFSET)
            throw new IllegalStateException();
        return _unsafe.getObject(object, _offset);
    }

    /**
     * get对象属性值
     *
     * @param fieldname
     * @param object
     * @return
     * @throws Exception
     */
    protected Object getFieldValue(String fieldname, Object object)
        throws Exception {
        return getFieldValue(getDeclaredField(object, fieldname), object);
    }
}
