package connect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bean.UserBean;
import service.LinkupApplication;
import set2.linkup.R;
import util.FileUtil;
import util.UserUtil;

import static android.R.attr.data;
import static org.jivesoftware.smackx.filetransfer.FileTransfer.Error.connection;
import static org.jivesoftware.smackx.workgroup.packet.RoomTransfer.Type.user;

/**
 * Name: XmppUtil
 * Description: Used for Xmpp request.
 *
 * Created on 2016/10/2 0002.
 */

public class XmppUtil {
    private static final String HOST = "172.20.10.4";
    private static final int PORT = 5222;
    private static final String SERVER_NAME = "linkupserver";
    private static XmppUtil instance;
    private XMPPConnection conn;

    /*Singleton method*/
    public static XmppUtil getInstance() {
        if (instance == null) {
            instance = new XmppUtil();
        }
        return instance;
    }

    public XMPPConnection getConnection() {
        return conn;
    }

    public void searchUsers(final Handler handler, final int what, final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (conn == null || !conn.isConnected())
                    initConnection();
                Message msg = new Message();
                msg.what = what;

                try {
                    if (conn.isConnected()) {

                        List<UserBean> results = new ArrayList<>();

                        UserSearchManager usm = new UserSearchManager(conn);
                        Form searchForm = usm.getSearchForm("search." + conn.getServiceName());
                        Form answerForm = searchForm.createAnswerForm();
                        answerForm.setAnswer("Email", true);
                        answerForm.setAnswer("Username", true);
                        answerForm.setAnswer("search", key);

                        ReportedData data = usm.getSearchResults(answerForm, "search." + conn.getServiceName());
                        Iterator<ReportedData.Row> it = data.getRows();
                        ReportedData.Row row = null;
                        UserBean user = null;

                        while (it.hasNext()) {
                            user = new UserBean();
                            row = it.next();
                            user.setUsername(row.getValues("Username").next().toString());
                            user.setEmail(row.getValues("Email").next().toString());

                            results.add(user);
                        }

                        msg.obj = results;
                        msg.arg1 = 1;
                    } else
                        msg.arg1 = -1;

                } catch (Exception e) {
                    msg.arg1 = -1;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void login(final Handler handler, final int what, final String uname, final String pword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (conn == null || !conn.isConnected())
                    initConnection();
                Message msg = new Message();
                msg.what = what;

                try {
                    if (conn.isConnected()) {
                        conn.login(uname, pword);
                        msg.arg1 = 1;
                    } else
                        msg.arg1 = -1;

                } catch (Exception e) {
                    msg.arg1 = -1;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void register(final Handler handler, final int what, final String uname, final String email, final String pword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (conn == null || !conn.isConnected())
                    initConnection();

                Message msg = new Message();
                msg.what = what;

                try {
                    if (conn.isConnected()) {

                        Registration reg = new Registration();
                        reg.setType(IQ.Type.SET);
                        reg.setTo(conn.getServiceName());
                        reg.setUsername(uname);
                        reg.setPassword(pword);
                        reg.addAttribute("android", "geolo_createUser_android");

                        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()),
                                new PacketTypeFilter(IQ.class));
                        PacketCollector collector = conn.createPacketCollector(filter);
                        conn.sendPacket(reg);
                        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
                        // Stop queuing results
                        collector.cancel();//停止请求results（是否成功的结果）

                        if (result == null) {
                            msg.arg1 = 0;
                        } else if (result.getType() == IQ.Type.ERROR) {
                            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                                msg.arg1 = 2;
                            } else {
                                msg.arg1 = 3;
                            }
                        } else if (result.getType() == IQ.Type.RESULT) {
                            msg.arg1 = 1;
                        }
                    }
                } catch (Exception e) {
                    msg.arg1 = -1;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void getAvatar(final ImageView view, final String user) {

        final Handler handler = new Handler(){
            public void handleMessage(android.os.Message msg) {

                if(msg.obj!=null){
                    Bitmap bitmap = (Bitmap) msg.obj;
                    view.setImageBitmap(bitmap);
                }else
                    view.setImageResource(R.mipmap.ic_account_circle_black_48dp);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (conn == null || !conn.isConnected())
                    initConnection();

                ByteArrayInputStream bais = null;
                try {
                    VCard vcard = new VCard();

                    ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
                            new org.jivesoftware.smackx.provider.VCardProvider());

                    vcard.load(conn, user + "@linkupserver");

                    Message msg = new Message();

                    if (vcard.getOrganization() != null) {
                        byte[] bytes = Base64.decode(vcard.getOrganization().getBytes(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        msg.obj = bitmap;
                    } else
                        msg.obj = null;

                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void setAvatar(final Handler handler, final int what, final byte[] image) {

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = what;

                try {

                    if (conn == null || !conn.isConnected())
                        initConnection();

                    VCard card = new VCard();
                    card.setFirstName(LinkupApplication.getStringPref(UserUtil.UNAME));
                    card.load(conn);

                    PacketFilter filter = new AndFilter(new PacketIDFilter(
                            card.getPacketID()), new PacketTypeFilter(IQ.class));
                    PacketCollector collector = conn
                            .createPacketCollector(filter);
                    String encodeImage = StringUtils.encodeBase64(image);

                    card.setOrganization(encodeImage);

//                    card.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
//                           + encodeImage + "</BINVAL>", true);
//                    card.setAvatar(image, encodeImage);

                    card.save(conn);

                    IQ iq = (IQ) collector.nextResult(SmackConfiguration
                            .getPacketReplyTimeout());

                    msg.arg1 = 1;
                } catch (Exception e) {
                    msg.arg1 = -1;
                }

                handler.sendMessage(msg);
            }
        }.start();
    }

    public void changePassword(final Handler handler, final int what, final String pwd) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (conn == null || !conn.isConnected())
                    initConnection();

                Message msg = new Message();
                msg.what = what;

                try {
                    if (conn.isConnected()) {
                        conn.getAccountManager().changePassword(pwd);
                        msg.arg1 = 1;
                    } else
                        msg.arg1 = -1;

                } catch (Exception e) {
                    msg.arg1 = -1;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    /*new the Connection method*/
    public boolean initConnection() {
        boolean isConnect = false;
        if (conn == null || !conn.isConnected()) {
            ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT, SERVER_NAME);

            config.setDebuggerEnabled(true);

            config.setReconnectionAllowed(true);
            config.setSendPresence(true);

            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

            conn = new XMPPConnection(config);

            try {
                configureProviderManager(ProviderManager.getInstance());
                conn.connect();

                isConnect = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isConnect;
    }

    public void configureProviderManager(ProviderManager pm) {
        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }
        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }

}
