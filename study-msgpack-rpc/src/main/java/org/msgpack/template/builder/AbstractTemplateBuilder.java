//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.template.builder;

import org.msgpack.annotation.*;
import org.msgpack.template.FieldList;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTemplateBuilder implements TemplateBuilder {

    protected TemplateRegistry registry;

    protected AbstractTemplateBuilder(TemplateRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <T> Template<T> buildTemplate(final Type targetType)
            throws TemplateBuildException {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) targetType;
        checkClassValidation(targetClass);
        FieldOption fieldOption = getFieldOption(targetClass);
        FieldEntry[] entries = toFieldEntries(targetClass, fieldOption);
        return buildTemplate(targetClass, entries);
    }

    @Override
    public <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldList fieldList)
            throws TemplateBuildException {
        checkClassValidation(targetClass);
        FieldEntry[] entries = toFieldEntries(targetClass, fieldList);
        return buildTemplate(targetClass, entries);
    }

    protected abstract <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries);

    protected void checkClassValidation(final Class<?> targetClass) {
        if (targetClass.isInterface()) {
            throw new TemplateBuildException(
                    "Cannot build template for interface: " + targetClass.getName());
        }
        if (Modifier.isAbstract(targetClass.getModifiers())) {
            throw new TemplateBuildException(
                    "Cannot build template for abstract class: " + targetClass.getName());
        }
        if (targetClass.isArray()) {
            throw new TemplateBuildException(
                    "Cannot build template for array class: " + targetClass.getName());
        }
        if (targetClass.isPrimitive()) {
            throw new TemplateBuildException(
                    "Cannot build template of primitive type: " + targetClass.getName());
        }
    }

    protected FieldOption getFieldOption(Class<?> targetClass) {
        Message m = targetClass.getAnnotation(Message.class);
        if (m == null) {
            return FieldOption.DEFAULT;
        }
        MessagePackMessage mpm = targetClass
                .getAnnotation(MessagePackMessage.class);
        if (mpm == null) {
            return FieldOption.DEFAULT;
        }
        // TODO #MN
        return m.value();
    }

    private FieldEntry[] toFieldEntries(final Class<?> targetClass, final FieldList flist) {
        List<FieldList.Entry> src = flist.getList();
        FieldEntry[] entries = new FieldEntry[src.size()];
        for (int i = 0; i < src.size(); i++) {
            FieldList.Entry s = src.get(i);
            if (s.isAvailable()) {
                try {
                    entries[i] = new DefaultFieldEntry(targetClass.getDeclaredField(s.getName()), s.getOption());
                } catch (SecurityException e) {
                    throw new TemplateBuildException(e);
                } catch (NoSuchFieldException e) {
                    throw new TemplateBuildException(e);
                }
            } else {
                entries[i] = new DefaultFieldEntry();
            }
        }
        return entries;
    }

    protected FieldEntry[] toFieldEntries(final Class<?> targetClass, final FieldOption from) {
        Field[] fields = getFields(targetClass);

        /*
         * index:
         * 
         * @Index(0)
         * int field_a; // 0
         * int field_b; // 1
         * @Index(3)
         * int field_c; // 3
         * int field_d; // 4
         * @Index(2)
         * int field_e; // 2
         * int field_f; // 5
         */
        List<FieldEntry> indexed = new ArrayList<FieldEntry>();
        int maxIndex = -1;
        for (Field f : fields) {
            FieldOption opt = getFieldOption(f, from);
            if (opt == FieldOption.IGNORE) {
                // skip
                continue;
            }

            int index = getFieldIndex(f, maxIndex);
            if (indexed.size() > index && indexed.get(index) != null) {
                throw new TemplateBuildException("duplicated index: " + index);
            }
            if (index < 0) {
                throw new TemplateBuildException("invalid index: " + index);
            }

            while (indexed.size() <= index) {
                indexed.add(null);
            }
            indexed.set(index, new DefaultFieldEntry(f, opt));

            if (maxIndex < index) {
                maxIndex = index;
            }
        }

        FieldEntry[] entries = new FieldEntry[maxIndex + 1];
        for (int i = 0; i < indexed.size(); i++) {
            FieldEntry e = indexed.get(i);
            if (e == null) {
                entries[i] = new DefaultFieldEntry();
            } else {
                entries[i] = e;
            }
        }
        return entries;
    }

    private Field[] getFields(Class<?> targetClass) {
        // order: [fields of super class, ..., fields of this class]
        List<Field[]> succ = new ArrayList<Field[]>();
        int total = 0;
        for (Class<?> c = targetClass; c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            total += fields.length;
            succ.add(fields);
        }
        Field[] result = new Field[total];
        int off = 0;
        for (int i = succ.size() - 1; i >= 0; i--) {
            Field[] fields = succ.get(i);
            System.arraycopy(fields, 0, result, off, fields.length);
            off += fields.length;
        }
        return result;
    }

    private FieldOption getFieldOption(Field field, FieldOption from) {
        int mod = field.getModifiers();
        // default mode:
        // transient, static, final : Ignore
        // primitive type : NotNullable
        // reference type : Ignore
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)
                || Modifier.isTransient(mod)) {
            return FieldOption.IGNORE;
        }

        if (isAnnotated(field, Ignore.class)) {
            return FieldOption.IGNORE;
        } else if (isAnnotated(field, Optional.class)) {
            return FieldOption.OPTIONAL;
        } else if (isAnnotated(field, NotNullable.class)) {
            return FieldOption.NOTNULLABLE;
        }

        if (from != FieldOption.DEFAULT) {
            return from;
        }

        if (field.getType().isPrimitive()) {
            return FieldOption.NOTNULLABLE;
        } else {
            return FieldOption.OPTIONAL;
        }
    }

    private int getFieldIndex(final Field field, int maxIndex) {
        Index a = field.getAnnotation(Index.class);
        if (a == null) {
            return maxIndex + 1;
        } else {
            return a.value();
        }
    }

    @Override
    public void writeTemplate(Type targetType, String directoryName) {
        throw new UnsupportedOperationException(targetType.toString());
    }

    @Override
    public <T> Template<T> loadTemplate(Type targetType) {
        return null;
    }

    public static boolean isAnnotated(Class<?> targetClass,
            Class<? extends Annotation> with) {
        if(targetClass.getAnnotation(with) != null)
            return true;

        Class<?> superClass = targetClass;
        while (superClass!=null)
        {
            if (superClass.getAnnotation(with)!=null)
                return true;
            else
                superClass = superClass.getSuperclass();
        }

        Class<?>[] interfaces = targetClass.getInterfaces();
        return isInterfaceHasAnnotated(interfaces,with);
    }

    public static boolean isInterfaceHasAnnotated(Class<?>[] interfaces,
                                      Class<? extends Annotation> with){
        if(interfaces!=null && interfaces.length > 0)
        {
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> inter = interfaces[i];
                if (inter.getAnnotation(with)!=null || isInterfaceHasAnnotated(inter.getInterfaces(),with))
                    return true;
            }
            return false;
        }else {
            return false;
        }
    }

    public static boolean isAnnotated(AccessibleObject accessibleObject, Class<? extends Annotation> with) {
        return accessibleObject.getAnnotation(with) != null;
    }

    public static boolean matchAtClassTemplateBuilder(Class<?> targetClass, boolean hasAnnotation) {
        if (hasAnnotation) {
            return isAnnotated(targetClass, Message.class)
                    || isAnnotated(targetClass, MessagePackMessage.class);
        } else {
            return !targetClass.isEnum() && !targetClass.isInterface();
        }
    }

    public static boolean matchAtBeansClassTemplateBuilder(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        if (hasAnnotation) {
            return isAnnotated((Class<?>) targetType, Beans.class)
                    || isAnnotated((Class<?>) targetType, MessagePackBeans.class);
        } else {
            return !targetClass.isEnum() || !targetClass.isInterface();
        }
    }

    public static boolean matchAtArrayTemplateBuilder(Class<?> targetClass, boolean hasAnnotation) {
        if (((Type) targetClass) instanceof GenericArrayType) {
            return true;
        }
        return targetClass.isArray();
    }

    public static boolean matchAtOrdinalEnumTemplateBuilder(Class<?> targetClass, boolean hasAnnotation) {
        if (hasAnnotation) {
            return isAnnotated(targetClass, OrdinalEnum.class)
                    || isAnnotated(targetClass, MessagePackOrdinalEnum.class);
        } else {
            return targetClass.isEnum();
        }
    }
}
