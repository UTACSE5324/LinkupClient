package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import bean.MessageBean;
import service.XmppUtil;
import set2.linkup.ImgCaptureActivity;
import set2.linkup.R;
import util.DateUtil;

/**
 * Name: MessageRecyclerViewAdapter
 * Description: Adapter for RecyclerView in MessageActivity
 * Created on 2016/10/2 0002.
 */

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter{
    /*
    * ITEM for message sent by user
    * ITEM_ME for message user received
    * */
    private final static int ITEM = 1;
    private final static int ITEM_ME = 2;

    private Context context;
    private List<MessageBean> msgList;

    /*
    * showTranslate for judgement of the statement
    *     true: adapter will show the translated message
    *     false: adapter will show the original message
    * */
    private boolean showTranslate;


    public MessageRecyclerViewAdapter(Context context,List<MessageBean> msgList){
        this.context = context;
        this.msgList = msgList;
        this.showTranslate = false;
    }


    public void reverseTranslate(){
        showTranslate = !showTranslate;
    }

    @Override
    public int getItemCount(){
        return msgList.size();
    }

    //judge the Message is from current user or friend and display different view
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view;
        if(i == ITEM) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_msg, null);
            view.setLayoutParams(lp);
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_msg_pri, null);
            view.setLayoutParams(lp);
        }
        return new MessageRecyclerViewAdapter.ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position){
        if(msgList.get(position).isPrimary())
            return ITEM_ME;
        else
            return ITEM;
    }

    //full fill message to the view
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        final MessageBean bean = msgList.get(i);

        XmppUtil.getInstance().getAvatar(itemViewHolder.avatar, bean.getUser());

        if(showTranslate) {
            String s = "translated "
                    + DateUtil.getDateNormal(bean.getDateline());
            itemViewHolder.tvDate.setText(s);
        }else
            itemViewHolder.tvDate.setText(DateUtil.getDateNormal(bean.getDateline()));

        if(bean.getMessage()!=null && bean.getMessage().length()>0) {
            itemViewHolder.ivMessage.setVisibility(View.GONE);
            itemViewHolder.tvMessage.setVisibility(View.VISIBLE);

            if (showTranslate && msgList.get(i).getTranslate() != null) {
                itemViewHolder.tvMessage.setText(bean.getTranslate());
            } else {
                itemViewHolder.tvMessage.setText(bean.getMessage());
            }
        }else{
            itemViewHolder.ivMessage.setVisibility(View.VISIBLE);
            itemViewHolder.tvMessage.setVisibility(View.GONE);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bean.getImage(), 0, bean.getImage().length);

            itemViewHolder.ivMessage.setImageBitmap(bitmap);
        }

        itemViewHolder.ivMessage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(bean.getMessage() == null ||bean.getMessage().length()==0){

                            Intent intent = new Intent(context, ImgCaptureActivity.class);
                            intent.putExtra("image", bean.getImage());
                            context.startActivity(intent);
                            
                        }
                    }
                }
        );
    }

    /*
    * ViewHolder for RecyclerView to reuse the recycle of Item
    * */
    class ItemViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView avatar,ivMessage;
        TextView tvMessage,tvDate;

        public ItemViewHolder(View itemView){
            super(itemView);

            this.itemView = itemView;

            avatar = (ImageView) itemView.findViewById(R.id.iv_item);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            ivMessage = (ImageView) itemView.findViewById(R.id.iv_message);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);

        }
    }
}
