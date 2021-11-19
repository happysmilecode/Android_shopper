package c.offerak.speedshopper.response;

public class SignupResponse {


    /**
     * status : 200
     * message : You have been registered successfully. Please check your email for verification.
     * data : {"id":"32","name":"Hemant","email":"hemant11@infograins.com","email_verify":"0","contact":"12345678977","contact_verify":"0","profile_pic":"","token":"201804161314590000005ad4547b75041v1Rt4mZgXUszGnIMEDhbTral6H5dB8C0","otp":"021576"}
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
         * id : 32
         * name : Hemant
         * email : hemant11@infograins.com
         * email_verify : 0
         * contact : 12345678977
         * contact_verify : 0
         * profile_pic :
         * token : 201804161314590000005ad4547b75041v1Rt4mZgXUszGnIMEDhbTral6H5dB8C0
         * otp : 021576
         */

        private String id;
        private String name;
        private String email;
        private String email_verify;
        private String contact;
        private String contact_verify;
        private String profile_pic;
        private String token;
        private String otp;
//        private String userToken;
//        private String balance;
//        private String smsCode;


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

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

//        public String getTokenUser() {
//            return userToken;
//        }
//
//        public void setTokenUser(String userToken) {
//            this.userToken = userToken;
//        }
//
//        public String getSmsCode() {
//            return smsCode;
//        }
//
//        public void setSmsCode(String smsCode) {
//            this.smsCode = smsCode;
//        }
//
//        public String getBalance() {
//            return balance;
//        }
//
//        public void setBalance(String balance) {
//            this.balance = balance;
//        }
    }
}
