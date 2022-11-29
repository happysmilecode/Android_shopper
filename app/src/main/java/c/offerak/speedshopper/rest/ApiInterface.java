package c.offerak.speedshopper.rest;

import c.offerak.speedshopper.response.AddNewStore;
import c.offerak.speedshopper.response.AdvertisementListByMerchantIdResponse;
import c.offerak.speedshopper.response.AisleLocationResponse;
import c.offerak.speedshopper.response.BuyListResponse;
import c.offerak.speedshopper.response.EZListsResponse;
import c.offerak.speedshopper.response.FAQ_Response;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.GetShoppingListResponse;
import c.offerak.speedshopper.response.ListItemResponse;
import c.offerak.speedshopper.response.LoginResponse;
import c.offerak.speedshopper.response.MarketListResponse;
import c.offerak.speedshopper.response.MessageListResponse;
import c.offerak.speedshopper.response.NotificationListRespose;
import c.offerak.speedshopper.response.ProductListResponse;
import c.offerak.speedshopper.response.ProfileResponse;
import c.offerak.speedshopper.response.ShoppingListAddItemResponse;
import c.offerak.speedshopper.response.SignupResponse;
import c.offerak.speedshopper.response.SpeedAvailableItemResponse;
import c.offerak.speedshopper.response.StoreImageListResponse;
import c.offerak.speedshopper.response.StoreListResponse;
import c.offerak.speedshopper.response.TransactionHistoryResponse;
import c.offerak.speedshopper.response.UpdateProfileResponse;
import c.offerak.speedshopper.response.WalletResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    @POST(Constants.LOGIN_URL)
    @FormUrlEncoded
    Call<LoginResponse> login(@Field(Constants.EMAIL) String email,
                              @Field(Constants.PASSWORD) String password,
                              @Field(Constants.DEVICE_ID) String deviceId,
                              @Field(Constants.DEVICE_TYPE) String deviceType,
                              @Field(Constants.ONESIGNAL_ID) String push_id);

    @POST(Constants.LOGIN_URL_GUEST)
    @FormUrlEncoded
    Call<LoginResponse> loginGuest(@Field(Constants.DEVICE_ID) String deviceId,
                                    @Field(Constants.DEVICE_TYPE) String deviceType,
                                    @Field(Constants.ONESIGNAL_ID) String push_id);

    @POST(Constants.REGISTER_URL)
    @FormUrlEncoded
    Call<SignupResponse> register(@Field(Constants.NAME) String name,
                                  @Field(Constants.EMAIL) String email,
                                  @Field(Constants.CONTACT) String contact,
                                  @Field(Constants.PASSWORD) String password);

    @POST(Constants.SENDMESSAGE_URL)
    @FormUrlEncoded
    Call<SignupResponse> sendEmail(@Field(Constants.NAME) String name,
                                @Field(Constants.EMAIL) String email,
                                @Field(Constants.TOKEN) String token);

    @POST(Constants.LOGOUT_URL)
    @FormUrlEncoded
    Call<GetResponse> logout(@Field(Constants.TOKEN) String token);

    @POST(Constants.FORGET_URL)
    @FormUrlEncoded
    Call<GetResponse> forget(@Field(Constants.EMAIL) String email);

    @POST(Constants.SMSCODE_URL)
    @FormUrlEncoded
    Call<SignupResponse> sendSMS(@Field(Constants.MOBILE) String phone,
                              @Field(Constants.TOKEN) String token);

    @GET(Constants.GET_USER_URL)
    Call<ProfileResponse> getProfile(@Path(Constants.TOKEN) String token);

    @POST(Constants.GET_SSTX_EARNED_URL)
    @FormUrlEncoded
    Call<GetResponse> getSSTXEarned(@Path(Constants.TOKEN) String token,
                                        @Field(Constants.STORE_ID) String storeId);

    @POST(Constants.GET_STORES_URL)
    @FormUrlEncoded
    Call<StoreListResponse> getStores(@Path(Constants.TOKEN) String token,
                                      @Field(Constants.LATITUDE) String lat,
                                      @Field(Constants.LONGITUDE) String lng,
                                      @Field(Constants.DISTANCE) String distance);

    @POST(Constants.STORE_ITEMS_URL)
    @FormUrlEncoded
    Call<ListItemResponse> storeItems(@Path(Constants.TOKEN) String token,
                                      @Field(Constants.STORE_ID) String storeId);

    @POST(Constants.SHOPPING_LIST_ITEM_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> shoppingListItem(@Path(Constants.TOKEN) String token,
                                                      @Field(Constants.LIST_ID) String listId);

    @POST(Constants.SHOPPING_LIST_URL)
    Call<GetShoppingListResponse> getShoppingList(@Path(Constants.TOKEN) String token);

    @POST(Constants.ADD_SHOPPING_LIST_URL)
    @FormUrlEncoded
    Call<GetResponse> addShoppingList(@Path(Constants.TOKEN) String token,
                                      @Field(Constants.STORE_ID) String storeId,
                                      @Field(Constants.NAME) String listName,
                                      @Field(Constants.IMAGE) String imageName

    );

    @POST(Constants.SHARE_TOKEN_UPDATE_URL)
    @FormUrlEncoded
    Call<GetResponse>updateShareStatus(@Path(Constants.TOKEN) String token, @Field(Constants.SHARE_TOKEN) String shareToken);

    @POST(Constants.SAVE_SHARE_TOKEN_URL)
    @FormUrlEncoded
    Call<GetResponse>saveShareToken(@Path(Constants.TOKEN) String token,
                                    @Field(Constants.SHARE_TOKEN) String shareToken,
                                    @Field(Constants.LIST_ID) String list_id);

    @POST(Constants.ADD_ITEM_TO_SHOPPING_LIST_URL)
    @FormUrlEncoded
    Call<ShoppingListAddItemResponse> addItemToShoppingList(@Path(Constants.TOKEN) String token,
                                                            @Field(Constants.LIST_ID) String listId,
                                                            @Field(Constants.STORE_ID) String storeId,
                                                            @Field(Constants.ITEM) String itemName);

    @POST(Constants.REMOVE_SHOPPING_LIST_URL)
    @FormUrlEncoded
    Call<GetResponse> removeShoppingList(@Path(Constants.TOKEN) String token,
                                         @Field(Constants.LIST_ID) String listId);

    @POST(Constants.REMOVE_ALL_ITEM_URL)
    @FormUrlEncoded
    Call<GetResponse> removeAllItem(@Path(Constants.TOKEN) String token,
                                    @Field(Constants.LIST_ID) String listId);


    @POST(Constants.REMOVE_CHECKED_ITEM_URL)
    @FormUrlEncoded
    Call<GetResponse> removeCheckedItem(@Path(Constants.TOKEN) String token,
                                    @Field(Constants.LIST_ID) String listId);

    @POST(Constants.REMOVE_ITEM_IMAGE_URL)
    @FormUrlEncoded
    Call<GetResponse> removeItemImage(@Path(Constants.TOKEN) String token,
                                      @Field(Constants.ITEM_ID) String listId,
                                      @Field(Constants.IMAGE) String image);


    @POST(Constants.PURCHASE_ITEM_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> purchaseItem(@Path(Constants.TOKEN) String token,
                                                  @Field(Constants.ITEM_ID) String itemId,
                                                  @Field(Constants.PURCHASE) String purchase,
                                                  @Field(Constants.LIST_ID) String listId);
    @POST(Constants.UPDATE_ITEM_QUANTITY_URL)
    @Multipart
    Call<GetResponse> updateItemQuantity(@Path(Constants.TOKEN) String token,
                                         @Part(Constants.ITEM_ID) RequestBody itemId,
                                         @Part(Constants.QUANTITY) RequestBody quantity,
                                         @Part(Constants.UNIT_PRICE) RequestBody price,
                                         @Part("id") RequestBody id,
                                         @Part MultipartBody.Part file);

    @POST(Constants.UPDATE_URL)
    @Multipart
    Call<UpdateProfileResponse> update(@Part(Constants.TOKEN) RequestBody token,
                                       @Part(Constants.NAME) RequestBody name,
                                       @Part MultipartBody.Part file);

    @POST(Constants.DELETE_URL)
    @Multipart
    Call<GetResponse> update(@Part(Constants.TOKEN) RequestBody token);

    @POST(Constants.IMPORT_ITEM_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> importItem(@Path(Constants.TOKEN) String token,
                                                     @Field(Constants.IMPORT_SHOPPING_LIST_ID) String importId,
                                                     @Field(Constants.CUR_SHOPPING_LIST_ID) String curId);

    @POST(Constants.ADD_ITEM_LOCATION_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> addItemLocation(@Path(Constants.TOKEN) String token,
                                                     @Field(Constants.ITEM_ID) String itemId,
                                                     @Field(Constants.LATITUDE) String latitude,
                                                     @Field(Constants.STORE_ID) String storeId,
                                                     @Field(Constants.LOCATION) String location,
                                                     @Field(Constants.LONGITUDE) String longitude,
                                                     @Field(Constants.LIST_ID) String list_id);

    @POST(Constants.ADD_ITEM_LOCATION_PRO_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> addItemLocationPro(@Path(Constants.TOKEN) String token,
                                                     @Field(Constants.ITEM_ID) String itemId,
                                                     @Field(Constants.LATITUDE) String latitude,
                                                     @Field(Constants.STORE_ID) String storeId,
                                                     @Field(Constants.LOCATION) String location,
                                                     @Field(Constants.LONGITUDE) String longitude,
                                                     @Field(Constants.LIST_ID) String list_id);

    @POST(Constants.UPDATE_ITEM_LOCATION_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> updateItemLocation(@Path(Constants.TOKEN) String token,
                                                     @Field(Constants.ITEM_ID) String itemId,
                                                     @Field(Constants.LATITUDE) String latitude,
                                                     @Field(Constants.STORE_ID) String storeId,
                                                     @Field(Constants.LOCATION) String location,
                                                     @Field(Constants.LONGITUDE) String longitude,
                                                     @Field(Constants.LIST_ID) String list_id);

    @POST(Constants.UPDATE_ITEM_LOCATION_PRO_URL)
    @FormUrlEncoded
    Call<SpeedAvailableItemResponse> updateItemLocationPro(@Path(Constants.TOKEN) String token,
                                                        @Field(Constants.ITEM_ID) String itemId,
                                                        @Field(Constants.LATITUDE) String latitude,
                                                        @Field(Constants.STORE_ID) String storeId,
                                                        @Field(Constants.LOCATION) String location,
                                                        @Field(Constants.LONGITUDE) String longitude,
                                                        @Field(Constants.LIST_ID) String list_id);

    @POST(Constants.UPDATE_SHOPPING_LIST_NAME_URL)
    @FormUrlEncoded
    Call<GetResponse> updateShoppingListName(@Path(Constants.TOKEN) String token,
                                             @Field(Constants.LIST_ID) String listId,
                                             @Field(Constants.NAME) String name);

    @POST(Constants.UPDATE_SHOPPING_LIST_LOGO_URL)
    @FormUrlEncoded
    Call<GetResponse> updateShoppingListImage(@Path(Constants.TOKEN) String token,
                                              @Field(Constants.LIST_ID) String listId,
                                              @Field(Constants.IMAGE) String imageId);

    @POST(Constants.FACEBOOK_LOGIN_URL)
    @FormUrlEncoded
    Call<LoginResponse> facebookLogin(@Field(Constants.NAME) String name,
                                      @Field(Constants.EMAIL) String email,
                                      @Field(Constants.FACEBOOK_ID) String facebookId,
                                      @Field(Constants.LOGIN_TYPE) String login_type,
                                      @Field(Constants.DEVICE_ID) String deviceId,
                                      @Field(Constants.DEVICE_TYPE) String deviceType,
                                      @Field(Constants.ONESIGNAL_ID) String push_id);

    @POST(Constants.ADD_URL)
    @FormUrlEncoded
    Call<AddNewStore> addNewStore(@Path(Constants.TOKEN) String token,
                                  @Field(Constants.NAME) String name,
                                  @Field(Constants.EMAIL) String email,
                                  @Field(Constants.CONTACT) String contact,
                                  @Field(Constants.ADDRESS) String address,
                                  @Field(Constants.LATITUDE) String latitude,
                                  @Field(Constants.LONGITUDE) String longitude,
                                  @Field(Constants.USER_LATITUDE) String user_latitude,
                                  @Field(Constants.USER_LONGITUDE) String user_longitude,
                                  @Field(Constants.CITY) String city,
                                  @Field(Constants.STATE) String state,
                                  @Field(Constants.COUNTRY) String country,
                                  @Field(Constants.ZIPCODE) String postal_code);

    @GET(Constants.LOCATIONS_URL)
    Call<AisleLocationResponse> getAisleLocations(@Path(Constants.TOKEN) String token);

    @GET(Constants.GET_WALLET_URL)
    Call<WalletResponse> getWallet(@Path(Constants.TOKEN) String token);

    @POST(Constants.CONTACT_URL)
    @FormUrlEncoded
    Call<GetResponse> contact(@Path(Constants.TOKEN) String token,
                              @Field(Constants.MESSAGE) String message);

    @POST(Constants.GET_SPEED_SHOPPER_MARKET_URL)
    @FormUrlEncoded
    Call<MarketListResponse> getSpeedShopperMarket(@Path(Constants.TOKEN) String token,
                                                   @Field(Constants.PAGE) String page,
                                                   @Field(Constants.KEY) String key);

    @POST(Constants.GET_EZ_LISTS_URL)
    @FormUrlEncoded
    Call<EZListsResponse> getEZLists(@Path(Constants.TOKEN) String token,
                                     @Field(Constants.PAGE) String page,
                                     @Field(Constants.KEY) String key);

    @POST(Constants.ADD_ITEM_TO_MY_LISTS)
    @FormUrlEncoded
    Call<GetResponse> addItemToMyLists(@Path(Constants.TOKEN) String token,
                                                       @Field(Constants.EZ_LIST_ID) String listId,
                                                       @Field(Constants.MY_LIST_ID) String storeId);

    @POST(Constants.GET_PRODUCT_BY_MERCHANT_ID_URL)
    @FormUrlEncoded
    Call<ProductListResponse> getProductByMerchantID(@Path(Constants.TOKEN) String token,
                                                     @Field(Constants.PAGE) String page,
                                                     @Field(Constants.STORE_ID) String storeID,
                                                     @Field(Constants.KEY) String key);

    @GET(Constants.FAQ_URL)
    Call<FAQ_Response> faq(@Path(Constants.TOKEN) String token);

    @POST(Constants.BUY_URL)
    @FormUrlEncoded
    Call<GetResponse> getBuyAPI(@Path(Constants.TOKEN) String token,
                                @Field(Constants.USER_ID) String user_id,
                                @Field(Constants.MERCHANT_ID) String merchant_id,
                                @Field(Constants.PRODUCT_ID) String product_id);

    @GET(Constants.TRNSACTION_HISTORY_URL)
    Call<TransactionHistoryResponse> getTransactionHistoryAPI(@Path(Constants.TOKEN) String token);

    @GET(Constants.BUY_LIST_URL)
    Call<BuyListResponse> getBuyListAPI(@Path(Constants.TOKEN) String token);

    @POST(Constants.ADVERTISEMENT_LIST_BY_STORE_ID_URL)
    @FormUrlEncoded
    Call<AdvertisementListByMerchantIdResponse> getAdvertisementListByStoreIdAPI(@Path(Constants.TOKEN) String token,
                                                                                 @Field(Constants.STORE_ID) String store_id);


    @POST(Constants.ADVERTISEMENT_INCREASE_CLICK_COUNT_URL)
    @FormUrlEncoded
    Call<GetResponse> advertisementIncreaseClickCountAPI(@Path(Constants.TOKEN) String token,
                                                                                 @Field(Constants.ADS_ID) String ads_id);

    @GET(Constants.NOTIFICATIONS_URL + Constants.NOTIFICATION_PAGE_NUMBER_URL)
    Call<NotificationListRespose> getNotificationsList(@Path(Constants.TOKEN) String token,
                                                       @Path(Constants.PAGE_NUMBER) String page_number);

    @GET(Constants.MESSAGES_URL + Constants.MESSAGES_PAGE_NUMBER_URL)
    Call<MessageListResponse> getMessagesList(@Path(Constants.TOKEN) String token,
                                                   @Path(Constants.PAGE_NUMBER) String page_number);

    @GET(Constants.STOREIMAGE_URL)
    Call<StoreImageListResponse> getLogosList(@Path(Constants.TOKEN) String token);

    @POST(Constants.MOBILE_VERIFY)
    @FormUrlEncoded
    Call<LoginResponse> verify_mobile(@Field(Constants.TOKEN) String token,
                              @Field(Constants.DEVICE_ID) String deviceId,
                              @Field(Constants.DEVICE_TYPE) String deviceType,
                              @Field(Constants.ONESIGNAL_ID) String push_id);

    @POST(Constants.GETREWARD_URL)
    @FormUrlEncoded
    Call<GetResponse> getReward(@Field(Constants.TOKEN) String token,
                                      @Field(Constants.PREMIUM) String key);

}

