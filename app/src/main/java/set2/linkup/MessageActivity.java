package set2.linkup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

import adapter.MessageRecyclerViewAdapter;
import bean.MessageBean;
import bean.TranslateBean;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import connect.HttpUtil;
import connect.XmppUtil;
import okhttp3.Headers;
import okhttp3.Response;

import static service.LinkupApplication.context;

/**
 * Name: MessageActivity
 * Description: Activity for a conversation.
 * Created on 2016/10/2 0002.
 */

public class MessageActivity extends AppCompatActivity {
    private final static int SENDMSG = 1;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private EditText etReply;
    private ImageView ivReply;

    /*show this dialog when waiting for response of the Translate API*/
    private ProgressDialog progressDialog;

    private List<MessageBean> msgList;
    private MessageRecyclerViewAdapter adapter;

    private boolean showTranslate;

    private String uName;
    private Chat newChat;


    private BaseHttpRequestCallback callback = new BaseHttpRequestCallback(){
        @Override
        public void onResponse(Response httpResponse, String response, Headers headers) {
            try{
                TranslateBean bean =  JSON.parseObject(response, TranslateBean.class);

                for(int i = 0 ; i<msgList.size() ;i++){
                    String trans = bean.getData().getTranslations().get(i).getTranslatedText();
                    msgList.get(i).setTranslate(trans);
                }

                if(adapter!=null){
                    adapter.reverseTranslate();
                    adapter.notifyDataSetChanged();
                }

                Toast.makeText(context,"Messages have been translated",Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
            }catch (Exception e){}
        }};

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adapter.notifyItemRangeInserted(msgList.size(),1);
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

    public void initViews(){

        progressDialog = new ProgressDialog(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        etReply = (EditText) findViewById(R.id.et_reply);
        ivReply = (ImageView) findViewById(R.id.iv_reply);

        msgList = new ArrayList<>();
        adapter = new MessageRecyclerViewAdapter(this,msgList);

        recyclerView.setAdapter(adapter);

        //handle user's send message request
        ivReply.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etReply.getText().length()>0){
                            try {
                                newChat.sendMessage(etReply.getText().toString());
                                MessageBean bean = new MessageBean();
                                bean.setPrimary(true);
                                bean.setMessage(etReply.getText().toString());
                                bean.setDateline(System.currentTimeMillis());
                                msgList.add(bean);

                                etReply.setText("");

                                handler.sendEmptyMessage(0);

                            }catch(Exception e){}
                        }
                    }
                }
        );
    }

    public void initData(){
        ChatManager chatmanager = XmppUtil.getInstance().getConnection().getChatManager();
        newChat = chatmanager.createChat(uName + "@linkupserver/Smack" , new MessageListener() {
            public void processMessage(Chat chat, Message message){
                        if (message.getBody() != null) {

                            MessageBean bean = new MessageBean();
                            bean.setPrimary(false);
                            bean.setMessage(message.getBody());
                            bean.setDateline(System.currentTimeMillis());
                            msgList.add(bean);

                            handler.sendEmptyMessage(0);
                            }
                    }  
        });  
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            //back button
            case android.R.id.home:
                this.finish();
                break;
            //translate message button
            case R.id.action_translate:
                if(msgList.size() > 0) {
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
