package set2.linkup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import adapter.MessageRecyclerViewAdapter;
import bean.MessageBean;
import bean.TranslateBean;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import service.HttpUtil;
import service.XmppUtil;
import okhttp3.Headers;
import okhttp3.Response;
import service.LinkupApplication;
import util.UserUtil;

import static service.LinkupApplication.context;

/**
 * Name: MessageActivity
 * Description: Activity for a conversation.
 * Created on 2016/10/2 0002.
 */

public class MessageActivity extends AppCompatActivity {
    private final static int SEND_IMG = 1;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private EditText etReply;
    private ImageView ivReply, ivImage;

    /*show this dialog when waiting for response of the Translate API*/
    private ProgressDialog progressDialog;

    private List<MessageBean> msgList;
    private MessageRecyclerViewAdapter adapter;

    private boolean showTranslate;

    private String uName;
    private Chat newChat;

    private BaseHttpRequestCallback callback = new BaseHttpRequestCallback() {
        @Override
        public void onResponse(Response httpResponse, String response, Headers headers) {
            try {
                TranslateBean bean = JSON.parseObject(response, TranslateBean.class);

                for (int i = 0; i < msgList.size(); i++) {
                    String trans = bean.getData().getTranslations().get(i).getTranslatedText();
                    msgList.get(i).setTranslate(trans);
                }

                if (adapter != null) {
                    adapter.reverseTranslate();
                    adapter.notifyDataSetChanged();
                }

                Toast.makeText(context, "Messages have been translated", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
            } catch (Exception e) {
            }
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adapter.notifyItemRangeInserted(msgList.size(), 1);
            recyclerView.scrollToPosition(recyclerView.getChildCount());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uName = getIntent().getStringExtra("uname");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(uName);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);

        showTranslate = false;

        initViews();
        initData();

    }

    public void initViews() {

        progressDialog = new ProgressDialog(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        etReply = (EditText) findViewById(R.id.et_reply);
        ivReply = (ImageView) findViewById(R.id.iv_reply);
        ivImage = (ImageView) findViewById(R.id.iv_image);

        msgList = new ArrayList<>();
        adapter = new MessageRecyclerViewAdapter(this, msgList);

        recyclerView.setAdapter(adapter);

        //handle user's send message request
        ivReply.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etReply.getText().length() > 0) {
                            try {
                                newChat.sendMessage(etReply.getText().toString());

                                MessageBean bean = new MessageBean();
                                bean.setPrimary(true);
                                bean.setUser(LinkupApplication.getStringPref(UserUtil.UNAME));
                                bean.setMessage(etReply.getText().toString());
                                bean.setDateline(System.currentTimeMillis());
                                msgList.add(bean);

                                etReply.setText("");

                                handler.sendEmptyMessage(0);

                            } catch (Exception e) {
                            }
                        }
                    }
                }
        );

        ivImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MessageActivity.this, ImgFolderActivity.class);
                        intent.putExtra("max", 1);
                        startActivityForResult(intent, SEND_IMG);
                    }
                }
        );
    }

    public void initData() {
        ChatManager chatmanager = XmppUtil.getInstance().getConnection().getChatManager();
        newChat = chatmanager.createChat(uName + "@linkupserver/Smack", new MessageListener() {
            public void processMessage(Chat chat, Message message) {
                if (message.getBody() != null) {

                    MessageBean bean = new MessageBean();
                    bean.setPrimary(false);
                    bean.setUser(uName);

                    String msg = message.getBody();

                    if (msg.length() > 50) {
                        byte[] bytes = Base64.decode(msg, Base64.DEFAULT);

                        bean.setImage(bytes);
                    } else {
                        bean.setMessage(msg);
                    }

                    bean.setDateline(System.currentTimeMillis());
                    msgList.add(bean);

                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            switch (requestCode) {
                case SEND_IMG:
                    try {
                        List<String> resList = (List<String>) data.getSerializableExtra("result");

                        FileInputStream fis = new FileInputStream(resList.get(0));
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        fis.close();

                        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, output);//把bitmap100%高质量压缩 到 output对象里
                        bitmap.recycle();//自由选择是否进行回收
                        byte[] image = output.toByteArray();//转换成功了
                        String encodeImage = StringUtils.encodeBase64(image);

                        newChat.sendMessage(encodeImage);

                        MessageBean bean = new MessageBean();
                        bean.setPrimary(true);
                        bean.setUser(LinkupApplication.getStringPref(UserUtil.UNAME));
                        bean.setImage(image);
                        bean.setDateline(System.currentTimeMillis());
                        msgList.add(bean);

                        handler.sendEmptyMessage(0);
                    } catch (Exception e) {

                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //back button
            case android.R.id.home:
                this.finish();
                break;
            //translate message button
            case R.id.action_translate:
                if (msgList.size() > 0) {
                    if (!showTranslate) {

                        List<String> list = new ArrayList<String>();

                        for (int i = 0; i < msgList.size(); i++) {
                            list.add(msgList.get(i).getMessage());
                        }
                        new HttpUtil(HttpUtil.NON_TOKEN_PARAMS)
                                .translate(list, callback);

                        progressDialog.show();
                    } else {
                        adapter.reverseTranslate();
                        adapter.notifyDataSetChanged();
                    }

                    showTranslate = !showTranslate;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
