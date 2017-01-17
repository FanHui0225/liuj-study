package com.stereo.via.rpc.server;


import com.stereo.via.rpc.api.IActionContext;
import com.stereo.via.rpc.api.IService;
import com.stereo.via.rpc.api.IUpload;
import com.stereo.via.rpc.core.Skeleton;
import com.stereo.via.rpc.core.SkeletonContext;
import com.stereo.via.rpc.core.context.ActionContext;
import com.stereo.via.rpc.io.AbstractInput;
import com.stereo.via.rpc.io.AbstractOutput;
import com.stereo.via.rpc.io.factory.SerializerFactory;
import com.stereo.via.rpc.core.IHandler;
import com.stereo.via.rpc.transport.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * 基于RPC二进制传输Http骨架
 *
 * @author liujing
 */
public abstract class HttpSkeleton extends HttpServlet implements
        IHandler<Packet> {

    /**
     *
     */
    private static final long serialVersionUID = -7957852503130583912L;

    private static final Logger log = LoggerFactory.getLogger(HttpSkeleton.class);

    private Class<?> _homeAPI;
    private Object _homeImpl;

    private Class<?> _objectAPI;
    private Object _objectImpl;

    private Skeleton _homeSkeleton;
    private Skeleton _objectSkeleton;

    private SerializerFactory _serializerFactory;

    protected IActionContext actionContext;

    public HttpSkeleton() {
    }

    public String getServletInfo() {
        return "RPC Servlet";
    }

    public void setHomeAPI(Class<?> api) {
        _homeAPI = api;
    }

    public void setHome(Object home) {
        _homeImpl = home;
    }

    public void setObjectAPI(Class<?> api) {
        _objectAPI = api;
    }

    public void setObject(Object object) {
        _objectImpl = object;
    }

    public void setService(Object service) {
        setHome(service);
    }

    public void setAPIClass(Class<?> api) {
        setHomeAPI(api);
    }

    public Class<?> getAPIClass() {
        return _homeAPI;
    }

    public void setSerializerFactory(SerializerFactory factory) {
        _serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        if (_serializerFactory == null)
            _serializerFactory = new SerializerFactory();
        return _serializerFactory;
    }

    public void setSendCollectionType(boolean sendType) {
        getSerializerFactory().setSendCollectionType(sendType);
    }

    public void setLogName(String name) {
        // _log = Logger.getLogger(name);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try
        {
            actionContext = ActionContext.getInstance();
            // 服务端共享接口实现类
            if (_homeImpl != null) {
            } else if (getInitParameter("home-class") != null) {
                String className = getInitParameter("home-class");

                Class<?> homeClass = loadClass(className);

                _homeImpl = homeClass.newInstance();
                System.out.println();
                init(_homeImpl);
            } else if (getInitParameter("skeleton-class") != null) {
                String className = getInitParameter("skeleton-class");

                Class<?> homeClass = loadClass(className);

                _homeImpl = homeClass.newInstance();

                init(_homeImpl);
            } else {
                if (getClass().equals(HttpSkeleton.class))
                    throw new ServletException(
                            "server must extend RPCHttpServer");
                _homeImpl = this;
            }
            if (_homeAPI != null) {
            } else if (getInitParameter("home-api") != null) {
                String className = getInitParameter("home-api");
                _homeAPI = loadClass(className);
            } else if (getInitParameter("api-class") != null) {
                String className = getInitParameter("api-class");

                _homeAPI = loadClass(className);
            } else if (_homeImpl != null) {
                _homeAPI = findRemoteAPI(_homeImpl.getClass());

                if (_homeAPI == null)
                    _homeAPI = _homeImpl.getClass();

                _homeAPI = _homeImpl.getClass();
            }

            // 其他服务接口实现类
            if (_objectImpl != null) {
            } else if (getInitParameter("object-class") != null) {
                String className = getInitParameter("object-class");

                Class<?> objectClass = loadClass(className);
                _objectImpl = objectClass.newInstance();
                init(_objectImpl);
            }
            if (_objectAPI != null) {
            } else if (getInitParameter("object-api") != null) {
                String className = getInitParameter("object-api");

                _objectAPI = loadClass(className);
            } else if (_objectImpl != null)
                _objectAPI = _objectImpl.getClass();

            // 服务端代理
            _homeSkeleton = new Skeleton(_homeImpl, _homeAPI);
            if (_objectAPI != null)
                _homeSkeleton.setObjectClass(_objectAPI);

            if (_objectImpl != null) {
                _objectSkeleton = new Skeleton(_objectImpl, _objectAPI);
                _objectSkeleton.setHomeClass(_homeAPI);
            } else
                _objectSkeleton = _homeSkeleton;

            if ("true".equals(getInitParameter("debug"))) {
            }
            if ("false".equals(getInitParameter("send-collection-type")))
                setSendCollectionType(false);
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Class<?> findRemoteAPI(Class<?> implClass) {
        /*
		 * if (implClass == null || implClass.equals(GenericService.class))
		 * return null;
		 * 
		 * Class []interfaces = implClass.getInterfaces();
		 * 
		 * if (interfaces.length == 1) return interfaces[0];
		 * 
		 * return findRemoteAPI(implClass.getSuperclass());
		 */
        return null;
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassLoader loader = getContextClassLoader();

        if (loader != null)
            return Class.forName(className, false, loader);
        else
            return Class.forName(className);
    }

    protected ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private void init(Object service) throws ServletException {
        if (!this.getClass().equals(HttpSkeleton.class)) {
        } else if (service instanceof IService)
            ((IService) service).init(getServletConfig());
        else if (service instanceof Servlet)
            ((Servlet) service).init(getServletConfig());
    }

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (!req.getMethod().equals("POST")) {
            res.setStatus(500);
            PrintWriter out = res.getWriter();
            res.setContentType("text/html");
            out.println("<h1>RPC Requires POST</h1>");
            return;
        }
        String serviceId = req.getPathInfo();
        String objectId = req.getParameter("id");
        if (objectId == null)
            objectId = req.getParameter("ejbid");

        SkeletonContext.begin(req, res, serviceId, objectId);
        try
        {
            InputStream is = request.getInputStream();
            OutputStream os = response.getOutputStream();
            response.setContentType("x-application/rpc");
            SerializerFactory serializerFactory = getSerializerFactory();
            invoke(is, os, objectId, serializerFactory);
        } catch (RuntimeException e) {
            throw e;
        } catch (ServletException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServletException(e);
        } finally {
            SkeletonContext.end();
        }
    }

    public void invoke(InputStream is, OutputStream os, String objectId,
                       SerializerFactory serializerFactory) throws Exception {
        if (objectId != null)
            _objectSkeleton.invoke(is, os, serializerFactory);
        else
            _homeSkeleton.invoke(is, os, serializerFactory);
    }

    /**
     * 所有请求都会抵达到这里，然后把信息包分发出去
     */
    @Override
    public Packet handle(Packet packet) throws Exception {
        actionContext.invoke(packet);
        return packet;
    }

    @Override
    public boolean allowUpload(String user ,String passwd) throws Exception {
        return true;
    }

    /**
     * 所有上传请求都会抵达到这里，然后处理文件
     */
    @Override
    public void handleUpload(AbstractInput in, AbstractOutput out)
            throws Exception
    {
        handleUpload(new Uploader(in, out));
    }

    @Override
    public void handleUpload(IUpload upload) throws Exception {
    }
}