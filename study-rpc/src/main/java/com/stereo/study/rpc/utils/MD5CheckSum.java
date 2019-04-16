package com.stereo.study.rpc.utils;

import com.stereo.study.rpc.exc.RpcRuntimeException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/11/25.
 */
public class MD5CheckSum {

    private MessageDigest messageDigest;
    public MD5CheckSum()
    {
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");

        }catch (NoSuchAlgorithmException ex)
        {
            throw new RpcRuntimeException(ex);
        }
    }
    public void process(byte[] input, int offset, int len)
    {
        messageDigest.update(input, offset, len);
    }
    public String processed()
    {
        return MD5Util.bufferToHex(messageDigest.digest());
    }
}
