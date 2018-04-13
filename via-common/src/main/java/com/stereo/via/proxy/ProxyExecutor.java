package com.stereo.via.proxy;

import com.stereo.via.util.Daemon;
import com.stereo.via.util.InvokeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.stereo.via.proxy.ProxyExecutor.Op.*;

/**
 * 代理执行器
 * <p>
 * Created by liuj-ai on 2018/4/12.
 */
public class ProxyExecutor<T extends IProxyInstance> implements IProxyExecutor<T>, Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ProxyExecutor.class);

    enum Op {
        NOMAL(-1),
        INIT(0),
        INITED(1),
        CLOSE(2),
        CLOSED(3),
        INVOKE(4),
        INVOKED(5),
        RUN(6),
        READY(7);
        public int op;

        Op(int op) {
            this.op = op;
        }

        static boolean invokeIgnoreOp(Op op) {
            switch (op) {
                case INIT:
                case INITED:
                case RUN:
                case READY:
                case CLOSE:
                case CLOSED:
                    return true;
            }
            return false;
        }
    }

    private T proxy;
    private Class<T> proxyClass;
    private IProxyInstance proxyInstance;
    private final ClassLoader loader;
    private volatile Invoker invoker;
    private volatile boolean isOpened;
    private volatile Op op = NOMAL;
    private volatile Exception exception;
    private final Lock lock = new ReentrantLock();
    private final Condition proxyExecCondition = lock.newCondition();
    private final Condition externalExecCondition = lock.newCondition();
    private final AtomicLong invokerCallCount = new AtomicLong(0);

    public ProxyExecutor() {
        this.loader = ClassLoader.getSystemClassLoader();
    }

    @Override
    public void run() {
        exec();
    }

    private void op(Op op) {
        this.op = op;
    }

    void exec() {
        lock.lock();
        try {
            while (isOpened) {
                try {
                    //无操作|准备就绪|已打开|已执行
                    while (this.op.equals(NOMAL) || this.op.equals(READY) || this.op.equals(INITED) || this.op.equals(INVOKED)) {
                        proxyExecCondition.await();
                    }
                    if (this.op.equals(INIT))
                        initProxy();
                    else if (this.op.equals(CLOSE))
                        destroyProxy();
                    else if (this.op.equals(INVOKE))
                        invoker.invoke();
                    else if (this.op.equals(RUN))
                        ready();
                    else
                        logger.debug("ProxyExecutor exec op error");
                } catch (Exception ex) {
                    logger.error("ProxyExecutor exec error", ex);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void ready() {
        op(READY);
        externalExecCondition.signalAll();
    }

    /**
     * 初始化代理器
     */
    private void initProxy() {
        try {
            this.proxy = (T) Proxy.newProxyInstance(this.loader, new Class[]{proxyClass}, new InvocationHandlerImpl());
            this.proxyInstance.opened();
            op(INITED);
            externalExecCondition.signalAll();
        } catch (Error error) {
            error.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁代理器
     */
    private void destroyProxy() {
        this.proxyInstance.closed();
        this.proxyInstance = null;
        this.proxyClass = null;
        this.proxy = null;
        this.isOpened = false;
        op(CLOSED);
        this.externalExecCondition.signalAll();
    }


    @Override
    public T open(Class<T> t, IProxyInstance instance) {
        lock.lock();
        try {
            if (!isOpened) {
                this.isOpened = true;
                this.proxyClass = t;
                this.proxyInstance = instance;
                //启动操作
                op(RUN);
                //启动执行线程
                new Daemon(this).start();
                //等待就绪
                while (!this.op.equals(Op.READY)) {
                    externalExecCondition.await();
                }
                //执行初始化
                op(Op.INIT);
                proxyExecCondition.signal();
                //等待初始化
                while (!this.op.equals(INITED)) {
                    externalExecCondition.await();
                }
                return proxy;
            } else {
                throw new RuntimeException("ProxyExecutor is isOpened");
            }
        } catch (Exception ex) {
            logger.error("ProxyExecutor open error", ex);
            throw new RuntimeException(ex);
        } finally {
            op(NOMAL);
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            if (this.op.equals(CLOSE))
                return;
            else {
                while (invokerCallCount.get() > 0) {
                    externalExecCondition.await();
                }
                op(Op.CLOSE);
                proxyExecCondition.signal();
                while (!this.op.equals(CLOSED)) {
                    externalExecCondition.await();
                }
            }
        } catch (Exception ex) {
            logger.error("ProxyExecutor close error", ex);
            throw new RuntimeException(ex);
        } finally {
            op(NOMAL);
            lock.unlock();
        }
    }

    class Invoker {
        volatile boolean invoked;
        String method;
        Object result;
        Class<?> returnType;
        Object[] params;

        public void invoke() throws Exception {
            try {
                Object[] methodResult = InvokeUtils.findMethodWithExactParameters(proxyInstance, method, params);
                if (methodResult.length == 0 || methodResult[0] == null) {
                    throw new IllegalArgumentException("ProxyExecutor invoke params error");
                } else {
                    Method method = (Method) methodResult[0];
                    Object[] methodParams = (Object[]) methodResult[1];
                    this.result = method.invoke(proxyInstance, methodParams);
                }
                op(INVOKED);
            } catch (Exception ex) {
                logger.error("ProxyExecutor Invoker error", ex);
                ProxyExecutor.this.exception = ex;
                throw ex;
            } finally {
                externalExecCondition.signalAll();
                invoked = true;
            }
        }

        Invoker(String method, Class<?> returnType, Object[] params) {
            this.method = method;
            this.returnType = returnType;
            this.params = params;
            this.invoked = false;
        }

    }

    private Invoker createInvoker(String method, Class<?> returnType, Object[] params) {
        return new Invoker(method, returnType, params);
    }

    class InvocationHandlerImpl implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                lock.lock();
                while (true) {
                    if (Op.invokeIgnoreOp(op)) {
                        return null;
                    }
                    invokerCallCount.incrementAndGet();
                    while (!op.equals(NOMAL)) {
                        externalExecCondition.await();
                    }
                    op(Op.INVOKE);
                    //创建执行内容
                    ProxyExecutor.this.invoker = createInvoker(method.getName(), method.getReturnType(), args);
                    //唤醒执行器
                    proxyExecCondition.signal();
                    //等待执行完成
                    while (!op.equals(INVOKED)) {
                        externalExecCondition.await();
                    }
                    op(NOMAL);
                    externalExecCondition.signalAll();
                    invokerCallCount.decrementAndGet();
                    //异常则抛出
                    if (exception != null) {
                        throw exception;
                    }
                    //无异常取结果
                    else {
                        return invoker.result;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }
}