package c.offerak.speedshopper.response;

import java.util.List;

public class EZListsResponse {
    /**
     * status : 200
     * message : Speed Shopping List Stores list
     * data : [{"store_id":"3","store_name":"Neetesh","address":"indore"},{"store_id":"2","store_name":"CP Provisional Store","address":"asaljkdjkfhk"},{"store_id":"1","store_name":"Hemant","address":"indore 452001"}]
     */

    private int status;
    private String message;
    private List<EZListsResponse.DataBean> data;

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

    public List<EZListsResponse.DataBean> getData() {
        return data;
    }

    public void setData(List<EZListsResponse.DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * store_id : 3
         * store_name : Neetesh
         * address : indore
         */

        private String id;
        private String title;
        private String description;
        private String image;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
