//package com.stereo.study.glodon;
//
//import com.alibaba.fastjson.JSONObject;
//import com.stereo.study.util.HttpClientVisitor;
//
///**
// * Created by liuj-ai on 2019/9/25.
// */
//public class GlodonPTC {
//
//    public boolean setClock(String network_type, String tele_operator, String ip,
//                            String wifi_name, String mac_address, String longitude,
//                            String latitude, NetCallback<BaseResult, String> callback) throws JSONException {
//        JSONObject json = new JSONObject();
//        json.put("network_type", "WIFI");
//        json.put("tele_operator", "未知");
//        json.put("ip", "10.1.91.72");
//        json.put("wifi_name", "Glodon_Wifi");
//        json.put("mac_address", mac_address);
//        json.put("longitude", "116.288049");
//        json.put("latitude", "40.050979");
//        HttpClientVisitor.POST.applicationJson()
//
//
//        Call<ResponseBody> call = this.mAPIService.setClock(RequestBody.create(MediaType.parse("application/json"), json.toString()));
//        setOnNetworkListener(new GlodonNetWorkListener(callback, BaseResult.class, String.class));
//        GlodonHttpRequestPool.getInstance().request(this, call);
//        return true;
//    }
//
//
//    public boolean login(String username, String password, String system, NetCallback<LoginResult, String> callback) throws Exception {
//        String str = (System.currentTimeMillis() / 1000) + "GLODON_APP" + password;
//        password = Base64.encodeToString(RSAUtil.encryptData(Base64.encode(str.getBytes(Charset.forName("UTF-8")), 0), RSAUtil.loadPublicKey(Environment.ApplicationContext.getAssets().open("public_key.pem"))), 0).replaceAll("\n", "");
//        JSONObject json = new JSONObject();
//        json.put("user", username);
//        json.put(Constant.PASSWORD, password);
//        json.put(Constant.SYSTEM, system);
//        json.put("check_version", "Y");
//        Call<ResponseBody> call = this.mAPIService.login(RequestBody.create(MediaType.parse("application/json"), json.toString()));
//        setOnNetworkListener(new LoginNetWorkListener(callback, LoginResult.class, String.class));
//        GlodonHttpRequestPool.getInstance().request(this, call);
//        return true;
//    }
//
//    public static void main(String[] args) {
//
//
//    }
//}
