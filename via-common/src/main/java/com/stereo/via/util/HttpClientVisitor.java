package com.stereo.via.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Httpclient封装
 *
 * Created by liuj-ai on 2018/10/30.
 */
public enum HttpClientVisitor {
    GET {
        public Result invoke(String uri, Map<String, String> headers, Charset charset) throws Exception {
            return get(uri, headers, charset);
        }
    },
    POST {
        public Result formData(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return post(uri, headers, body, null, CONTENT_TYPE.FORM_DATA, charset);
        }

        public Result formUrlencoded(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return post(uri, headers, body, null, CONTENT_TYPE.X_WWW_FORM_URLENCODED, charset);
        }

        public Result textPlain(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return post(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_PLAIN, charset);
        }

        public Result applicationJson(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return post(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JSON, charset);
        }

        public Result applicationJavascript(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return post(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JAVASCRIPT, charset);
        }

        public Result applicationXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return post(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_XML, charset);
        }

        public Result textXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return post(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_XML, charset);
        }

        public Result textHtml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return post(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_HTML, charset);
        }
    },
    PUT {
        public Result formData(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return put(uri, headers, body, null, CONTENT_TYPE.FORM_DATA, charset);
        }

        public Result formUrlencoded(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return put(uri, headers, body, null, CONTENT_TYPE.X_WWW_FORM_URLENCODED, charset);
        }

        public Result textPlain(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return put(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_PLAIN, charset);
        }

        public Result applicationJson(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return put(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JSON, charset);
        }

        public Result applicationJavascript(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return put(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JAVASCRIPT, charset);
        }

        public Result applicationXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return put(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_XML, charset);
        }

        public Result textXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return put(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_XML, charset);
        }

        public Result textHtml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return put(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_HTML, charset);
        }
    },
    PATCH {
        public Result formData(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return patch(uri, headers, body, null, CONTENT_TYPE.FORM_DATA, charset);
        }

        public Result formUrlencoded(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return patch(uri, headers, body, null, CONTENT_TYPE.X_WWW_FORM_URLENCODED, charset);
        }

        public Result textPlain(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return patch(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_PLAIN, charset);
        }

        public Result applicationJson(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return patch(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JSON, charset);
        }

        public Result applicationJavascript(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return patch(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JAVASCRIPT, charset);
        }

        public Result applicationXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return patch(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_XML, charset);
        }

        public Result textXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return patch(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_XML, charset);
        }

        public Result textHtml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return patch(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_HTML, charset);
        }
    },
    DELETE {
        public Result formData(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return delete(uri, headers, body, null, CONTENT_TYPE.FORM_DATA, charset);
        }

        public Result formUrlencoded(String uri, Map<String, String> headers, Map<String, String> body,
            Charset charset) throws Exception {
            return delete(uri, headers, body, null, CONTENT_TYPE.X_WWW_FORM_URLENCODED, charset);
        }

        public Result textPlain(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return delete(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_PLAIN, charset);
        }

        public Result applicationJson(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return delete(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JSON, charset);
        }

        public Result applicationJavascript(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return delete(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_JAVASCRIPT, charset);
        }

        public Result applicationXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return delete(uri, headers, null, bodyAsString, CONTENT_TYPE.APPLICATION_XML, charset);
        }

        public Result textXml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return delete(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_XML, charset);
        }

        public Result textHtml(String uri, Map<String, String> headers, String bodyAsString,
            Charset charset) throws Exception {
            return delete(uri, headers, null, bodyAsString, CONTENT_TYPE.TEXT_HTML, charset);
        }
    };

    private final static Logger logger = LoggerFactory.getLogger(HttpClientVisitor.class);

    // HTTP Method
    enum METHOD {
        GET, POST, PUT, PATCH, DELETE
    }

    // Body content type
    enum CONTENT_TYPE {
        FORM_DATA,
        X_WWW_FORM_URLENCODED,
        TEXT_PLAIN,
        APPLICATION_JSON,
        APPLICATION_JAVASCRIPT,
        APPLICATION_XML,
        TEXT_XML,
        TEXT_HTML
    }

    public static final String FORM_DATA_TYPE = "multipart/form-data";
    public static final String X_WWW_FORM_URLENCODED_TYPE = "application/x-www-form-urlencoded";
    public static final String TEXT_PLAIN_TYPE = "text/plain";
    public static final String APPLICATION_JSON_TYPE = "application/json";
    public static final String APPLICATION_JAVASCRIPT_TYPE = "application/javascript";
    public static final String APPLICATION_XML_TYPE = "application/xml";
    public static final String TEXT_XML_TYPE = "text/xml";
    public static final String TEXT_HTML_TYPE = "text/html";

    // set request settings here. Recommended to read configuration from config file.
    static RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
        .setConnectTimeout(5000)
        .setSocketTimeout(5000).build();
    // set client settings here. Recommended to read configuration from config file.
    static CloseableHttpClient client = HttpClients.custom().setMaxConnPerRoute(100)
        .setMaxConnTotal(1000)
        .setConnectionTimeToLive(30, TimeUnit.SECONDS)
        .evictIdleConnections(30, TimeUnit.SECONDS)
        .build();

    public static class Result {
        private int status;
        private String content;

        Result(int status, String content) {
            this.status = status;
            this.content = content;
        }

        public int getStatus() {
            return this.status;
        }

        public String getContent() {
            return this.content;
        }
    }

    public Result invoke(String uri, Map<String, String> headers, Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result formData(String uri, Map<String, String> headers, Map<String, String> body,
        Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result formUrlencoded(String uri, Map<String, String> headers, Map<String, String> body,
        Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result textPlain(String uri, Map<String, String> headers, String body, Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result applicationJson(String uri, Map<String, String> headers, String body,
        Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result applicationJavascript(String uri, Map<String, String> headers, String body,
        Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result applicationXml(String uri, Map<String, String> headers, String body,
        Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result textXml(String uri, Map<String, String> headers, String body, Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    public Result textHtml(String uri, Map<String, String> headers, String body, Charset charset) throws Exception {
        throw new AbstractMethodError();
    }

    static Result get(String uri, Map<String, String> headers, Charset charset) throws Exception {
        assert (null != uri && uri.trim().length() > 0);
        if (null == charset) {
            charset = Consts.UTF_8;
        }
        HttpRequestBase request = request(uri, headers, METHOD.GET);
        return execute(uri, request, charset);
    }

    static Result post(String uri, Map<String, String> headers, Map<String, String> body, String bodyAsString,
        CONTENT_TYPE contentType, Charset charset) throws Exception {
        assert (null != uri && uri.trim().length() > 0);
        if (null == charset) {
            charset = Consts.UTF_8;
        }
        HttpEntityEnclosingRequestBase request = request(uri, headers, body, bodyAsString, contentType, METHOD.POST, charset);
        return execute(uri, request, charset);
    }

    static Result put(String uri, Map<String, String> headers, Map<String, String> body, String bodyAsString,
        CONTENT_TYPE contentType, Charset charset) throws Exception {
        assert (null != uri && uri.trim().length() > 0);
        if (null == charset) {
            charset = Consts.UTF_8;
        }
        HttpEntityEnclosingRequestBase request = request(uri, headers, body, bodyAsString, contentType, METHOD.PUT, charset);
        return execute(uri, request, charset);
    }

    static Result patch(String uri, Map<String, String> headers, Map<String, String> body, String bodyAsString,
        CONTENT_TYPE contentType, Charset charset) throws Exception {
        assert (null != uri && uri.trim().length() > 0);
        if (null == charset) {
            charset = Consts.UTF_8;
        }
        HttpEntityEnclosingRequestBase request = request(uri, headers, body, bodyAsString, contentType, METHOD.PATCH, charset);
        return execute(uri, request, charset);
    }

    static Result delete(String uri, Map<String, String> headers, Map<String, String> body, String bodyAsString,
        CONTENT_TYPE contentType, Charset charset) throws Exception {
        assert (null != uri && uri.trim().length() > 0);
        if (null == charset) {
            charset = Consts.UTF_8;
        }
        HttpRequestBase request = request(uri, headers, METHOD.DELETE);
        return execute(uri, request, charset);
    }

    static Result execute(String uri, HttpRequestBase request, Charset charset) throws Exception {
        Result result;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), charset);
            logger.debug("uri:{}, statusCode:{}", uri, status);
            if (status < 200 || status > 400) {
                logger.debug("HTTP exception, statusCode:{}, content:{}", status, content);
            }
            result = new Result(status, content);
        } catch (Exception e) {
            logger.error("", e);
            result = new Result(500, e.getMessage());
        } finally {
            if (null != response) {
                EntityUtils.consume(response.getEntity());
            }
            if (null != request) {
                request.releaseConnection();
            }
        }
        return result;
    }

    /**
     * @param uri
     * @param headers
     * @param method - GET, DELETE
     * @return HttpRequestBase
     */
    static HttpRequestBase request(String uri, Map<String, String> headers, METHOD method) throws Exception {
        HttpRequestBase httpBase;
        if (method == METHOD.GET) {
            httpBase = new HttpGet(uri);
        } else if (method == METHOD.DELETE) {
            httpBase = new HttpDelete(uri);
        } else {
            throw new NoSuchMethodError("method is not allowed.");
        }
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpBase.addHeader(entry.getKey(), entry.getValue());
            }
        }
        httpBase.setConfig(requestConfig);
        return httpBase;
    }

    /**
     * @param uri
     * @param headers
     * @param body
     * @param bodyAsString
     * @param contentType
     * @param method - POST, PATCH, PUT, (DELETE)
     * @param charset
     * @return HttpEntityEnclosingRequestBase
     */
    static HttpEntityEnclosingRequestBase request(String uri, Map<String, String> headers, Map<String, String> body,
        String bodyAsString, CONTENT_TYPE contentType, METHOD method, Charset charset) throws Exception {
        HttpEntityEnclosingRequestBase httpBase;
        if (method == METHOD.POST) {
            httpBase = new HttpPost(uri);
        } else if (method == METHOD.PATCH) {
            httpBase = new HttpPatch(uri);
        } else if (method == METHOD.PUT) {
            httpBase = new HttpPut(uri);
        } else if (method == METHOD.DELETE) {
            // you should implement yourself's HttpDelete by extends HttpEntityEnclosingRequestBase
            // cause of @link org.apache.http.client.methods.HttpDelete extends HttpRequestBase, you
            // cannot use entity.
            throw new NoSuchMethodError("DELETE method is not provided.");
        } else {
            throw new NoSuchMethodError("method is not allowed.");
        }
        httpBase.setConfig(requestConfig);
        httpBase.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, getMimeType(contentType)));
        httpBase.setHeaders(partHeaders(headers, charset));
        httpBase.setEntity(partEntity(body, bodyAsString, contentType, charset));
        return httpBase;
    }

    static Header[] partHeaders(Map<String, String> headers, Charset charset) {
        if (null != headers && headers.size() > 0) {
            Header[] arr = new Header[headers.size()];
            int index = 0;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                arr[index++] = new BasicHeader(entry.getKey(), entry.getValue());
            }
            return arr;
        }
        return null;
    }

    /**
     * @param body
     * @param bodyAsString
     * @param contentType
     * @param charset
     * @return HttpEntity
     */
    static HttpEntity partEntity(Map<String, String> body, String bodyAsString, CONTENT_TYPE contentType,
        Charset charset) throws UnsupportedEncodingException, NoSuchMethodException {
        if (null != body && body.size() > 0) {
            if (CONTENT_TYPE.FORM_DATA == contentType) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                for (Map.Entry<String, String> entry : body.entrySet()) {
                    //noinspection ConstantConditions
                    builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.create(getMimeType(contentType), charset)));
                }
                return builder.build();
            } else if (CONTENT_TYPE.X_WWW_FORM_URLENCODED == contentType) {
                List<NameValuePair> params = new LinkedList<>();
                for (Map.Entry<String, String> entry : body.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                return new UrlEncodedFormEntity(params, charset);
            } else {
                throw new NoSuchMethodException("Only Content-Type 'form-data' or 'x-www-form-urlencoded' can use body params.");
            }
        }
        if (null != bodyAsString && bodyAsString.trim().length() > 0) {
            return new StringEntity(bodyAsString, ContentType.create(getMimeType(contentType), charset));
        }
        return null;
    }

    static String getMimeType(CONTENT_TYPE contentType) {
        switch (contentType) {
            case FORM_DATA:
                return FORM_DATA_TYPE;
            case X_WWW_FORM_URLENCODED:
                return X_WWW_FORM_URLENCODED_TYPE;
            case TEXT_PLAIN:
                return TEXT_PLAIN_TYPE;
            case APPLICATION_JSON:
                return APPLICATION_JSON_TYPE;
            case APPLICATION_JAVASCRIPT:
                return APPLICATION_JAVASCRIPT_TYPE;
            case APPLICATION_XML:
                return APPLICATION_XML_TYPE;
            case TEXT_XML:
                return TEXT_XML_TYPE;
            case TEXT_HTML:
                return TEXT_HTML_TYPE;
            default:
                return TEXT_PLAIN_TYPE;
        }
    }
}