package bean;

import android.graphics.Bitmap;

/**
 * Name: MessageBean
 * Description: Bean for the message received
 * message: summary of the message
 * translate: used for storage of the translated result
 * dateline: the dateline message being created(TimeStamp)
 * primary: if the message is created by current user
 * Structure will be like
 *           {
 *               "message":"Hello"
 *               "translate":""
 *               "dateline":"xxxxxxxxxxx"
 *               "primary":"true"
 *           }
 * Created on 2016/10/2 0002.
 */

public class MessageBean {
    String user;
    String message;
    String translate;
    byte[] image;
    long dateline;
    boolean primary;

    public String getUser(){return user;}

    public String getMessage() {
        return message;
    }

    public String getTranslate() {
        return translate;
    }

    public byte[] getImage(){return image;}

    public long getDateline() {
        return dateline;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public void setImage(byte[] image){this.image = image;}

    public void setUser(String user){this.user = user;}
}
