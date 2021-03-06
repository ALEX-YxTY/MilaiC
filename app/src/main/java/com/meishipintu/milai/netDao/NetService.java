package com.meishipintu.milai.netDao;

import android.support.annotation.Nullable;

import com.meishipintu.milai.beans.AppInfo;
import com.meishipintu.milai.beans.BindTelInfo;
import com.meishipintu.milai.beans.ExchangeRiceLog;
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

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Administrator on 2016/8/4.
 */
public interface NetService {

    //用类型传参时要添加头部信息
    //手机号登录
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/loginTel")
    Observable<ResponseBody> loginHttp(@Body LoginInfoTel info);

    //手机号登录新接口
    @FormUrlEncoded
    @POST("mspt/Activity/mobile_login")
    Observable<HttpResult<UserInfo>> loginHttpNew(@Field("mobile") String mobile, @Field("verify") String verify);

    //用类型传参时要添加头部信息
    //第三方登录
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/login")
    Observable<ResponseBody> loginAuthHttp(@Body LoginInfo info);

    //用类型传参时要添加头部信息
    //获取用户详细信息
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/getDetail")
    Observable<ResponseBody> getUserDetailInfoHttp(@Body Uid uid);

    //用类型传参时要添加头部信息
    //更新用户详细信息
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/updateDetail")
    Observable<ResponseBody> updateDetailHttp(@Body UserDetailInfo userDetailInfo);

    //用类型传参时要添加头部信息
    //手机号注册
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/register")
    Observable<ResponseBody> registerHttp(@Body RegisterInfo info);

    //用类型传参时要添加头部信息
    //忘记密码
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/forgetpwd")
    Observable<ResponseBody> forgetPwdHttp(@Body ResetPwdInfo info);

    //用类型传参时要添加头部信息
    //绑定手机号
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/bindtel")
    Observable<ResponseBody> bindTelHttp(@Body BindTelInfo info);

    //获取Welfare页面信息
    @FormUrlEncoded
    @POST("mspt/Activity/index ")
    Observable<HttpResult<List<Welfare>>> getWelfareHttp(@Field("cityid") int cityId, @Field("page") int page);

    //获取Exchange页面信息
    @FormUrlEncoded
    @POST("mspt/Activity/exchange_goods")
    Observable<ResponseBody> getExchangeHttp(@Field("activity_id") String activity_id, @Field("mobile") String mobile);

    //获取抢米页面信息
    @FormUrlEncoded
    @POST("mspt/Activity/mi_manage")
    Observable<HttpResult<List<Task>>> getTaskHttp(@Field("cityid") int cityId, @Field("page") int page,@Nullable @Field ("uid") String uid);

    //获取通知页面信息
    @POST("mspt/Activity/getPushInfo")
    Observable<HttpResult<List<Notice>>> getNoticeHttp();

    //获取我的米
    @FormUrlEncoded
    @POST("mspt/Activity/getUserRice")
    Observable<ResponseBody> getMiHttp(@Field("uid") String uid);

    //点赞
    @FormUrlEncoded
    @POST("mspt/Activity/doLikes")
    Observable<ResponseBody> getLikesHttp(@Field("uid") String uid,@Field("mid") String mid);

    //点赞
    @FormUrlEncoded
    @POST("mspt/Activity/doForward")
    Observable<ResponseBody> getdoForwardHttp(@Field("uid") String uid,@Field("mid") String mid,@Field("type") String type);

    //图片地址获取
    @POST("mspt/Activity/getIndexImg")
    Observable<ResponseBody> getstartpictureHttp();

    //获取抢米记录
    @FormUrlEncoded
    @POST("mspt/Activity/getUserGrabRiceLog")
    Observable<HttpResult<List<GrabRiceLog>>> getMiLogHttp(@Field("uid") String uid);

    //获取兑换米记录
    @FormUrlEncoded
    @POST("mspt/Activity/exchange_log")
    Observable<HttpResult<List<ExchangeRiceLog>>> getExchangeLogHttp(@Field("uid") String uid);

    //获取消费记录
    @FormUrlEncoded
    @POST("mspt/Activity/getShoppingRecords ")
    Observable<ResponseBody> getConsumeLogHttp(@Field("uid") String uid
            , @Field("page") int page);


    //获取我的商户券
    @FormUrlEncoded
    @POST("mspt/Activity/getUserCoupon")
    Observable<ResponseBody> getCouponHttp(@Field("uid") String uid, @Field("status") int status);


    //用类型传参时要添加头部信息
    //获取验证码
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("mspt/member/verifytel")
    Observable<ResponseBody> getVerifyCodeHttp(@Body GetVCodeRequest getVCodeRequest);


    //获取系统信息
    @FormUrlEncoded
    @POST("http://b.milaipay.com/test/getSystem")
    Observable<HttpResult<AppInfo>> getSystemLHttp(@Field("app_type") int app_type);

    //上传头像
    @Multipart
    @POST("mspt/member_detail/adduserpic")
    Observable<ResponseBody> addHeaderPicHttp(@Part MultipartBody.Part file1, @Part("uid") RequestBody uid);

}
