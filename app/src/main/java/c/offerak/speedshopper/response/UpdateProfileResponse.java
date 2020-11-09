package c.offerak.speedshopper.response;

public class UpdateProfileResponse {


    /**
     * status : 200
     * message : Profile updated successfully.
     * data : {"id":"1","name":"Hemant Kumar Anjana","email":"hemant@infograins.com","contact":"7894561235","contact_verify":"1","password":"e10adc3949ba59abbe56e057f20f883e","token":"201804181725110000005ad7321f87ddbKVnu2MlQ8ZSTbf759Rc1yHWadGhrmEvi","profile_pic":"eb939104ab79e4c25bb00261c28754f1.png","device_id":"android","device_type":"android","status":"1","created_at":"2018-04-18 17:25:11","facebook_id":"","path":"http://192.168.1.120/SSTX/public/images/profile/"}
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
         * name : Hemant Kumar Anjana
         * email : hemant@infograins.com
         * contact : 7894561235
         * contact_verify : 1
         * password : e10adc3949ba59abbe56e057f20f883e
         * token : 201804181725110000005ad7321f87ddbKVnu2MlQ8ZSTbf759Rc1yHWadGhrmEvi
         * profile_pic : eb939104ab79e4c25bb00261c28754f1.png
         * device_id : android
         * device_type : android
         * status : 1
         * created_at : 2018-04-18 17:25:11
         * facebook_id :
         * path : http://192.168.1.120/SSTX/public/images/profile/
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
        private String facebook_id;
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

        public String getFacebook_id() {
            return facebook_id;
        }

        public void setFacebook_id(String facebook_id) {
            this.facebook_id = facebook_id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
