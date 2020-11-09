package c.offerak.speedshopper.modal;

public class ShoppingListBean {

    private String shopItem;
    private String id;
    private String storeName;
    private String shoppingListName;
    private String address;
    private String storeId;
    private String itemCount;

    public String getStoreId() {

        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    public String getShoppingListName() {
        return shoppingListName;
    }

    public void setShoppingListName(String shoppingListName) {
        this.shoppingListName = shoppingListName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopItem() {
        return shopItem;
    }

    public void setShopItem(String shopItem) {
        this.shopItem = shopItem;
    }

    public String getItemCount() { return itemCount; }

    public void setItemCount(String itemCount) { this.itemCount = itemCount; }
}
