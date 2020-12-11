package c.offerak.speedshopper.response;

import java.util.List;

public class MessageListResponse {
    private int status;
    private String message;
    private List<MessageListResponse.DataBean> data;

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

    public List<MessageListResponse.DataBean> getData() {
        return data;
    }

    public void setData(List<MessageListResponse.DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String title;
        private String message;
        private String created_at;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
