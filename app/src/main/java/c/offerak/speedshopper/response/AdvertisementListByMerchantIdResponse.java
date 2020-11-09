package c.offerak.speedshopper.response;

import java.util.List;

public class AdvertisementListByMerchantIdResponse {

    private int status;
    private String message;
    private List<AdvertisementListByMerchantIdResponse.DataBean> data;

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

        private String store_id;
        private String store_name;
        private String address;
        private String adevertise_name;
        private String amount;
        private String description;
        private String adv_id;
        private String ad_image;
        private String path;
        private String name;
        private String image;
        private String item_id;
        private String adv_link;

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

        public String getAdv_link() {return adv_link;}

        public void setAdv_link(String adv_link) {this.adv_link = adv_link;}

        public String getAdevertise_name() {
            return adevertise_name;
        }

        public void setAdevertise_name(String adevertise_name) {
            this.adevertise_name = adevertise_name;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAdv_id() {
            return adv_id;
        }

        public void setAdv_id(String adv_id) {
            this.adv_id = adv_id;
        }

        public String getAd_image() {
            return ad_image;
        }

        public void setAd_image(String ad_image) {
            this.ad_image = ad_image;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }
    }
}
