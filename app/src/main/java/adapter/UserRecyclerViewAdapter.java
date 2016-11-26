package adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.UserBean;
import service.XmppUtil;
import set2.linkup.R;

/**
 * Created on 2016/10/2 0002.
 */

public class UserRecyclerViewAdapter extends RecyclerView.Adapter{
    private Context context;

    private List<UserBean> userList;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1){
                case 1:
                    Toast.makeText(context,"Add Friends success !",Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(context,"Add Friends fail !",Toast.LENGTH_SHORT).show();
                    break;
            }
        }};

    public UserRecyclerViewAdapter(Context context){
        this.context = context;
        this.userList = new ArrayList<>();
    }

    //Get user list
    public void setUserList(List<UserBean> userList){
        this.userList = userList;
    }

    @Override
    public int getItemCount(){
        return userList.size();
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

    //display the user name and email
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        String uname = userList.get(i).getUsername();
        String email = userList.get(i).getEmail();

        itemViewHolder.uname = uname;

        /*use XmppUtil to get user bitmap*/
        XmppUtil.getInstance().getAvatar(itemViewHolder.avatar, uname);

        itemViewHolder.tvTitle.setText(uname);
        itemViewHolder.tvContent.setText(email);
    }

    //ViewHolder for recycle view
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
                            XmppUtil.getInstance().addFriend(handler,0,uname,uname);
                        }
                    }
            );
        }
    }
}
