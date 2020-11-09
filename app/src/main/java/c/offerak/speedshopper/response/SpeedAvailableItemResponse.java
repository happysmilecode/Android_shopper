package c.offerak.speedshopper.response;

import java.util.List;

public class SpeedAvailableItemResponse {


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
        private List<UnpurchasedBean> unpurchased;
        private List<PurchasedBean> purchased;

        public List<UnpurchasedBean> getUnpurchased() {
            return unpurchased;
        }

        public void setUnpurchased(List<UnpurchasedBean> unpurchased) {
            this.unpurchased = unpurchased;
        }

        public List<PurchasedBean> getPurchased() {
            return purchased;
        }

        public void setPurchased(List<PurchasedBean> purchased) {
            this.purchased = purchased;
        }

        public static class UnpurchasedBean {
            /**
             * id : 3
             * item_id : 4
             * name : Meat
             * Quantity: 5
             * status : 0
             * location :
             */

            private String id;
            private int quantity;
            private String item_id;
            private String name;
            private String status;
            private String location;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getItem_id() {
                return item_id;
            }

            public void setItem_id(String item_id) {
                this.item_id = item_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }
        }

        public static class PurchasedBean {
            /**
             * id : 5
             * item_id : 25
             * name : Apple
             * Quantity:5
             * status : 1
             * location :
             */

            private String id;
            private String item_id;
            private String name;
            private int quantity;
            private String status;
            private String location;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getItem_id() {
                return item_id;
            }

            public void setItem_id(String item_id) {
                this.item_id = item_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }
        }
    }
}
