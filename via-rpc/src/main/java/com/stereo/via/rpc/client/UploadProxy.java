package com.stereo.via.rpc.client;

import com.stereo.via.rpc.api.IConnection;
import com.stereo.via.rpc.api.IProgress;
import com.stereo.via.rpc.api.IUpload;
import com.stereo.via.rpc.exc.IOExceptionWrapper;
import com.stereo.via.rpc.exc.RpcRuntimeException;
import com.stereo.via.rpc.io.AbstractInput;
import com.stereo.via.rpc.io.AbstractOutput;
import com.stereo.via.rpc.utils.FileUtil;
import com.stereo.via.rpc.utils.MD5CheckSum;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * 上载代理
 *
 * @author liujing
 * @version 2014.5.21
 */
public class UploadProxy implements InvocationHandler, Serializable {

    private static final Logger log = Logger.getLogger(UploadProxy.class
            .getName());

    /**
     *
     */
    private static final long serialVersionUID = -4610802859772604635L;

    private URL _url;

    private File _src;

    private String _md5;

    private String _fileName;

    private String _user, _passwd;

    private String _remotePath;

    protected RpcProxyFactory _factory;

    protected BufferedInputStream _bis;

    protected AtomicInteger counter = new AtomicInteger(0);

    private final MD5CheckSum checkSum = new MD5CheckSum();

    protected UploadProxy(URL url, String src, String remotePath, String remoteFileName, RpcProxyFactory factory,
                          String user, String passwd) {
        this._url = url;
        this._user = user;
        this._passwd = passwd;
        this._factory = factory;
        this._src = new File(src);
        if (StringUtils.isEmpty(remoteFileName))
            this._fileName = _src.getName();
        else
            {
                String ext = FileUtil.getFileExtension(_src);
                if(StringUtils.isEmpty(ext))
                    this._fileName = remoteFileName;
                else
                    this._fileName = remoteFileName + "." + ext;
            }
        this._remotePath = remotePath;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
        IConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        int length = 0;
        int total = 0;
        int currentlength = 0;
        try {

            if (_src.exists())
                _bis = new BufferedInputStream(new FileInputStream(_src));
            else
                throw new IOExceptionWrapper(new Exception(
                        "file src not found."));

            //验证上载
            if (isUsable(_url))
            {
                IProgress _listener = args !=null && args.length == 1 ? (IProgress) args[0] : null;
                total = _bis.available();
                conn = _factory.getConnectionFactory().open(_url);
                conn.addHeader("Content-Type", "x-application/rpc");
                conn.addHeader("Accept-Encoding", "deflate");
                try {
                    os = conn.getOutputStream();
                } catch (Exception e) {
                    throw new RpcRuntimeException(e);
                }
                AbstractOutput out = _factory.getRPCOutput(os);
                byte[] buffer = new byte[IUpload.FILE_SEGMENTED_BUF];

                // 写入文件信息
                out.call(IUpload.FILE_UPLOAD_METHOD, total);
                out.writeString(_fileName);
                out.writeString(_remotePath);
                out.flush();

                while (-1 != (length = _bis.read(buffer)))
                {
                    checkSum.process(buffer,0,length);
                    //写len
                    out.writeInt(length);
                    //写字节
                    out.writeBytes(buffer, 0, length);
                    out.flush();
                    counter.incrementAndGet();
                    currentlength += length;
                    if (null != _listener)
                        _listener.update(total, currentlength, counter.get());
                }
                //获取校验值
                _md5 = checkSum.processed();
                out.writeInt(IUpload.FILE_UPLOAD_EOF_MARKER);
                out.writeString(_md5);
                out.flush();
                conn.sendRequest();
                if ("deflate".equals(conn.getContentEncoding()))
                    is = new InflaterInputStream(conn.getInputStream(),
                            new Inflater(true));
                else
                    is = conn.getInputStream();
                AbstractInput in = _factory.getRPCInput(is);
                String result = in.readString();
                if (IUpload.FILE_UPLOAD_COMPLETED.equals(result))
                    return true;
                else if (IUpload.FILE_UPLOAD_FAILED.equals(result))
                    return false;
                else
                    throw new RuntimeException("upload not completed");
            } else
                throw new RuntimeException("upload not usable");
        } catch (Exception e) {
            throw new RpcRuntimeException(e);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (_bis != null)
                    _bis.close();
                if (conn != null)
                    conn.destroy();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isUsable(URL url) {
        InputStream is = null;
        OutputStream os = null;
        IConnection connection = null;
        try {
            connection = _factory.getConnectionFactory().open(_url);
            connection.addHeader("Content-Type", "x-application/rpc");
            connection.addHeader("Accept-Encoding", "deflate");
            os = connection.getOutputStream();
            AbstractOutput out = _factory.getRPCOutput(os);
            out.call(IUpload.FILE_UPLOAD_AVAILABLE_METHOD, 0);
            out.writeString(_user);
            out.writeString(_passwd);
            out.flush();
            connection.sendRequest();
            if ("deflate".equals(connection.getContentEncoding()))
                is = new InflaterInputStream(connection.getInputStream(),
                        new Inflater(true));
            else
                is = connection.getInputStream();
            AbstractInput in = _factory.getRPCInput(is);
            String result = in.readString();
            if (IUpload.FILE_UPLOAD_AUTH_COMPLETED.equals(result))
                return true;
            else if (IUpload.FILE_UPLOAD_AUTH_FAILED.equals(result))
                return false;
        } catch (Exception e) {
            log.log(Level.WARNING, e.toString(), e);
            return false;
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (connection != null)
                    connection.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}