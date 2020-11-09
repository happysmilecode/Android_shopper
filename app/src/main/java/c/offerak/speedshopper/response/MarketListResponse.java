package c.offerak.speedshopper.response;

import java.util.List;

public class MarketListResponse {

    /**
     * status : 200
     * message : Speed Shopper Stores list
     * data : [{"store_id":"3","store_name":"Neetesh","address":"indore"},{"store_id":"2","store_name":"CP Provisional Store","address":"asaljkdjkfhk"},{"store_id":"1","store_name":"Hemant","address":"indore 452001"}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * store_id : 3
         * store_name : Neetesh
         * address : indore
         */

        private String store_id;
        private String store_name;
        private String address;
        private String profile_pic;

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }
    }
}
