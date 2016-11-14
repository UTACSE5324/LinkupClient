package adapter;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bean.UserBean;
import connect.XmppUtil;
import service.LinkupApplication;
import set2.linkup.MessageActivity;
import set2.linkup.R;

/**
 * Created by zhang on 2016/11/13 0013.
 */

public class SpeechRecyclerViewAdapter extends RecyclerView.Adapter{
    private Context context;

    private List<String> list;

    private TextToSpeech tts;

    public SpeechRecyclerViewAdapter(Context context){
        this.context = context;
        this.list = new ArrayList<>();

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("TTS", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("TTS", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(LinkupApplication.getContext(), listener);
    }

    public void setList(List<String> list){
        this.list = list;
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_normal, null);
        view.setLayoutParams(lp);
        return new SpeechRecyclerViewAdapter.ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position){
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;


        final String source = list.get(i);

        itemViewHolder.text.setText(source);
        itemViewHolder.image.setImageResource(R.mipmap.ic_translate_black_24dp);

        itemViewHolder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts.speak(source, TextToSpeech.QUEUE_ADD, null, "DEFAULT");
                    }
                }
        );
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView image;
        TextView text;

        public ItemViewHolder(View itemView){
            super(itemView);

            this.itemView = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
