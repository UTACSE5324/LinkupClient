package bean;

import java.util.List;

/**
 * Created by zhang on 2016/11/23 0023.
 */

public class ConversationBean {
    List<MessageBean> msgList;
    String user;

    public List<MessageBean> getMsgList() {
        return msgList;
    }

    public String getUser() {
        return user;
    }

    public void setMsgList(List<MessageBean> msgList) {
        this.msgList = msgList;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
