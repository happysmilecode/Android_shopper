package c.offerak.speedshopper.response;

public class ProfileResponse {


    /**
     * status : 200
     * message : User Detail
     * data : {"id":"1","name":"Hemant","email":"hemant@infograins.com","contact":"8462014788","contact_verify":"1","password":"e10adc3949ba59abbe56e057f20f883e","token":"201804181424330000005ad707c9ecd11K5xLTRNHDqXZIJrfePjAGUs89yWMio03","profile_pic":"","device_id":"1234","device_type":"android","status":"1","created_at":"2018-04-13 01:38:59","path":"https://infograins.in/INFO01/SSTX/public/images/profile/"}
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
         * name : Hemant
         * email : hemant@infograins.com
         * contact : 8462014788
         * contact_verify : 1
         * password : e10adc3949ba59abbe56e057f20f883e
         * token : 201804181424330000005ad707c9ecd11K5xLTRNHDqXZIJrfePjAGUs89yWMio03
         * profile_pic :
         * device_id : 1234
         * device_type : android
         * status : 1
         * created_at : 2018-04-13 01:38:59
         * path : https://infograins.in/INFO01/SSTX/public/images/profile/
         */

        private String id;
        private String name;
        private String email;
        private String contact;
        private String contact_verify;
        private String password;
        private String token;
        private String profile_pic;
        private String device_id;
        private String device_type;
        private String status;
        private String created_at;
        private String path;
        private String balance;
        private String login_num;


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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getLogin_num() {
            return login_num;
        }

        public void setLogin_num(String login_num) {
            this.login_num = login_num;
        }
    }
}
