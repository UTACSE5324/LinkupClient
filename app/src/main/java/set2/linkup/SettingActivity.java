package set2.linkup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.List;

import service.XmppUtil;
import util.CacheUtil;

/**
 * Created by zhang on 2016/10/30 0030.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAMERA_SELECT = 1;

    private MaterialDialog changePwdDialog;
    private ProgressDialog progressDialog;
    private Context context;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 0)
                switch (msg.arg1){
                    case 1:
                        Toast.makeText(context,"Change password success !",Toast.LENGTH_SHORT).show();
                        changePwdDialog.dismiss();
                        break;
                    case -1:
                        Toast.makeText(context,"Change password fail !",Toast.LENGTH_SHORT).show();
                        break;
                }

            if(msg.what == 1)
                switch (msg.arg1){
                    case 1:
                        progressDialog.dismiss();
                        Toast.makeText(context,"Upload success !",Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        progressDialog.dismiss();
                        Toast.makeText(context,"Upload fail !",Toast.LENGTH_SHORT).show();
                        break;
                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Setting");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);

        initViews();
    }

    public void initViews() {
        LinearLayout btnClearCache = (LinearLayout) findViewById(R.id.clearcache);
        btnClearCache.setOnClickListener(this);

        LinearLayout btnAvatar = (LinearLayout) findViewById(R.id.changeavatar);
        btnAvatar.setOnClickListener(this);

        LinearLayout btnPassword = (LinearLayout) findViewById(R.id.changepwd);
        btnPassword.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        changePwdDialog = new MaterialDialog.Builder(this)
                .title("Change password")
                .customView(R.layout.dialog_change_pwd,true)
                .build();

        final View view = changePwdDialog.getCustomView();

        final MaterialEditText pwd = (MaterialEditText) view.findViewById(R.id.password);
        final MaterialEditText confirm = (MaterialEditText) view.findViewById(R.id.confirm);

        TextView submit = (TextView) view.findViewById(R.id.submit);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);

        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String a = pwd.getText().toString();
                        String b = confirm.getText().toString();
                        if(!a.equals("") && a.equals(b)){
                            XmppUtil.getInstance().changePassword(handler,0,a);
                        }
                    }
                }
        );

        cancle.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changePwdDialog.dismiss();
                    }
                }
        );
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.changeavatar:
                Intent intent = new Intent(this,ImgFolderActivity.class);
                intent.putExtra("max",1);
                startActivityForResult(intent,CAMERA_SELECT);
                break;
            case R.id.changepwd:
                changePwdDialog.show();
                break;
            case R.id.clearcache:
                Toast.makeText(context,"Clear cache " + CacheUtil.clearCache(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            switch(requestCode){
                case CAMERA_SELECT:
                    List<String> resList = (List<String>) data.getSerializableExtra("result");
                    setAvatar(resList.get(0));
                    break;
            }
        }
    }

    public void setAvatar(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
            bitmap.recycle();//自由选择是否进行回收
            byte[] image = output.toByteArray();//转换成功了

            progressDialog.show();
            XmppUtil.getInstance().setAvatar(handler, 1,image);

            output.close();
        }catch(Exception e){

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

        switch (id) {
            //back button
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
