package c.offerak.speedshopper.response;

public class AddNewStore {

    /**
     * status : 200
     * message : Store added successfully.
     * data : {"id":"8","name":"Ritika Store","logo":"","address":"Tower 61, In front of mata gujari  girls college, indore","latitude":"22.251230","longitude":"75.759870","path":"http://192.168.1.120/SSTX/public/images/store/"}
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
        /**
         * id : 8
         * name : Ritika Store
         * logo :
         * address : Tower 61, In front of mata gujari  girls college, indore
         * latitude : 22.251230
         * longitude : 75.759870
         * path : http://192.168.1.120/SSTX/public/images/store/
         */

        private String id;
        private String name;
        private String logo;
        private String address;
        private String latitude;
        private String longitude;
        private String path;

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

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
