package set2.linkup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import adapter.SectionsPagerAdapter;
import connect.XmppUtil;
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

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager());
        ViewPager viewpager = (ViewPager) findViewById(R.id.container);

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


        XmppUtil.getInstance().getAvatar(avatar,LinkupApplication.getStringPref(UserUtil.UNAME));

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
            //Quit the App, disconnect the Server
            if(XmppUtil.getInstance().getConnection().isConnected()){
                XmppUtil.getInstance().getConnection().disconnect();
            }
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
            case R.id.nav_account:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
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

        /*
        * SignOut
        * First disconnect the server
        * Second clear the local data
        * */

        if(XmppUtil.getInstance().getConnection().isConnected()){
            XmppUtil.getInstance().getConnection().disconnect();
        }

        UserUtil.saveUserInfo(null);
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            setAccount();
        }catch (Exception e){

        }
    }
}
