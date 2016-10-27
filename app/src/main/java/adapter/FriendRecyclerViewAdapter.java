package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import set2.linkup.MessageActivity;
import set2.linkup.R;

/**
 * Name: FriendRecyclerViewAdapter
 * Description: Adapter for RecyclerView in FriendFragment
 * Created on 2016/10/2 0002.
 */

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter{
    private Context context;

    public FriendRecyclerViewAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount(){
        return 10;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_chat, null);
        view.setLayoutParams(lp);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position){
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        itemViewHolder.uname = "User" + i;

        itemViewHolder.avatar.setImageResource(R.mipmap.ic_account_circle_black_48dp);
        itemViewHolder.tvTitle.setText("User "+i);
        itemViewHolder.tvContent.setText("Information of User "+i);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        String uname;
        ImageView avatar;
        TextView tvTitle,tvContent;

        public ItemViewHolder(View itemView){
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.iv_item);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("uname", uname );
                            context.startActivity(intent);
                        }
                    }
            );
        }
    }
}
