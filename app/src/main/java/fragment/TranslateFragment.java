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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.SpeechRecyclerViewAdapter;
import ocr.OcrCaptureActivity;
import set2.linkup.R;

import static android.app.Activity.RESULT_OK;

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

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new SpeechRecyclerViewAdapter(context);
        recyclerView.setAdapter(adapter);

        Button speakButton = (Button) rootView.findViewById(R.id.btn_speak);
        Button imageButton = (Button) rootView.findViewById(R.id.btn_camera);
        PackageManager pm = context.getPackageManager();


        imageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OcrCaptureActivity.class);
                        context.startActivity(intent);
                    }
                }
        );

        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() != 0) {

            speakButton.setOnClickListener(this);

        } else {

            speakButton.setEnabled(false);

            speakButton.setText("Recognizer not present");

        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_speak) {
            startVoiceRecognitionActivity();
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
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for (int i = 0; i < matches.size(); i++) {
                    Log.e(i + "", matches.get(i));
                }

                adapter.setList(matches);
                adapter.notifyDataSetChanged();

            }catch(Exception e){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}
