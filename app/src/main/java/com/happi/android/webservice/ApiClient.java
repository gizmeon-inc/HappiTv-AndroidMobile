package com.happi.android.webservice;

import com.happi.android.models.CategoryModel;
import com.happi.android.models.FeaturedShowsResponseModel;
import com.happi.android.models.GetPayperviewVideoListResponseModel;
import com.happi.android.models.GetWatchListResponseModel;
import com.happi.android.models.HomeVideoModel;
import com.happi.android.models.LikedFlagResponseModel;
import com.happi.android.models.LiveScheduleResponse;
import com.happi.android.models.LogoutResponseModel;
import com.happi.android.models.PartnerResponseModel;
import com.happi.android.models.PartnerVideoListResponseModel;
import com.happi.android.models.PublisherModel;
import com.happi.android.models.RegisterWithEmailResponseModel;
import com.happi.android.models.ScheduleUpdatedResponseModel;
import com.happi.android.models.SelectedVideoResponseModel;
import com.happi.android.models.ShowListResponseModel;
import com.happi.android.models.ShowVideoUpdatedResponseModel;
import com.happi.android.models.UpdateReceiptResponseModel;
import com.happi.android.models.VodToLiveResponseModel;
import com.happi.android.models.WatchListShowsResponseModel;
import com.happi.android.models.WatchlistflagResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.happi.android.models.BasicResponse;
import com.happi.android.models.CategorisHomeListResponseModel;
import com.happi.android.models.CategoryListResponseModel;
import com.happi.android.models.ChannelListResponse;
import com.happi.android.models.ChannelScheduleResponseModel;
import com.happi.android.models.ChannelSubscriptionResponseModel;
import com.happi.android.models.IPAddressModel;
import com.happi.android.models.LanguageModel;
import com.happi.android.models.LikeUnlikeModel;
import com.happi.android.models.LoginResponseModel;
import com.happi.android.models.PhoneVerificationResponseModel;
import com.happi.android.models.SubscriptionTransactionModel;
import com.happi.android.models.UserSubscriptionResponseModel;
import com.happi.android.models.ASTVHomeResponse;
import com.happi.android.models.SearchResponseModel;
import com.happi.android.models.SessionTokenResponseModel;
import com.happi.android.models.SetLanguagePriorityRequestModel;
import com.happi.android.models.ShowsResponseModel;
import com.happi.android.models.TokenResponse;
import com.happi.android.models.VideoResponse;
import com.happi.android.models.VideoSubscriptionResponseModel;
import com.happi.android.models.YTResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.happi.android.utils.ConstantUtils.BASE_URL;
import static com.happi.android.utils.ConstantUtils.BASE_URL_TOKEN;
import static com.happi.android.utils.ConstantUtils.IP_BASE_URL;

public class ApiClient {

    private static Gson getGsonViaBuilder() {
        return new GsonBuilder().serializeNulls().create();
    }


