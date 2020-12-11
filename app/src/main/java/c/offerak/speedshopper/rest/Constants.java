package c.offerak.speedshopper.rest;

public class Constants {

    //server
//    public static final String BASE_URL ="http://localhost/api/";
    public static final String BASE_URL ="https://www.speedshopperapp.com/app/api/";
//    public static final String BASE_URL ="https://www.speedshopperapp.com/app_sandbox/api/";

    //local
//    public static final String BASE_URL = "http://192.168.1.137/SSTX/api/";
//    public static final String BASE_URL = "http://192.168.1.120/SSTX/api/";

    public static final String LOGIN_URL = "account/login";
    public static final String LOGIN_URL_GUEST = "account/guestregister";
    public static final String FACEBOOK_LOGIN_URL = "account/facebookLogin";
    public static final String REGISTER_URL = "account/register";
    public static final String LOGOUT_URL = "account/logout";
    public static final String FORGET_URL = "account/forget";
    public static final String UPDATE_URL = "account/update";
    public static final String GET_USER_URL = "account/getUser/{token}";
    public static final String GET_SSTX_EARNED_URL = "shopping/getSSTXValue/{token}";
    public static final String GET_STORES_URL = "store/getStores/{token}";
    public static final String STORE_ITEMS_URL = "store/storeItems/{token}";
    public static final String SHOPPING_LIST_ITEM_URL = "shopping/shoppingListItem/{token}";
    public static final String SHOPPING_LIST_URL = "shopping/shoppingList/{token}";
    public static final String ADD_SHOPPING_LIST_URL = "shopping/addShoppingList/{token}";

    public static final String ADD_ITEM_TO_SHOPPING_LIST_URL = "shopping/addItemToShoppingListNew/{token}";
    public static final String REMOVE_SHOPPING_LIST_URL = "shopping/removeShoppingList/{token}";
    public static final String REMOVE_ALL_ITEM_URL = "shopping/removeAllItem/{token}";
    public static final String REMOVE_CHECKED_ITEM_URL = "shopping/removeCheckedItem/{token}";
    public static final String PURCHASE_ITEM_URL = "shopping/purchaseItemNew/{token}";
    public static final String UPDATE_ITEM_QUANTITY_URL = "shopping/updateItemQuantity/{token}";
    public static final String ADD_ITEM_LOCATION_URL = "shopping/addItemLocation/{token}";
    public static final String IMPORT_ITEM_URL = "shopping/importItem/{token}";
    public static final String UPDATE_SHOPPING_LIST_NAME_URL = "shopping/updateShoppingListName/{token}";
    public static final String UPDATE_SHOPPING_LIST_LOGO_URL = "shopping/updateShoppingListLogo/{token}";
    public static final String ADD_URL = "store/add/{token}";
    public static final String LOCATIONS_URL = "shopping/locations/{token}";
    public static final String GET_WALLET_URL = "wallet/getWallet/{token}";
    public static final String CONTACT_URL = "common/contact/{token}";
    public static final String GET_SPEED_SHOPPER_MARKET_URL = "speed_shopper_market/getSpeedShopperMarket/{token}";
    public static final String GET_PRODUCT_BY_MERCHANT_ID_URL = "speed_shopper_market/getProductByMerchantID/{token}";
    public static final String FAQ_URL = "common/faq/{token}";
    public static final String BUY_URL = "speed_shopper_market/buy/{token}";
    public static final String TRNSACTION_HISTORY_URL = "transaction/transaction_history/{token}";
    public static final String BUY_LIST_URL = "speed_shopper_market/buy_list/{token}";
    public static final String ADVERTISEMENT_LIST_BY_MERCHANT_ID_URL = "speed_shopper_market/advertisement_list_by_merchant_id/{token}";
    public static final String ADVERTISEMENT_LIST_BY_STORE_ID_URL = "speed_shopper_market/advertisement_list_by_store_id/{token}";
    public static final String ADVERTISEMENT_INCREASE_CLICK_COUNT_URL = "speed_shopper_market/advertisement_increase_click_count/{token}";
    public static final String NOTIFICATIONS_URL = "common/notifications/{token}";
    public static final String NOTIFICATION_PAGE_NUMBER_URL = "/{page_number}";
    public static final String MESSAGES_URL = "common/messages/{token}";
    public static final String MESSAGES_PAGE_NUMBER_URL = "/{page_number}";

    public static final String PRIVACY_POLICY_URL="https://www.speedshopperapp.com/app/privacy-policies";
    public static final String TERMS_CONDITION_URL="https://www.speedshopperapp.com/app/terms-and-conditions";

    public static final String LOGIN_TYPE = "login_type";
    public static String HOME_LIST_FRAGMENT = "home_list_fragment";
    public static final String FIREBASE_TOKEN = "firebase_token";
    public static final String MESSAGE = "message";
    public static final String TOKEN = "token";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_ID = "device_id";
    public static final String FACEBOOK_ID = "facebook_id";
    public static final String NAME = "name";
    public static final String CONTACT = "contact";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String STORE_ID = "store_id";
    public static final String LIST_ID = "list_id";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM = "item";
    public static final String PURCHASE = "purchase";
    public static final String QUANTITY = "quantity";
    public static final String LOCATION = "location";
    public static final String ADDRESS = "address";

    public static final String ADS_ID = "ads_id";
    public static final String USER_LATITUDE = "user_latitude";
    public static final String USER_LONGITUDE = "user_longitude";
    public static final String PAGE = "page";
    public static final String PAGE_NUMBER = "page_number";
    public static final String USER_ID = "user_id";
    public static final String MERCHANT_ID = "merchant_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String KEY = "key";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String ZIPCODE = "zipcode";
    public static final String SDCARD_FOLDER_PATH = "";
    public static final String DISTANCE = "distance";
    public static final String EVENT_CHECK = "event_check";
    public static final String IMPORT_SHOPPING_LIST_ID = "import_shopping_list_id";
    public static final String CUR_SHOPPING_LIST_ID = "cur_shopping_list_id";

    public static final String IMAGE = "image";
    public static final Boolean purchased = false;
}
