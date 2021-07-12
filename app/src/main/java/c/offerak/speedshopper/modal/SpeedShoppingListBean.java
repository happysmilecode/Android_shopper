package c.offerak.speedshopper.modal;

public class SpeedShoppingListBean {

    private int index;
    private String id;
    private String item_id;
    private String name;
    private String image;
    private int quantity;
    private float unit_price;
    private String status;
    private String location;
    private String advertStatus;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public int getQuantity() {return quantity;}

    public void setQuantity(int quantity) {this.quantity = quantity;}

    public float getUnitPrice() {return unit_price;}

    public void setUnitPrice(float price) {this.unit_price = price;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getAdvertStatus() {
        return advertStatus;
    }

    public void setAdvertStatus(String advertStatus) {
        this.advertStatus = advertStatus;
    }
}
