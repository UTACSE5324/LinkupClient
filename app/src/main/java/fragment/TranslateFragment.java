package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import adapter.SpeechRecyclerViewAdapter;
import bean.TranslateBean;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import connect.HttpUtil;
import ocr.OcrCaptureActivity;
import okhttp3.Headers;
import okhttp3.Response;
import set2.linkup.R;

import static android.app.Activity.RESULT_OK;
import static service.LinkupApplication.context;

/**
 * Name: TranslateFragment
 * Description: Fragment for simultaneous translation.
 * Haven't finished.
 * Created on 2016/10/2 0002.
 */

public class TranslateFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_SECTION = "Translate";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

    private Context context;
    private RecyclerView recyclerView;
    private SpeechRecyclerViewAdapter adapter;

    //Recognized Text
    private ArrayList<String> sourceList;
    //Translated Text
    private ArrayList<String> transList;

    private BaseHttpRequestCallback callback = new BaseHttpRequestCallback() {
        @Override
        public void onResponse(Response httpResponse, String response, Headers headers) {
            try {
                TranslateBean bean = JSON.parseObject(response, TranslateBean.class);
                transList.clear();

                for (int i = 0; i < bean.getData().getTranslations().size(); i++) {
                    String trans = bean.getData().getTranslations().get(i).getTranslatedText();
                    trans = trans.replace("&#39;","'");
                    transList.add(trans);
                }

                if (adapter != null) {
                    adapter.setList(sourceList,transList);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
        }
    };

    public static TranslateFragment newInstance() {
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

        sourceList = new ArrayList<>();
        transList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new SpeechRecyclerViewAdapter(context,sourceList,transList);
        recyclerView.setAdapter(adapter);

        LinearLayout speakButton = (LinearLayout) rootView.findViewById(R.id.speak);
        LinearLayout imageButton = (LinearLayout) rootView.findViewById(R.id.camera);
        LinearLayout transButton = (LinearLayout) rootView.findViewById(R.id.translate);

        PackageManager pm = context.getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() != 0) {

            speakButton.setOnClickListener(this);

        } else {

            speakButton.setEnabled(false);

            //speakButton.setText("Recognizer not present");
        }

        transButton.setOnClickListener(this);
        imageButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.speak:
                startVoiceRecognitionActivity();
                break;
            case R.id.camera:
                Intent intent = new Intent(context, OcrCaptureActivity.class);
                context.startActivity(intent);
                break;
            case R.id.translate:
                new HttpUtil(HttpUtil.NON_TOKEN_PARAMS)
                        .translateReverse(sourceList, callback);
                break;
        }
    }

    private void startVoiceRecognitionActivity() {

        // Pass the Recognizer with Intent

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Language Model and Language type

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Mention the start of input

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Start talking...");

        // start voice recognizer

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

    }

    // onActivityResult when recognize stops

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == RESULT_OK) {
            // Accept the result

            try {
                sourceList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                transList.clear();

                adapter.setList(sourceList,transList);
                adapter.notifyDataSetChanged();

            }catch(Exception e){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}
