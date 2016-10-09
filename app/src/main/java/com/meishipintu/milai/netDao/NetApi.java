package com.meishipintu.milai.netDao;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.meishipintu.milai.beans.AppInfo;
import com.meishipintu.milai.beans.BindTelInfo;
import com.meishipintu.milai.beans.ConsumeRecordInfo;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.beans.GetVCodeRequest;
import com.meishipintu.milai.beans.GrabRiceLog;
import com.meishipintu.milai.beans.HttpResult;
import com.meishipintu.milai.beans.LoginInfo;
import com.meishipintu.milai.beans.LoginInfoTel;
import com.meishipintu.milai.beans.Notice;
import com.meishipintu.milai.beans.RegisterInfo;
import com.meishipintu.milai.beans.ResetPwdInfo;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.beans.Uid;
import com.meishipintu.milai.beans.UserDetailInfo;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.beans.Welfare;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/8/4.
 */
public class NetApi {

    private static NetApi netApi;
    private NetService netService;
    private Retrofit retrofit = null;


    private NetApi() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ConstansUtils.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        netService = retrofit.create(NetService.class);
    }

    public static NetApi getInstance() {
        //因为netService是静态对象，所以使用静态锁
        synchronized (NetApi.class) {
            if (netApi == null) {
                netApi = new NetApi();
            }
            return netApi;
        }
    }

    public Observable<UserInfo> login(LoginInfoTel loginInfo){
        return netService.loginHttp(loginInfo).flatMap(new Func1<ResponseBody, Observable<UserInfo>>() {
            @Override
            public Observable<UserInfo> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "json:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(jsonObject.getJSONObject("memberInfo").toString()
                                , UserInfo.class);
                        return Observable.just(userInfo);
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        });

    }

    public Observable<UserInfo> loginNew(String mobile, String verify) {
        return netService.loginHttpNew(mobile, verify).map(new MyResultFunc<UserInfo>());
    }

    public Observable<String> register(RegisterInfo registerInfo){
        return netService.registerHttp(registerInfo).flatMap(new Func1<ResponseBody, Observable<String>>() {
            @Override
            public Observable<String> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "register_result:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        return Observable.just("注册成功");
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        });

    }

    public Observable<List<Notice>> getNotice() {

        return netService.getNoticeHttp().map(new MyResultFunc<List<Notice>>());

    }

    public Observable<UserDetailInfo> getUserDetailInfo(Uid uid) {
        return netService.getUserDetailInfoHttp(uid).flatMap(new Func1<ResponseBody, Observable<UserDetailInfo>>() {
            @Override
            public Observable<UserDetailInfo> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "userinfoget:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        JSONObject jsonResult = jsonObject.getJSONObject("memberInfo");
                        UserDetailInfo userDetailInfo = new UserDetailInfo();
                        userDetailInfo.setName(jsonResult.getString("name"));
                        userDetailInfo.setRealname(jsonResult.getString("realname"));
                        userDetailInfo.setSex(jsonResult.getInt("sex"));
                        userDetailInfo.setTel(jsonResult.getString("tel"));
                        userDetailInfo.setUid(jsonResult.getString("uid"));
                        userDetailInfo.setAddress(jsonResult.getString("address"));
                        userDetailInfo.setSignature(jsonResult.getString("signature"));
                        userDetailInfo.setUrl(jsonResult.getString("url"));
                        return Observable.just(userDetailInfo);
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<String> updateUserDetail (UserDetailInfo userDetailInfo) {
        return netService.updateDetailHttp(userDetailInfo).flatMap(new Func1<ResponseBody, Observable<String>>() {
            @Override
            public Observable<String> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "json:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        return Observable.just("保存成功");
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<List<Welfare>> getWelfare(int cityId, int page) {
        return netService.getWelfareHttp(cityId,page).map(new MyResultFunc<List<Welfare>>());
    }

    public Observable<List<Task>> getTask(int cityId, int page) {
        return netService.getTaskHttp(cityId,page).map(new MyResultFunc<List<Task>>());
    }

    public Observable<String> getMi(String uid) {
        return netService.getMiHttp(uid).map(new Func1<ResponseBody, String>() {
            @Override
            public String call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    if (jsonObject.getInt("status") == 1) {
                        return jsonObject.getString("credit");
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
        });
    }

    public Observable<GrabRiceLog> getMiLog(String uid){
        return netService.getMiLogHttp(uid).map(new MyResultFunc<List<GrabRiceLog>>())
                .flatMap(new Func1<List<GrabRiceLog>, Observable<GrabRiceLog>>() {
            @Override
            public Observable<GrabRiceLog> call(List<GrabRiceLog> grabRiceLogs) {
                return Observable.from(grabRiceLogs);
            }
        });
    }

    public Observable<List<Coupon>> getCoupon(String uid, int status){
        if (status == 0) {
            status = 4;
        }
        return netService.getCouponHttp(uid, status).flatMap(new Func1<ResponseBody, Observable<List<Coupon>>>() {
            @Override
            public Observable<List<Coupon>> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    if (jsonObject.getInt("status") == 1) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        List<Coupon> couponList = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject json = (JSONObject) data.get(i);
                            Coupon coupon = new Coupon();
                            coupon.setCouponSn(json.getString("coupon_sn"));
                            coupon.setName(json.getString("name"));
                            coupon.setValue(json.getDouble("value"));
                            coupon.setMinPrice(json.getDouble("min_price"));
                            coupon.setEndTime(DateUtils.getDateFormat(json.getString("end_time")));
                            coupon.setMi(json.getInt("is_mi") > 0);
                            coupon.setCouponShow(json.getString("couponShow"));
                            couponList.add(coupon);
                        }
                        return Observable.just(couponList);
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<String> getVerifyCode(GetVCodeRequest getVCodeRequest) {
        return netService.getVerifyCodeHttp(getVCodeRequest).flatMap(new Func1<ResponseBody, Observable<String>>() {
            @Override
            public Observable<String> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "jsonObject:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        return Observable.just(jsonObject.getString("verify"));
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<String> resetPwd(ResetPwdInfo info) {
        return netService.forgetPwdHttp(info).flatMap(new Func1<ResponseBody, Observable<String>>() {
            @Override
            public Observable<String> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "jsonObject:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        return Observable.just("密码修改成功");
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<UserInfo> loginAuth(LoginInfo info) {
        return netService.loginAuthHttp(info).flatMap(new Func1<ResponseBody, Observable<UserInfo>>() {
            @Override
            public Observable<UserInfo> call(ResponseBody responseBody) {
                try {
//                    Log.i("test", "response:" + responseBody.string());
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "json:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        Gson gson = new Gson();
                        UserInfo info = gson.fromJson(jsonObject.getJSONObject("profile").toString()
                                , UserInfo.class);
                        return Observable.just(info);
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JsonSyntaxException e) {
                    Log.i("test", "json synctaxException");
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<ConsumeRecordInfo> getConsumeRecord(String uid, int page) {
        return netService.getConsumeLogHttp(uid,page).flatMap(new Func1<ResponseBody
                , Observable<ConsumeRecordInfo>>() {
            @Override
            public Observable<ConsumeRecordInfo> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    if (jsonObject.getInt("status") != 1) {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<ConsumeRecordInfo> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsInfo = jsonArray.getJSONObject(i);
                            ConsumeRecordInfo info = new ConsumeRecordInfo();
                            info.setCreateTime(jsInfo.getString("create_" +
                                    "time"));
                            info.setShopName(jsInfo.getString("shop_name"));
                            info.setTradeValue((float) jsInfo.getDouble("money"));
                            info.setMiDiscount((float) jsInfo.getDouble("score"));
                            info.setCouponDiscount((float) jsInfo.getDouble("value"));
                            info.setRealPay((float) jsInfo.getDouble("total_fee"));
                            list.add(info);
                        }
                        return Observable.from(list);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<AppInfo> getSystemInfo(final int app_type) {
        return netService.getSystemLHttp(app_type).map(new MyResultFunc<AppInfo>());
    }

    public Observable<String> bindTel(BindTelInfo info) {
        return netService.bindTelHttp(info).flatMap(new Func1<ResponseBody, Observable<String>>() {
            @Override
            public Observable<String> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "response:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        return Observable.just(jsonObject.toString());
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    public Observable<String> addHeaderPicHttp(File photeFile, String uid) {
        //将file类型转化为MultipartBody.part类型
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/*"), photeFile);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("picture", "avator.jpg", photoRequestBody);
//        MultipartBody.Part uidPart = MultipartBody.Part.createFormData("uid", uid);
        RequestBody uidRequest = RequestBody.create(null, uid);
        return netService.addHeaderPicHttp(photo, uidRequest).flatMap(new Func1<ResponseBody, Observable<String>>() {
            @Override
            public Observable<String> call(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    Log.i("test", "response:" + jsonObject.toString());
                    if (jsonObject.getInt("result") == 1) {
                        return Observable.just(ConstansUtils.URL + jsonObject.getString("picture"));
                    } else {
                        throw new RuntimeException(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    class MyResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            if (httpResult.getStatus() != 1) {
                throw new RuntimeException(httpResult.getMsg());
            }
            Log.i("test", httpResult.getData().toString());
            return httpResult.getData();
        }
    }



}
