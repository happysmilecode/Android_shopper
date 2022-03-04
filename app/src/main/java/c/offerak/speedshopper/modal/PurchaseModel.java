package c.offerak.speedshopper.modal;

import androidx.lifecycle.ViewModel;

public class PurchaseModel  extends ViewModel {

    //YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE
    private  final String GOOGLE_PLAY_CONSOL_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtieMtLK24wfUQurVJANIF2dsXOWyqYPjMO190airMwn1JMAqVLx76GF0dvSRnZCnvwZtje/IsqhC2ak8uV6D7nSGSKomtz+n7yFlzxFcBDMzP7s2MYTaEaNyYPKQS69Ek69t8Qo9Y9MyaDXCcE8H8KKhsQAdTOS7wBlxa333SH4wvFuwxXXO9wzwmAVHc2BKzS1ecOMQ3WKi+Knee8sz00DUXwg9RyFS+mEuZLbCtnE8ervxnjVHgPDBLVs/JvDvteFvJBCPa8uxg+udTxhFdRQ+Rl/qhC2E8sJLeVWOuME0pcW+bEwPej10BH77Bv3G/f4VgLwjwxkt6ku4hW+OcwIDAQAB";

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