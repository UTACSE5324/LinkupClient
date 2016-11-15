package adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import service.LinkupApplication;
import set2.linkup.R;

/**
 *  Name: SpeechRecyclerViewAdapter
 *  Description: Adapter for RecyclerView in TranslateFragment
 **/

public class SpeechRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;

    private List<String> sourceList;
    private List<String> transList;

    private TextToSpeech tts;

    public SpeechRecyclerViewAdapter(Context context, List<String> sourceList, List<String> transList) {
        this.context = context;
        this.sourceList = sourceList;
        this.transList = transList;

        /*Speech method by Google API*/
        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("TTS", "Text to speech engine started successfully.");
                            //Set Speech language
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("TTS", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(LinkupApplication.getContext(), listener);
    }

    public void setList(List<String> sourceList, List<String> transList) {
        this.sourceList = sourceList;
        this.transList = transList;
    }

    @Override
    public int getItemCount() {
        return sourceList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_trans, null);
        view.setLayoutParams(lp);
        return new SpeechRecyclerViewAdapter.ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;


        final String source = sourceList.get(i);

        itemViewHolder.source.setText(source);


        if (transList.size() > i) {
            itemViewHolder.trans.setText(transList.get(i));
        } else
            itemViewHolder.trans.setText("");

        itemViewHolder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (transList.size() > i)
                            tts.speak(transList.get(i), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
                    }
                }
        );
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView source, trans;

        public ItemViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            source = (TextView) itemView.findViewById(R.id.source);
            trans = (TextView) itemView.findViewById(R.id.trans);
        }
    }
}
