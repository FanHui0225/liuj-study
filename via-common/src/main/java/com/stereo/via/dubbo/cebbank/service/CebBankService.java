package com.stereo.via.dubbo.cebbank.service;

import com.stereo.via.dubbo.cebbank.dto.In;
import com.stereo.via.dubbo.cebbank.dto.Out;

/**
 * Created by stereo on 17-3-31.
 */
public interface CebBankService {
    public Out pay(In in) throws Exception;
}
