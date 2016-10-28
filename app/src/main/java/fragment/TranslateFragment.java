package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import bean.TranslateBean;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import connect.HttpUtil;
import okhttp3.Headers;
import okhttp3.Response;
import set2.linkup.R;

/**
 * Name: TranslateFragment
 * Description: Fragment for simultaneous translation.
 * Haven't finished.
 * Created on 2016/10/2 0002.
 */

public class TranslateFragment extends Fragment {
    private static final String ARG_SECTION = "Translate";

    private Context context;

    private TextView textView;
    private EditText editText;
    private Button button;

    private BaseHttpRequestCallback callback = new BaseHttpRequestCallback(){
        @Override
        public void onResponse(Response httpResponse, String response, Headers headers) {
            try{
                TranslateBean bean =  JSON.parseObject(response, TranslateBean.class);

                String source = "";

                for(int i = 0; i< bean.getData().getTranslations().size() ; i++){
                    source = source+"\n"+bean.getData().getTranslations().get(i).getTranslatedText();
                }

                textView.setText(source);

            }catch (Exception e){}
        }};

    public static TranslateFragment newInstance(){
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();
        args.putString("type", ARG_SECTION);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);

        context = getActivity();

        textView = (TextView) rootView.findViewById(R.id.text);
        editText = (EditText) rootView.findViewById(R.id.edittext);
        button = (Button) rootView.findViewById(R.id.button);


        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> list = new ArrayList<String>();
                        list.add(editText.getText().toString());

                        new HttpUtil(HttpUtil.NON_TOKEN_PARAMS)
                                .translate(list,callback);
                    }
                }
        );

        return rootView;
    }

}
