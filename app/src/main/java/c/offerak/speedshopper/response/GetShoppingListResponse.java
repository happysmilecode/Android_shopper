package c.offerak.speedshopper.response;

import java.util.List;

public class GetShoppingListResponse {


    /**
     * status : 200
     * message : Shopping list
     * data : [{"id":"2","name":"May Kirana","store_name":"Paytm"},{"id":"1","name":"May Kirana","store_name":"Paytm"}]
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
         * id : 2
         * name : May Kirana
         * store_name : Paytm
         */

        private String id;
        private String name;
        private String store_name;
        private String store_id;
        private String address;
        private String item_count;

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStore_name() {
            return store_name;
        }

        public String getItemCount() {return item_count; }

        public void setItemCount(String itemCount) { this.item_count = itemCount; }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }
    }
}
