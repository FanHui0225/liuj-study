package com.stereo.via.dubbo.cebbank.service;

import com.stereo.via.dubbo.cebbank.dto.In;
import com.stereo.via.dubbo.cebbank.dto.Out;

/**
 * Created by stereo on 17-3-29.
 */
public class CebBankManager extends CebBankClient implements ICebBankManager{


    public Out applyWorkKey(In in) throws Exception
    {
        return null;
    }

    @Override
    public Out queryBill(In in) throws Exception {
        return null;
    }

    @Override
    public Out billCharge(In in) throws Exception {
        return null;
    }

}
