package set2.linkup;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.SelectGridViewAdapter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by zhang on 2016/11/12 0012.
 */

public class ImgFolderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private GridView gridview;

    private MaterialDialog loadingDialog, alertDialog;

    private List<String> srcList = new ArrayList<String>();

    private SelectGridViewAdapter adapter;

    private int maxNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_folder);

        maxNum = getIntent().getIntExtra("max", 10);

        initViews();
        getImages();
    }

    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Album");
        toolbar.setNavigationIcon(R.mipmap.back);

        gridview = (GridView) findViewById(R.id.grid);

        loadingDialog = new MaterialDialog.Builder(this)
                .content("Loading")
                .progress(true, 0).build();

        alertDialog = new MaterialDialog.Builder(this)
                .title("Select ?")
                .positiveText("submit")
                .negativeText("cancle")
                .negativeColorRes(R.color.colorTextSecond)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("result", (Serializable) adapter.getSelectImages());
                        intent.putExtras(bundle);

                        setResult(RESULT_OK, intent);
                        ImgFolderActivity.this.finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ImgFolderActivity.this.finish();
                    }
                })
                .build();
    }

    public void initData() {
        adapter = new SelectGridViewAdapter(this, srcList, maxNum);
        gridview.setAdapter(adapter);
    }

    public void getImages() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            loadingDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver mContentResolver = ImgFolderActivity.this
                            .getContentResolver();

                    // 只查询jpeg和png的图片
                    Cursor mCursor = mContentResolver.query(mImageUri, null,
                            MediaStore.Images.Media.MIME_TYPE + "=? or "
                                    + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"},
                            MediaStore.Images.Media.DATE_MODIFIED);

                    while (mCursor.moveToNext()) {
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        srcList.add(path);
                    }

                    mCursor.close();
                    handler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    loadingDialog.dismiss();
                    // 为View绑定数据
                    initData();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_img_folder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_submit:
                if (adapter != null && adapter.getSelectImages().size() > 0) {
                    Intent intent = new Intent();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("result", (Serializable) adapter.getSelectImages());
                    intent.putExtras(bundle);

                    setResult(RESULT_OK, intent);
                    ImgFolderActivity.this.finish();
                }
                break;
            case android.R.id.home:
                if (adapter != null && adapter.getSelectImages().size() > 0) {
                    alertDialog.show();
                } else
                    this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