    public static IpAddressApiService createIPService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(IP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(getGsonViaBuilder()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(IpAddressApiService.class);
    }

    public interface IpAddressApiService {

        @GET("json/")
        Observable<IPAddressModel> fetchIPAddress();

    }

    public static UsersService create() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(UsersService.class);
    }
    public static UsersService createToken() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_TOKEN)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(UsersService.class);
    }

    public static TokenService token() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(TokenService.class);
    }

    public interface UsersService {

        @POST("api/createPaymentToken")
        Observable<TokenResponse> getToken(@Header("access-token") String header,
                                           @Body JsonObject jsonObject);


        //===================== CHANNEL / PARTNER=============================

        @GET("api/Restallchannel")
        Observable<ChannelListResponse> Restallchannel(@Header("access-token") String header,
                                                       @Query("key") Integer key,
                                                       @Query("country_code") String country_code,
                                                       @Query("pubid") String publisher_id);

        @GET("api/Getallchannels")
        Observable<ChannelListResponse> getChannels(@Header("access-token") String header,
                                                    @Query("country_code") String country_code,
                                                    @Query("pubid") String publisher_id);

        @GET("api/liveSchedule")
        Observable<LiveScheduleResponse> getLiveSchedule(@Header("access-token") String header,
                                                         @Query("country_code") String country_code,
                                                         @Query("channelid") int channel_id);
        @GET("api/PopularChannels")
        Observable<ChannelListResponse> PopularChannels(@Header("access-token") String header,
                                                        @Query("country_code") String country_code,
                                                        @Query("pubid") String publisher_id);

        @GET("api/Channelhome")
        Observable<ASTVHomeResponse> getChannelHome(@Header("access-token") String header,
                                                    @Query("key") int key,
                                                    @Query("uid") int userId,
                                                    @Query("country_code") String country_code,
                                                    @Query("device_type") String device_type,
                                                    @Query("pubid") String publisher_id);

        @GET("api/partnerList")
        Observable<PartnerResponseModel> getPartnerList(@Header("access-token") String header,
                                                        @Query("country_code") String country_code,
                                                        @Query("pubid") String pubid);
        //@GET("api/partnerVideos")
        @GET("api/partnerVideosByShows")
        Observable<PartnerVideoListResponseModel> getPartnerVideos(@Header("access-token") String header,
                                                                   @Query("pubid") String pubid,
                                                                   @Query("partner_id") String partner_id,
                                                                   @Query("country_code") String country_code,
                                                                   @Query("uid") int userId);



        //===========================================================
        //===================== VIDEO ===============================

        @GET("api/GenerateToken")
        Observable<TokenResponse> getVideoToken(@Header("access-token") String header,
                                                @Query("pubid") String publisher_id);

        @GET("api/GenerateTokenLive")
        Observable<TokenResponse> getVideoTokenLive(@Header("access-token") String header,
                                                    @Query("pubid") String publisher_id);

        @GET("api/GetvideoList")
        Observable<VideoResponse> Getvideo(@Header("access-token") String header,
                                           @Query("key") Integer key,
                                           @Query("uid") Integer uid,
                                           @Query("country_code") String country_code,
                                           @Query("pubid") String publisher_id);

        @GET("api/Similarvideo")
        Observable<VideoResponse> Similarvideo(@Header("access-token") String header,
                                               @Query("uid") int uid,
                                               @Query("vid") int vid,
                                               @Query("country_code") String country_code,
                                               @Query("pubid") String publisher_id);

        @GET("api/GetSelectedVideo")
        Observable<VideoResponse> GetSelectedVideo(@Header("access-token") String header,
                                                   @Query("vid") Integer vid,
                                                   @Query("uid") Integer uid,
                                                   @Query("country_code") String country_code,
                                                   @Query("device_type") String device_type,
                                                   @Query("pubid") String publisher_id);

        @GET("api/NewArrivalsUpdated2")
       // Observable<VideoResponse> NewReleases(@Header("access-token") String header,
        Observable<ShowListResponseModel> NewReleases(@Header("access-token") String header,
                                                      @Query("country_code") String country_code,
                                                      @Query("pubid") String publisher_id);
        @GET("api/getfreevideos2")
        Observable<ShowListResponseModel> getFreeShowList(@Header("access-token") String header,
                                                          @Query("country_code") String country_code,
                                                          @Query("pubid") String publisher_id);

        @GET("GetHomeVideoUpdated2")
        Observable<HomeVideoModel> GetHomeVideos(@Header("access-token") String header,
                                                @Query("pubid") String publisher_id);
        @GET("api/getYTVOD")
        Observable<YTResponse> YTVideos(@Header("access-token") String header,
                                        @Query("uid") Integer uid,
                                        @Query("country_code") String country_code,
                                        @Query("device_id") String device_id,
                                        @Query("pubid") String publisher_id);


        @GET("api/getScheduleByDate")
        Observable<ChannelScheduleResponseModel>GetChannelSchedule(@Header("access-token") String header,
                                                                   @Query("device_id") String device_id,
                                                                   @Query("country_code") String country_code,
                                                                   @Query("start_utc") String start_utc,
                                                                   @Query("end_utc") String end_utc,
                                                                   @Query("channel_id") int channel_id,
                                                                   @Query("pubid") String publisher_id);

        @GET("api/GetFeaturedshows")
        //Observable<VideoResponse> GetFeaturedvideo(@Header("access-token") String header,
        Observable<FeaturedShowsResponseModel> GetFeaturedvideo(@Header("access-token") String header,
                                                                @Query("uid") Integer uid,
                                                                @Query("country_code") String country_code,
                                                                @Query("pubid") String publisher_id);

        @GET("api/GetAllLiveVideos")
        Observable<ChannelListResponse> GetPopularLiveVideos(@Header("access-token") String header,
                                                             @Query("country_code") String country_code,
                                                             @Query("pubid") String publisher_id);

        @GET("api/getShows")
        Observable<ShowsResponseModel> getShows(@Header("access-token") String header,
                                                @Query("country_code") String country_code,
                                                @Query("pubid") String publisher_id);

        @GET("api/getShowVideos")
        Observable<VideoResponse> getShowVideos(@Header("access-token") String header,
                                                     @Query("show_id") Integer showId,
                                                @Query("country_code") String country_code,
                                                @Query("pubid") String publisher_id);

        @GET("api/getShowVideosUpdated")
        Observable<ShowVideoUpdatedResponseModel> getVideoDetailsByShow (@Header("access-token") String header,
                                                                         @Query("pubid") String publisher_id,
                                                                         @Query("show_id") String show_id,
                                                                         @Query("country_code") String country_code,
                                                                         @Query("user_id") int user_id );
        @GET("api/GetshowsByCategoryUpdated2")
        Observable<ShowListResponseModel> getShowByCategory (@Header("access-token") String header,
                                                             @Query("pubid") String publisher_id,
                                                             @Query("country_code") String country_code,
                                                             @Query("genre_id") String genre_id);
        @GET("api/ProducershowsUpdated")
        Observable<ShowListResponseModel> getShowByProducer (@Header("access-token") String header,
                                                             @Query("pubid") String publisher_id,
                                                             @Query("device_type") String device_type,
                                                             @Query("country_code") String country_code,
                                                             @Query("producer") String producer);
        @GET("api/SimilarshowsUpdated")
        Observable<ShowListResponseModel> getSimilarShows (@Header("access-token") String header,
                                                           @Query("uid") String userId,
                                                           @Query("pubid") String publisher_id,
                                                           @Query("country_code") String country_code,
                                                           @Query("vid") String videoId);
        @GET("api/GetWatchlistUpdated")
        Observable<GetWatchListResponseModel> getWatchlist (@Header("access-token") String header,
                                                            @Query("pubid") String pubid,
                                                            @Query("country_code") String country_code,
                                                            @Query("uid") int uid);
        @GET("api/GetLikedShowsUpdated")
        Observable<GetWatchListResponseModel> getFavouritesList(@Header("access-token") String header,
                                                                @Query("pubid") String pubid,
                                                                @Query("country_code") String country_code,
                                                                @Query("uid") int uid);

        @GET("api/GetSchedule")
        Observable<ScheduleUpdatedResponseModel> getScheduleUpdated(@Header("access-token") String header,
                                                                    @Query("pubid") String publisher_id,
                                                                    @Query("channelid") String channel_id,
                                                                    @Query("startdate") String startdate,
                                                                    @Query("enddate") String enddate);

        //===========================================================
        //================= LIVE ================

        @GET("api/GetVodtoLivevideos")
        Observable<VodToLiveResponseModel> getSimilarListForLive (@Header("access-token") String header,
                                                                  @Query("country_code") String country_code,
                                                                  @Query("pubid") String publisher_id);


        //===========================================================
        //================= REGISTER/LOGIN/LANGUAGES ================

        @GET("Login?")
        Observable<LoginResponseModel> login(@Query("user_email") String email_id,
                                             @Query("password") String password,
                                             @Query("pubid") String publisher_id);

        //new login and logout
        @GET("Loginnew?")
        Observable<LoginResponseModel> newLogin(@Query("user_email") String email_id,
                                                @Query("password") String password,
                                                @Query("pubid") String publisher_id,
                                                @Query("device_id") String device_id,
                                                @Query("ipaddress") String ipaddress,
                                                @Query("version") String version);
        //Logoutuser?
        @GET("Logoutuser?")
        Observable<LogoutResponseModel> logout(@Query("user_id") int user_id,
                                               @Query("pubid") String publisher_id,
                                               @Query("device_id") String device_id,
                                               @Query("ipaddress") String ipaddress);

        //Logoutall
        @GET("Logoutall?")
        Observable<LogoutResponseModel> logoutAll(@Query("user_id") int user_id,
                                                  @Query("pubid") String publisher_id,
                                                  @Query("device_id") String device_id,
                                                  @Query("ipaddress") String ipaddress);

        @GET("Forgotpassword")
        Observable<BasicResponse> forgotPassword(@Query("user_email") String user_email,
                                                 @Query("pubid") String publisher_id);

        @GET("FBLogin")
        Call<LoginResponseModel> checkFbLoginDetails(@Query("facebook_id") String facebook_id);

        @POST("registerWithEmail")
        Observable<RegisterWithEmailResponseModel> RegisterWithEmail(@Body JsonObject registerJson);

        @GET("verifyOtpFromEmail")
        Observable<LoginResponseModel> verifyOtpFromEmail (@Query("user_id") int user_id,
                                                           @Query("pubid") String pubid,
                                                           @Query("otp") String otp);

        @GET("resendOtp")
        Observable<BasicResponse> resendOtp (@Query("user_id") int user_id,
                                            @Query("device_id") String device_id,
                                            @Query("ipaddress") String ipaddress,
                                            @Query("pubid") String publisher_id);
        @POST("Register")
        Observable<LoginResponseModel> Register(@Body JsonObject locationPost);

        @POST("GuestRegister")
        Observable<LoginResponseModel> GuestRegister(@Body JsonObject locationPost);

        @POST("api/LoginRemoval")
        Observable<LoginResponseModel> LoginRemoval(@Header("access-token") String header,
                                                    @Body JsonObject locationPost);

        @GET("api/GetLanguages")
        Observable<LanguageModel> GetLanguages();

        @POST("api/SetLanguagePriority")
        Observable<BasicResponse> SetLanguagePriority(@Header("access-token") String header,
                                                      @Body SetLanguagePriorityRequestModel setLanguagePriorityRequestModel);

        @GET("api/GetUserLanguages")
        Observable<LanguageModel> GetUserLanguages(@Header("access-token") String header,
                                                   @Query("uid") Integer uid);

        //===========================================================
        //===================== SUBSCRIPTION =====================


        @GET("api/getUserSubscriptions")
        Observable<UserSubscriptionResponseModel> getUserSubscriptions(@Header("access-token") String header,
                                                                       @Query("uid") Integer uid,
                                                                       @Query("device_id") String device_id,
                                                                       @Query("country_code") String country_code,
                                                                       @Query("pubid") String publisher_id);
        @GET("api/GetvideoSubscriptions")
        Observable<VideoSubscriptionResponseModel> getVideoSubscriptions(@Header("access-token") String header,
                                                                         @Query("video_id") Integer video_id,
                                                                         @Query("uid") Integer uid,
                                                                         @Query("device_id") String device_id,
                                                                         @Query("country_code") String country_code,
                                                                         @Query("pubid") String publisher_id);
        @GET("api/GetchannelSubscriptions")
        Observable<ChannelSubscriptionResponseModel> getChannelSubscriptions(@Header("access-token") String header,
                                                                             @Query("channel_id") Integer channel_id,
                                                                             @Query("uid") Integer uid,
                                                                             @Query("device_id") String device_id,
                                                                             @Query("country_code") String country_code,
                                                                             @Query("pubid") String publisher_id);
        @GET("api/checkPhoneVerification")
        Observable<PhoneVerificationResponseModel> checkPhoneVerification(@Header("access-token") String header,
                                                                          @Query("uid") Integer uid,
                                                                          @Query("device_id") String device_id,
                                                                          @Query("country_code") String country_code,
                                                                          @Query("pubid") String publisher_id);

        @GET("api/verifyPhoneNumber")
        Observable<PhoneVerificationResponseModel> verifyPhoneNumber(@Header("access-token") String header,
                                                                          @Query("uid") Integer uid,
                                                                          @Query("phone") String phone,
                                                                          @Query("c_code") String c_code,
                                                                          @Query("device_id") String device_id,
                                                                          @Query("pubid") String publisher_id,
                                                                          @Query("video_id") Integer video_id,
                                                                          @Query("country_code") String country_code);
        @POST("api/updateTransaction")
        Observable<SubscriptionTransactionModel> SubscriptionTransaction(@Header("access-token") String header,
                                                                         @Body JsonObject jsonObject);
        @GET("api/updatereceiptandroid")
        Observable<UpdateReceiptResponseModel> UpdateReceipt(@Header("access-token") String token,
                                                             @Query("user_id") Integer uid,
                                                             @Query("purchaseToken") String purchaseToken,
                                                             @Query("pubid") String publisherId,
                                                             @Query("productId") String productId);

        @POST("getPubID")
        Observable<PublisherModel> getPubID(@Query("app_key") String appKey,
                                            @Query("app_bundle_id") String bundleId);




        //===========================================================
        //===================== CATEGORY/SEARCH =====================

        @GET("api/GetCategories")
        Observable<CategoryListResponseModel> GetCategories(@Header("access-token") String header,
                                                            @Query("country_code") String country_code,
                                                            @Query("pubid") String publisher_id);

        @GET("api/GetThemes")
        Observable<CategoryModel> GetTheme(@Header("access-token") String header,
                                           @Query("country_code") String country_code,
                                           @Query("pubid") String publisher_id);

        @GET("api/GetvideoByCategory")
        Observable<VideoResponse> GetvideoByCategory(@Header("access-token") String header,
                                                     @Query("key") Integer key,
                                                     @Query("uid") Integer uid,
                                                     @Query("country_code") String country_code,
                                                     @Query("pubid") String publisher_id);

        @GET("api/Search")
        Observable<SearchResponseModel> Search(@Header("access-token") String header,
                                               @Query("key") String key,
                                               @Query("search_type") String search_type,
                                               @Query("uid") Integer uid,
                                               @Query("country_code") String country_code,
                                               @Query("pubid") String publisher_id);
        //@GET("api/SearchshowsUpdated2")
        @GET("api/searchShows")
        Observable<ShowListResponseModel> searchByshows (@Header("access-token") String header,
                                                         @Query("pubid") String publisher_id,
                                                         @Query("key") String key,
                                                         @Query("country_code") String country_code,
                                                         @Query("uid") Integer uid);

        @GET("GetHomeVideoUpdated2")
        Observable<CategorisHomeListResponseModel> GetHomeVideo (@Header("access-token") String header,
                                                                 @Query("country_code") String country_code,
                                                                 @Query("pubid") String publisher_id);

        //===========================================================
        //===================== LIKE/VIEW-COUNT =====================

        @GET("api/updateWatchlist")
        Observable<BasicResponse> updateWatchlist(@Header("access-token") String header,
                                                  @Query("vid") Integer vid,
                                                  @Query("uid") Integer uid,
                                                  @Query("country_code") String country_code,
                                                  @Query("pubid") String publisher_id);

        @GET("api/LikeVideo")
        Observable<LikeUnlikeModel> LikeVideo(@Header("access-token") String header,
                                              @Query("vid") Integer vid,
                                              @Query("uid") Integer uid,
                                              @Query("liked") Integer liked,
                                              @Query("pubid") String publisher_id);

        @GET("api/GetWatchlist")
        Observable<VideoResponse> GetWatchlist(@Header("access-token") String header,
                                               @Query("uid") Integer uid,
                                               @Query("country_code") String country_code,
                                               @Query("pubid") String publisher_id);

        @GET("api/GetSelectedVideoUpdated2")
        Observable<SelectedVideoResponseModel> getSelectedVideo (@Header("access-token") String header,
                                                                 @Query("pubid") String publisher_id,
                                                                 @Query("device_type") String device_type,
                                                                 @Query("country_code") String country_code,
                                                                 @Query("vid") Integer vid);
        //add to watchlist
        @GET("api/WatchlistShows")
        Observable<WatchListShowsResponseModel> addToWatchList(@Header("access-token") String header,
                                                               @Query("pubid") String pubid,
                                                               @Query("show_id") String show_id,
                                                               @Query("uid") int uid,
                                                               @Query("watchlistflag") int watchlistflag);
        @GET("api/LikedShows")
        Observable<WatchListShowsResponseModel> likeOrUnlikeShow(@Header("access-token") String header,
                                                                 @Query("pubid") String pubid,
                                                                 @Query("show_id") String show_id,
                                                                 @Query("uid") int uid,
                                                                 @Query("liked") int likeflag,
                                                                 @Query("version") String version);
        @GET("api/LikedShows")
        Observable<WatchListShowsResponseModel> dislikeShow(@Header("access-token") String header,
                                                            @Query("pubid") String pubid,
                                                            @Query("show_id") String show_id,
                                                            @Query("uid") int uid,
                                                            @Query("disliked") int dislikeflag,
                                                            @Query("version") String version);

        @GET("api/watchlistflag")
        Observable<WatchlistflagResponseModel> watchListFlagApi(@Header("access-token") String header,
                                                                    @Query("pubid") String pubid,
                                                                    @Query("show_id") String show_id,
                                                                    @Query("uid") int uid);
        @GET("api/likedflag")
        Observable<LikedFlagResponseModel> likedFlagApi (@Header("access-token") String header,
                                                         @Query("pubid") String pubid,
                                                         @Query("show_id") String show_id,
                                                         @Query("uid") int uid);

        @GET("api/getSubscribedVideos")
        Observable<GetPayperviewVideoListResponseModel> getPayperviewVideolist(@Header("access-token") String header,
                                                                               @Query("uid") int uid,
                                                                               @Query("device_id") String device_id,
                                                                               @Query("country_code") String country_code,
                                                                               @Query("pubid") String publisher_id);

        //===========================================================
    }

    public interface TokenService {

        @GET("android-authenticate")
        Observable<SessionTokenResponseModel> getSessionToken(@Query("uid") Integer uid,
                                                              @Query("app_key") String appKey,
                                                              @Query("app_bundle_id") String bundleId);


    }
}

