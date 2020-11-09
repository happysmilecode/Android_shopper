package c.offerak.speedshopper.response;

import java.util.List;

public class ProductListResponse {

    /**
     * status : 200
     * message : Speed Shopper Stores list
     * data : [{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"qwertyyy","price":"500","description":"asdf;lkfdgjk hgh iusbf jkbfilwb fdfddf","product_id":"1","product_image":"911d812535e6a54b4f57fc512ec37aeb.png","path":"http://192.168.1.137/SSTX/public/images/store/"},{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"sdfsd","price":"100","description":"fdgfdgfdgfdg","product_id":"2","product_image":"14ef64f490d4178614a3a785a92f24c2.jpeg","path":"http://192.168.1.137/SSTX/public/images/store/"},{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"poiuy","price":"600","description":"mv;lsm;lf;l","product_id":"3","product_image":"9900b11b463056435d6aa7bc0ad128b5.jpeg","path":"http://192.168.1.137/SSTX/public/images/store/"},{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"asa","price":"30","description":"xldkflsdkfs","product_id":"4","product_image":"39813a149ff1847653e533d16cba3061.jpg","path":"http://192.168.1.137/SSTX/public/images/store/"},{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"New P","price":"30","description":"Hello this is test prduct","product_id":"5","product_image":"afe3a9f1a4454ac8d916da9ee3849f4d.jpeg","path":"http://192.168.1.137/SSTX/public/images/store/"},{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"My product","price":"10","description":"This is my product","product_id":"6","product_image":"shop-placeholder.png","path":"http://192.168.1.137/SSTX/public/images/store/"},{"store_id":"1","store_name":"Hemant","address":"indore 452001","product_name":"asasa","price":"58","description":"askjaskjk","product_id":"7","product_image":"shop-placeholder.png","path":"http://192.168.1.137/SSTX/public/images/store/"}]
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
         * store_id : 1
         * store_name : Hemant
         * address : indore 452001
         * product_name : qwertyyy
         * price : 500
         * description : asdf;lkfdgjk hgh iusbf jkbfilwb fdfddf
         * product_id : 1
         * product_image : 911d812535e6a54b4f57fc512ec37aeb.png
         * path : http://192.168.1.137/SSTX/public/images/store/
         */

        private String store_id;
        private String store_name;
        private String address;
        private String product_name;
        private String price;
        private String discount;
        private String description;
        private String website;
        private String product_id;
        private String product_image;
        private String path;

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

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getProduct_image() {
            return product_image;
        }

        public void setProduct_image(String product_image) {
            this.product_image = product_image;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
