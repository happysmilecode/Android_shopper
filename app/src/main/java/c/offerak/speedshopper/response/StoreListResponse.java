package c.offerak.speedshopper.response;

import java.util.List;

public class StoreListResponse {


    /**
     * status : 200
     * message : Stores list
     * data : [{"id":"4","name":"Vishal Mega Mart","logo":"35bd088700c1e5b693f2efb84771c1d3.jpg","address":"Mhow Naka Circle, Balda Colony, Indore, Madhya Pradesh","latitude":"22.705446","longitude":"75.843617","distance":"1.90","path":"http://192.168.1.120/SSTX/public/images/store/"},{"id":"3","name":"Big Bazaar","logo":"9498439eb8d81529671e95d607a10763.jpg","address":"Rajendra Nagar, Indore, Madhya Pradesh, India","latitude":"22.670918","longitude":"75.827524","distance":"2.74","path":"http://192.168.1.120/SSTX/public/images/store/"},{"id":"2","name":"Paytm","logo":"7a0fbfbd9fa1e4a005de5bbf304c6bdd.jpeg","address":"Palasia Square, Manorama Ganj, Indore, Madhya Pradesh","latitude":"22.723780","longitude":"75.886690","distance":"2.80","path":"http://192.168.1.120/SSTX/public/images/store/"},{"id":"1","name":"D-Mart New","logo":"35b4f648ccd21a869f1cd90e7c4cb1ce.jpeg","address":"Bhanwar Kuwa, Indore, Madhya Pradesh, India","latitude":"22.687821","longitude":"75.866467","distance":"0.00","path":"http://192.168.1.120/SSTX/public/images/store/"}]
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
         * id : 4
         * name : Vishal Mega Mart
         * logo : 35bd088700c1e5b693f2efb84771c1d3.jpg
         * address : Mhow Naka Circle, Balda Colony, Indore, Madhya Pradesh
         * latitude : 22.705446
         * longitude : 75.843617
         * distance : 1.90
         * path : http://192.168.1.120/SSTX/public/images/store/
         */

        private String id;
        private String name;
        private String logo;
        private String address;
        private String latitude;
        private String longitude;
        private String distance;
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

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
