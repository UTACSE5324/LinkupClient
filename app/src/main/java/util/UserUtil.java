package util;

import bean.UserBean;
import service.LinkupApplication;

/**
 * Name: UserUtil
 * Description: Save or get user from preference
 * Created on 2016/10/2 0002.
 */

public class UserUtil {

    public final static String UID = "id";
    public final static String TOKEN = "token";
    public final static String AVATAR = "avatar";
    public final static String UNAME = "username";
    public final static String EMAIL = "email";
    public final static String PASSWORD = "password";
    public final static String LANGUAGE = "language";

    public static void saveUserInfo(UserBean bean){
        if(bean!=null){
            LinkupApplication.setStringPref(UID,bean.getId());
            LinkupApplication.setStringPref(TOKEN,bean.getToken());
            LinkupApplication.setStringPref(AVATAR,bean.getAvatar());
            LinkupApplication.setStringPref(UNAME,bean.getUsername());
            LinkupApplication.setStringPref(EMAIL,bean.getEmail());
            LinkupApplication.setStringPref(PASSWORD,bean.getPassword());
            LinkupApplication.setStringPref(LANGUAGE,bean.getLanguage());
        }else{
            //Clear Cache
            LinkupApplication.setStringPref(UID,null);
            LinkupApplication.setStringPref(TOKEN,null);
            LinkupApplication.setStringPref(AVATAR,null);
            LinkupApplication.setStringPref(UNAME,null);
            LinkupApplication.setStringPref(EMAIL,null);
            LinkupApplication.setStringPref(PASSWORD,null);
            LinkupApplication.setStringPref(LANGUAGE,null);
        }
    }
}
