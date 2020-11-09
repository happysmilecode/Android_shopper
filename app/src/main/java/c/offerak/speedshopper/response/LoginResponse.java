package c.offerak.speedshopper.response;

public class LoginResponse {


    /**
     * status : 200
     * message : Login successfully
     * data : {"id":"1","name":"Hemant Kumar","email":"hemant@infograins.com","email_verify":"1","contact":"7894561235","contact_verify":"1","token":"201804181725110000005ad7321f87ddbKVnu2MlQ8ZSTbf759Rc1yHWadGhrmEvi","device_id":"1234","device_type":"android","profile_pic":"5d48826ccbb167dc67c48d059ea3c362.jpg","path":"https://infograins.in/INFO01/SSTX/public/images/profile/"}
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
         * id : 1
         * name : Hemant Kumar
         * email : hemant@infograins.com
         * email_verify : 1
         * contact : 7894561235
         * contact_verify : 1
         * token : 201804181725110000005ad7321f87ddbKVnu2MlQ8ZSTbf759Rc1yHWadGhrmEvi
         * device_id : 1234
         * device_type : android
         * profile_pic : 5d48826ccbb167dc67c48d059ea3c362.jpg
         * path : https://infograins.in/INFO01/SSTX/public/images/profile/
         */

        private String id;
        private String name;
        private String email;
        private String email_verify;
        private String contact;
        private String contact_verify;
        private String token;
        private String device_id;
        private String device_type;
        private String profile_pic;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail_verify() {
            return email_verify;
        }

        public void setEmail_verify(String email_verify) {
            this.email_verify = email_verify;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getContact_verify() {
            return contact_verify;
        }

        public void setContact_verify(String contact_verify) {
            this.contact_verify = contact_verify;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
