package set2.linkup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapter.SectionsPagerAdapter;
import bean.LanguagesBean;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import http.Constant;
import http.HttpUtil;
import okhttp3.Headers;
import okhttp3.Response;
import service.LinkupApplication;
import util.UserUtil;
import view.CircleImageView;

/**
 * Name: LoginActivity
 * Description: Activity for Main page
 * Created on 2016/10/2 0002.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    /*Drawer from left, used for display user's options*/
    private NavigationView navigationView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        initViews();
        setAccount();
    }

    public void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewpager = (ViewPager) findViewById(R.id.container);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager());
        viewpager = (ViewPager) findViewById(R.id.container);
        //防止viewpager刷新

        viewpager.setOffscreenPageLimit(3);
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);
    }

    /*display user's information*/
    public void setAccount(){
        CircleImageView avatar = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView language = (TextView) navigationView.getHeaderView(0).findViewById(R.id.language);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);

        try {
            Picasso.with(this).load(LinkupApplication.getStringPref(UserUtil.AVATAR)).into(avatar);
        }catch(Exception e){
            avatar.setImageResource(R.mipmap.ic_account_circle_black_48dp);
        }
        String un = LinkupApplication.getStringPref(UserUtil.UNAME);
        String la = LinkupApplication.getStringPref(UserUtil.LANGUAGE);
        String em = LinkupApplication.getStringPref(UserUtil.EMAIL);

        userName.setText(un);
        language.setText("preferred language : "+la);
        email.setText(em);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            //Choose user's preferred language
            case R.id.nav_language:
                new MaterialDialog.Builder(this)
                        .title("Choose your language")
                        .items(R.array.language)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                LinkupApplication.setStringPref(
                                        UserUtil.LANGUAGE,
                                        text.toString()
                                );

                                TextView language = (TextView) navigationView.getHeaderView(0).findViewById(R.id.language);
                                language.setText("preferred language : "+text.toString());
                                return true;
                            }
                        })
                        .positiveText("Choose")
                        .show();
                break;
            //Logout
            case R.id.nav_logout:
                new MaterialDialog.Builder(this)
                        .title("Logout?")
                        .content("You will logout and exit Linkup.")
                        .positiveText("Agree")
                        .negativeText("Cancle")
                        .onPositive(
                                new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                              signOut();
                                    }
                                }
                        )
                        .build()
                        .show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut(){
        GoogleApiClient apiClient = LinkupApplication.apiClient;
        if(apiClient.isConnected())
        Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        UserUtil.saveUserInfo(null);
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
