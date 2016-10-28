package set2.linkup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import connect.XmppUtil;
import service.LinkupApplication;
import util.UserUtil;

/**
 * Name: LoginActivity
 * Description: Activity for Login page
 * Created on 2016/10/2 0002.
 */

public class LoginActivity extends FragmentActivity implements View.OnClickListener{
    private final int LOGIN = 1;
    private final int REGISTER = 2;

    private Context context;

    private boolean login;

    private TextView typeText,typeButton,submitButton;
    private MaterialEditText username,password,confirm;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
           switch(msg.what){
               case LOGIN:
                   if(msg.arg1==1) {
                       Toast.makeText(context, "Login success", Toast.LENGTH_SHORT);

                       Intent intent = new Intent(context, MainActivity.class);
                       startActivity(intent);
                       finishAct();
                   }
                   if(msg.arg1==-1)
                       Toast.makeText(context,"Login fail",Toast.LENGTH_SHORT);
                   break;
               case REGISTER:
                   if(msg.arg1==0)
                       Toast.makeText(context,"No result",Toast.LENGTH_SHORT);
                   if(msg.arg1==1)
                       Toast.makeText(context,"Register success",Toast.LENGTH_SHORT);
                   if(msg.arg1==2)
                       Toast.makeText(context,"Name exists",Toast.LENGTH_SHORT);
                   if(msg.arg1==3)
                       Toast.makeText(context,"Login fail",Toast.LENGTH_SHORT);
                   if(msg.arg1==-1)
                       Toast.makeText(context,"Connect fail",Toast.LENGTH_SHORT);
                   break;
           }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        login = true;

        /*
        * judge if the user has signed in
        * */
        if(LinkupApplication.getStringPref(UserUtil.UNAME).equals("")){
            initViews();
        }else{
            // enter the main page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void initViews(){

        typeText = (TextView) findViewById(R.id.type);
        typeButton = (TextView) findViewById(R.id.btn_type);
        submitButton = (TextView) findViewById(R.id.btn_submit);

        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);
        confirm = (MaterialEditText) findViewById(R.id.confirm);

        typeButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_type:

                if(login){
                    login = false;
                    typeText.setText("Register");
                    confirm.setVisibility(View.VISIBLE);
                    typeButton.setText("Login");
                }else{
                    login = true;
                    typeText.setText("Login");
                    confirm.setVisibility(View.INVISIBLE);
                    typeButton.setText("Register");
                }
                break;
            case R.id.btn_submit:
                String uname = username.getText().toString();
                String pword = password.getText().toString();

                if(login)
                    XmppUtil.getInstance().login(handler, LOGIN, uname, pword);
                else if(confirm.getText().toString().equals(pword)){
                    XmppUtil.getInstance().register(handler, LOGIN, uname, pword);
                }
                break;
        }
    }

    public void finishAct(){
        this.finish();
    }

}
