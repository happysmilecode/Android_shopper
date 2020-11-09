package c.offerak.speedshopper.response;

import java.util.List;

public class AisleLocationResponse {


    /**
     * status : 200
     * message : Location List
     * data : [{"id":"1","name":"A1"},{"id":"2","name":"A2"},{"id":"3","name":"A3"},{"id":"4","name":"B1"},{"id":"5","name":"B2"},{"id":"6","name":"B3"},{"id":"7","name":"C1"},{"id":"8","name":"C2"},{"id":"9","name":"C3"}]
     */

    private int status;
    private String message;
    private List<String> data;
//    private List<DataBean> data;

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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    //    public List<DataBean> getData() {
//        return data;
//    }
//
//    public void setData(List<DataBean> data) {
//        this.data = data;
//    }

    public static class DataBean {
        /**
         * id : 1
         * name : A1
         */

        private String id;
        private String name;

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
    }
}
