package c.offerak.speedshopper.modal;

import androidx.lifecycle.ViewModel;

public class PurchaseModel  extends ViewModel {

    //YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE
    private  final String GOOGLE_PLAY_CONSOL_LICENSE_KEY = "";

    //YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE
    private  final String GOOGLE_PLAY_CONSOL_SUBSCRIPTION_ID = "";

    //DEVELOPER PAYLOAD HERE
    private  final String DEVELOPER_PAYLOAD = "";

    //PRODUCT ID OR SKU
    private  final String PRODUCT_SKU = "store_logos";
    private  final String PRODUCT_MONTHLY_SKU = "auto_premium1";
    private  final String PRODUCT_THREEMONTH_SKU = "auto_premium3";
    private  final String PRODUCT_SIXMONTH_SKU = "auto_premium6";
    private  final String PRODUCT_YEARLY_SKU = "auto_premium";


    private String ispuduct_puchase = "ispuduct_puchase";


    public  String getGooglePlayConsolLicenseKey() {
        return GOOGLE_PLAY_CONSOL_LICENSE_KEY;
    }

    public  String getGooglePlayConsolSubscriptionId() {
        return GOOGLE_PLAY_CONSOL_SUBSCRIPTION_ID;
    }

    public  String getDeveloperPayload() {
        return DEVELOPER_PAYLOAD;
    }

    public String getPRODUCT_SKU() {
        return PRODUCT_SKU;
    }

    public String getPRODUCT_MONTHLY_SKU() {
        return PRODUCT_MONTHLY_SKU;
    }

    public String getPRODUCT_THREEMONTH_SKU() {
        return PRODUCT_THREEMONTH_SKU;
    }

    public String getPRODUCT_SIXMONTH_SKU() {
        return PRODUCT_SIXMONTH_SKU;
    }

    public String getPRODUCT_YEARLY_SKU() {
        return PRODUCT_YEARLY_SKU;
    }

    public String getIspuduct_puchase() {
        return ispuduct_puchase;
    }

}