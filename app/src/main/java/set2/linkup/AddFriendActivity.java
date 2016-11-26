package set2.linkup;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import adapter.UserRecyclerViewAdapter;
import bean.UserBean;
import service.XmppUtil;
import service.LinkupApplication;
import util.UserUtil;

/**
 * Created by zhang on 2016/11/23 0023.
 */

public class AddFriendActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private ImageView submit;
    private EditText editText;

    private List<UserBean> userList;

    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter adapter;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.arg1==1){
                userList = (List<UserBean>)msg.obj;

                for(int i = 0 ; i<userList.size() ; i++){
                    if(userList.get(i).getUsername().equals(LinkupApplication.getStringPref(UserUtil.UNAME)))
                        userList.remove(i);
                }

                adapter.setUserList(userList);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);

        initViews();
    }

    public void initViews(){
        submit = (ImageView) findViewById(R.id.iv_submit);
        editText = (EditText) findViewById(R.id.et_submit);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new UserRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String keyword = editText.getText().toString();
                        if(!keyword.equals("")){
                            XmppUtil.getInstance().searchUsers(handler,0,keyword);
                        }
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //back button
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
