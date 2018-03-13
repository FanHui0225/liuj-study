package com.stereo.via.scan;

/**
 * Created by liuj-ai on 2018/3/13.
 */
public interface IDisconfUpdatePipeline {
    void reloadDisconfFile(String var1, String var2) throws Exception;

    void reloadDisconfItem(String var1, Object var2) throws Exception;
}
