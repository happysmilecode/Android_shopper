package c.offerak.speedshopper.modal;

public class Message {
    public String getDate_time() {
        return date_time;
    }

    public Message(String date_time, String title, String msg) {
        this.date_time = date_time;
        this.title = title;
        this.msg = msg;
    }

    public void setDate_time(String date_time) {

        this.date_time = date_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private  String date_time, title, msg;
}
