package bean;

import java.io.Serializable;

/**
 * Name: UserBean
 * Description: Bean for the user information
 * All the information will be got from user's google account and saved in SharedPreference
 * Created on 2016/10/2 0002.
 */

public class UserBean implements Serializable{

    String username;
    String email;
    String password;
    String id;
    String avatar;
    String token;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getToken() {
        return token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
