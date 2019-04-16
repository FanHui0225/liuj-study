package com.stereo.study.rpc.core;

import com.stereo.study.rpc.api.IUpload;
import com.stereo.study.rpc.io.AbstractInput;
import com.stereo.study.rpc.io.factory.IOFactory;
import com.stereo.study.rpc.io.factory.SerializerFactory;
import com.stereo.study.rpc.io.AbstractOutput;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 核心处理骨架
 *
 * @author liujing
 */
public final class Skeleton extends AbstractSkeleton {

    private static final Logger log = Logger
            .getLogger(Skeleton.class.getName());

    private IOFactory _factory = new IOFactory();

    private Object _service;

    /**
     * @param service
     * @param apiClass
     */
    public Skeleton(Object service, Class<?> apiClass) {
        super(apiClass);
        if (service == null)
            service = this;
        _service = service;
        if (!apiClass.isAssignableFrom(service.getClass()))
            throw new IllegalArgumentException("Service " + service
                    + " must be an instance of " + apiClass.getName());
    }

    public Skeleton(Class<?> apiClass) {
        super(apiClass);
    }

    public void setFactory(IOFactory factory) {
        _factory = factory;
    }

    public void invoke(InputStream is, OutputStream os) throws Exception {
        invoke(is, os, null);
    }

    /**
     * @param is
     * @param os
     * @param serializerFactory
     * @throws Exception
     */
    public void invoke(InputStream is, OutputStream os,
                       SerializerFactory serializerFactory) throws Exception {
        /**
         * here can read Version
         */
        AbstractInput in = _factory.createRPCInput(is);
        AbstractOutput out = _factory.createRPCOutput(os);
        if (serializerFactory != null) {
            in.setSerializerFactory(serializerFactory);
            out.setSerializerFactory(serializerFactory);
        }
        try {
            invoke(_service, in, out);
        } finally {
            in.close();
            out.close();
        }
    }

    /**
     * @param in
     * @param out
     * @throws Exception
     */
    public void invoke(AbstractInput in, AbstractOutput out) throws Exception {
        invoke(_service, in, out);
    }

    /**
     * @param service
     * @param in
     * @param out
     * @throws Exception
     */
    public void invoke(Object service, AbstractInput in, AbstractOutput out)
            throws Exception {
        SkeletonContext context = SkeletonContext.getContext();
        in.readCall();

        // read header
        String header;
        while ((header = in.readHeader()) != null) {
            Object value = in.readObject();
            context.addHeader(header, value);
        }

        // read method and args len
        String methodName = in.readMethod();


        // check upload
        if (methodName.equals(IUpload.FILE_UPLOAD_AVAILABLE_METHOD)) {
            boolean allow = false;
            //read 多余的len
            in.readMethodArgLength();
            //read user passwd
            String user = in.readString();
            String passwd = in.readString();
            Object[] args = {user, passwd};
            Method method = getMethod(methodName + "__" + args.length);
            if (method != null)
                allow = (Boolean) method.invoke(service,args);
            if (allow)
                out.writeString(IUpload.FILE_UPLOAD_AUTH_COMPLETED);
            else
                out.writeString(IUpload.FILE_UPLOAD_AUTH_FAILED);
            in.close();
            out.close();
            return;
        }

        // handle upload
        if (methodName.equals(IUpload.FILE_UPLOAD_METHOD)) {
            Object[] args = {in, out};
            Method method = getMethod(methodName + "__" + args.length);
            if (method != null)
                method.invoke(service, args);
            out.close();
            return;
        }

        // handle packet
        // read argLength
        int argLength = in.readMethodArgLength();
        Method method = getMethod(methodName + "__" + argLength);
        if (method == null)
            method = getMethod(methodName);
        if (method != null) {
        } else if (method == null) {
            out.writeFault(
                    "NoSuchMethodException",
                    escapeMessage("The skeleton has no method named: "
                            + in.getMethod()), null);
            out.close();
            return;
        }

        Class<?>[] args = method.getParameterTypes();

        if (argLength != args.length && argLength >= 0) {
            out.writeFault("NoSuchMethod",
                    escapeMessage("method " + method
                            + " argument length mismatch, received length="
                            + argLength), null);
            out.close();
            return;
        }

        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = in.readObject(args[i]);
        }

        Object result = null;
        try {
            result = method.invoke(service, values);
        } catch (Exception e) {
            Throwable e1 = e;
            if (e1 instanceof InvocationTargetException)
                e1 = ((InvocationTargetException) e).getTargetException();
            log.log(Level.FINE, this + " " + e1.toString(), e1);
            out.writeFault("ServiceException", escapeMessage(e1.getMessage()),
                    e1);
            out.close();
            return;
        }
        in.completeCall();
        out.writeReply(result);
        out.close();
    }

    private String escapeMessage(String msg) {
        if (msg == null)
            return null;

        StringBuilder sb = new StringBuilder();

        int length = msg.length();
        for (int i = 0; i < length; i++) {
            char ch = msg.charAt(i);

            switch (ch) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case 0x0:
                    sb.append("&#00;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }
        return sb.toString();
    }
}