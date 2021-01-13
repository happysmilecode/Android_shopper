package c.offerak.speedshopper.modal;


public class Images {

    private  String date_time, name, id;
    public Images(String date_time,  String name, String id) {
        this.date_time = date_time;
        this.name = name;
        this.id = id;
    }

    public String getDate_time() {
        return date_time;
    }
    public void setDate_time(String date_time) {

        this.date_time = date_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }


}

