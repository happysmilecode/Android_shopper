package c.offerak.speedshopper.response;

import java.util.List;

public class StoreImageListResponse {
    private int status;
    private String message;
    private List<StoreImageListResponse.DataBean> data;

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

    public List<StoreImageListResponse.DataBean> getData() {
        return data;
    }

    public void setData(List<StoreImageListResponse.DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String image_name;
        private String image_id;
        private String created_at;

        public String getName() {
            return image_name;
        }

        public void setName(String name) {
            this.image_name = name;
        }

        public String getImage_id() {
            return image_id;
        }

        public void setImage_id(String image_id) {
            this.image_id = image_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

    }
}
