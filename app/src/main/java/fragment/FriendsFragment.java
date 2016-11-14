package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import adapter.FriendRecyclerViewAdapter;
import bean.UserBean;
import connect.XmppUtil;
import service.LinkupApplication;
import set2.linkup.R;
import util.UserUtil;

/**
 * Name: FriendsFragment
 * Description: Fragment for friends list in MainActivity
 * Created on 2016/10/2 0002.
 */

public class FriendsFragment extends Fragment {
    private static final String ARG_SECTION = "Friends";
    private static final int FRIENDS = 1;

    private RecyclerView recyclerView;
    private FriendRecyclerViewAdapter friendRecyclerViewAdapter;

    private List<UserBean> friendsList;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case FRIENDS:
                    if(msg.arg1==1){
                        friendsList = (List<UserBean>)msg.obj;

                        for(int i = 0 ; i<friendsList.size() ; i++){
                            if(friendsList.get(i).getUsername().equals(LinkupApplication.getStringPref(UserUtil.UNAME)))
                                friendsList.remove(i);
                        }

                        friendRecyclerViewAdapter.setUserList(friendsList);
                        friendRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }};

    public static FriendsFragment newInstance(){
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString("type", ARG_SECTION);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(getContext());
        recyclerView.setAdapter(friendRecyclerViewAdapter);

        initData();

        return rootView;
    }

    public void initData(){
        XmppUtil.getInstance().searchUsers(handler,FRIENDS,"a");
    }
}
