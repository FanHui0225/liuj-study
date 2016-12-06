package com.stereo.via.rpc.server;

import com.stereo.via.rpc.core.context.ActionContext;
import com.stereo.via.rpc.exc.RpcRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 初始化应用
 *
 * @author liujing
 */
public abstract class ContextLoader implements ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger(ContextLoader.class);

    protected final static ActionContext actionContext = ActionContext.getInstance();

    protected final static Properties defaultStrategies = new Properties();

    protected static final String DEFAULT_STRATEGIES_PATH = "app.properties";

    protected String getProPath() {
        return ContextLoader.DEFAULT_STRATEGIES_PATH;
    }

    private void initProperties(ServletContext context) {
        InputStream in = null;
        try {
            //web
            //in = new FileInputStream(context.getRealPath(DEFAULT_STRATEGIES_PATH));
            //classpath
            in = ContextLoader.class.getClassLoader().getResourceAsStream(getProPath());
            defaultStrategies.load(in);
            for (Iterator<Entry<Object, Object>> iterator = defaultStrategies
                    .entrySet().iterator(); iterator.hasNext(); )
            {
                Entry<Object, Object> entry = iterator.next();
                actionContext.setAttribute(entry.getKey().toString(), entry.getValue());
            }
            in.close();
        } catch (Exception ex) {
            logger.error("loading app.properties error", ex);
            throw new RpcRuntimeException(ex);
        }
    }

    public abstract void initialized(ServletContext context);

    public abstract void destroyed(ServletContext context);

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        initProperties(arg0.getServletContext());
        initialized(arg0.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        destroyed(arg0.getServletContext());
    }
}