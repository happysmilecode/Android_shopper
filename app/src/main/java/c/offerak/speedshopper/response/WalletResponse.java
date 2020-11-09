package c.offerak.speedshopper.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletResponse {

    /**
     * status : 200
     * message : Wallet Balance
     * data : {"balance":"100"}
     */

    private int status;
    private String message;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {


        public String getPrivate_address() {
            return private_address;
        }

        public void setPrivate_address(String private_address) {
            this.private_address = private_address;
        }

        /**

         * balance : 100
         */

        private String balance;
        private String address;
        private String ethToken;
        private String ethBalance;
        private String private_address;


        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEthToken() {
            return ethToken;
        }

        public void setEthToken(String ethToken) {
            this.ethToken = ethToken;
        }

        public String getEthBalance() {
            return ethBalance;
        }

        public void setEthBalance(String ethBalance) {
            this.ethBalance = ethBalance;
        }
    }
}
