package set2.linkup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import bean.UserBean;
import service.LinkupApplication;
import util.UserUtil;

/**
 * Name: LoginActivity
 * Description: Activity for Login page
 * Created on 2016/10/2 0002.
 */

public class LoginActivity extends FragmentActivity implements View.OnClickListener{
    public static int RC_SIGN_IN = 0;

    private TextView process;

    private Context context;
    private Intent signInIntent;

    private GoogleApiClient apiClient;
    private SignInButton signInButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        apiClient = LinkupApplication.apiClient;

        /*
        * judge if the user has signed in
        * */
        if(LinkupApplication.getStringPref(UserUtil.UNAME).equals("")){
            //use google API to sign in
            initViews();
        }else{
            // enter the main page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void initViews(){
        process = (TextView) findViewById(R.id.process);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.sign_in_button:
                signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    /*handle the result from google login activity*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            process.setText("sign in successfully");
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                try {
                    process.setText("getting account information\n"+process.getText());
                    GoogleSignInAccount acct = result.getSignInAccount();
                    UserBean bean = new UserBean();
                    bean.setUsername(acct.getDisplayName());
                    bean.setEmail(acct.getEmail());
                    bean.setId(acct.getId());
                    bean.setToken(acct.getIdToken());

                    if(acct.getPhotoUrl()!=null)
                        bean.setAvatar(acct.getPhotoUrl().toString());

                    UserUtil.saveUserInfo(bean);
                }catch(Exception e){

                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                finish();
            }
    }
}
