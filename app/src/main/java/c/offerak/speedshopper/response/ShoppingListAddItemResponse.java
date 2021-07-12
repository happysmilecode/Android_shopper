package c.offerak.speedshopper.response;

import java.util.List;

public class ShoppingListAddItemResponse {


    /**
     * status : 200
     * message : Shopping List Item
     * data : {"unpurchased":[{"id":"3","item_id":"4","name":"Meat","status":"0","location":""},{"id":"2","item_id":"1","name":"Milk","status":"0","location":""},{"id":"1","item_id":"17","name":"Paav","status":"0","location":""},{"id":"4","item_id":"24","name":"Sugar","status":"0","location":""}],"purchased":[{"id":"5","item_id":"25","name":"Apple","status":"1","location":""}]}
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
        private String id;
        private String item_id;
        private String name;
        private String image;
        private int quantity;
        private float unit_price;
        private String status;
        private String location;
        public String getId() {
            return id;
        }
        public String getItem_id() {
            return item_id;
        }
        public String getName() {
            return name;
        }
        public String getImage() {
            return image;
        }
        public int getQuantity() {
            return quantity;
        }
        public String getStatus() {
            return status;
        }
        public String getLocation() {
            return location;
        }
        public float getUnit_price() {
            return unit_price;
        }



    }
}
