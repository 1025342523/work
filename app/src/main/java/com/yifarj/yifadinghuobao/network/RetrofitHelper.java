package com.yifarj.yifadinghuobao.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.yifarj.yifadinghuobao.app.YifaApplication;
import com.yifarj.yifadinghuobao.network.api.AccountService;
import com.yifarj.yifadinghuobao.network.api.CreateOrderService;
import com.yifarj.yifadinghuobao.network.api.FetchOrderService;
import com.yifarj.yifadinghuobao.network.api.FetchTraderContactService;
import com.yifarj.yifadinghuobao.network.api.FetchTraderService;
import com.yifarj.yifadinghuobao.network.api.GoodsListService;
import com.yifarj.yifadinghuobao.network.api.LoginService;
import com.yifarj.yifadinghuobao.network.api.LogoutService;
import com.yifarj.yifadinghuobao.network.api.MettingCodeService;
import com.yifarj.yifadinghuobao.network.api.MettingLoginService;
import com.yifarj.yifadinghuobao.network.api.OrderListService;
import com.yifarj.yifadinghuobao.network.api.OrderSummaryService;
import com.yifarj.yifadinghuobao.network.api.PasswordLoginService;
import com.yifarj.yifadinghuobao.network.api.ProductCategoryListService;
import com.yifarj.yifadinghuobao.network.api.ProductMemoryPriceService;
import com.yifarj.yifadinghuobao.network.api.PropertyListService;
import com.yifarj.yifadinghuobao.network.api.ReceiveMethodService;
import com.yifarj.yifadinghuobao.network.api.SaveOrderService;
import com.yifarj.yifadinghuobao.network.api.SaveTraderContactService;
import com.yifarj.yifadinghuobao.network.api.SaveTraderService;
import com.yifarj.yifadinghuobao.network.api.StockInfoForToolTipListService;
import com.yifarj.yifadinghuobao.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * RetrofitHelper
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 18:54
 */
public class RetrofitHelper {

    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }


    public static OrderListService getOrderListApi() {

        return createApi(OrderListService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static FetchOrderService getFetchOrderApi() {

        return createApi(FetchOrderService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static SaveOrderService getSaveOrderApi() {

        return createApi(SaveOrderService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static ReceiveMethodService getReceiveMethodApi() {

        return createApi(ReceiveMethodService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static MettingLoginService getMettingLoginApi() {

        return createApi(MettingLoginService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static MettingCodeService getMettingCodeApi() {

        return createApi(MettingCodeService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static LogoutService getLogoutApi() {

        return createApi(LogoutService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static CreateOrderService getCreateOrderInfo() {

        return createApi(CreateOrderService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static GoodsListService getGoodsListAPI() {

        return createApi(GoodsListService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static AccountService getAccountAPI() {

        return createApi(AccountService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static OrderSummaryService getOrderSummaryAPI(){
        return createApi(OrderSummaryService.class,ApiConstants.CUrl.BASE_URL);
    }


    public static LoginService getLoginApi() {

        return createApi(LoginService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static FetchTraderService getTraderApi() {

        return createApi(FetchTraderService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static StockInfoForToolTipListService getStockInfoListApi() {

        return createApi(StockInfoForToolTipListService.class, ApiConstants.CUrl.BASE_URL);
    }


    public static ProductMemoryPriceService getProductMemoryPriceApi() {

        return createApi(ProductMemoryPriceService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static ProductCategoryListService getProductCategoryListApi() {

        return createApi(ProductCategoryListService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static SaveTraderService saveTraderApi() {

        return createApi(SaveTraderService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static FetchTraderContactService fetchTraderContactApi() {

        return createApi(FetchTraderContactService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static SaveTraderContactService SaveTraderContactApi() {

        return createApi(SaveTraderContactService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static PasswordLoginService getPasswordLoginApi() {

        return createApi(PasswordLoginService.class, ApiConstants.CUrl.BASE_URL);
    }

    public static PropertyListService getPropertyListApi() {

        return createApi(PropertyListService.class, ApiConstants.CUrl.BASE_URL);
    }

    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private static <T> T createApi(Class<T> clazz, String baseUrl) {
        String url = baseUrl.substring(0, baseUrl.length() - 1);
        if (!url.equals("/")) {
            baseUrl += "/";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(YifaConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }


    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志,设置UA拦截器
     */
    private static void initOkHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(YifaApplication.getInstance()
                            .getCacheDir(), "HttpCache"), 1024 * 1024 * 10);

                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new CacheInterceptor())
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(new UserAgentInterceptor())
                            .build();
                }
            }
        }
    }


    /**
     * 添加UA拦截器
     */
    private static class UserAgentInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", ApiConstants.COMMON_UA_STR)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

    /**
     * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
     */
    private static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60 * 60;
            // 无网络时，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (CommonUtil.isNetworkAvailable(YifaApplication.getInstance())) {
                //有网络时只从网络获取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (CommonUtil.isNetworkAvailable(YifaApplication.getInstance())) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }
}
