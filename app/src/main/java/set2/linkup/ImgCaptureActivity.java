package set2.linkup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;

import bean.TranslateBean;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import connect.HttpUtil;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by zhang on 2016/11/12 0012.
 */

public class ImgCaptureActivity extends AppCompatActivity{
    private byte[] image;

    private ImageView imageView;
    private TextView textView,transTextView;

    private List<String> source;
    private String translated;

    private BaseHttpRequestCallback callback = new BaseHttpRequestCallback() {
        @Override
        public void onResponse(Response httpResponse, String response, Headers headers) {
            try {
                TranslateBean bean = JSON.parseObject(response, TranslateBean.class);
                translated = "";

                for(int i = 0; i< bean.getData().getTranslations().size() ; i++){
                    translated = translated + "\n" +
                            bean.getData().getTranslations().get(i).getTranslatedText();
                }

                transTextView.setText(translated);
            } catch (Exception e) {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_capture);

        image = getIntent().getByteArrayExtra("image");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TextCapture");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);

        initViews();
        initData();
    }

    public void initViews(){

        imageView = (ImageView) findViewById(R.id.iv_image);
        textView = (TextView) findViewById(R.id.text);
        transTextView = (TextView) findViewById(R.id.text_translate);
    }

    public void initData(){
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        imageView.setImageBitmap(bitmap);

        Context context = getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<TextBlock> textBlockSparseArray =  textRecognizer.detect(frame);

        source = new ArrayList<>();
        String s = "";

        for (int i = 0; i < textBlockSparseArray.size(); ++i) {
            TextBlock item = textBlockSparseArray.valueAt(i);

            source.add(item.getValue());

            s = s + "\n" + item.getValue();
        }

        textView.setText(s);
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

                if(source!=null && source.size() > 0){
                    new HttpUtil(HttpUtil.NON_TOKEN_PARAMS)
                            .translate(source, callback);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
