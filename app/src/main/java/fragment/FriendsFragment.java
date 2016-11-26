package fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import adapter.FriendRecyclerViewAdapter;
import bean.MessageBean;
import bean.UserBean;
import service.XmppUtil;
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
    private static final int MESSAGE = 2;

    private ProgressDialog loadingDialog;

    private RecyclerView recyclerView;
    private FriendRecyclerViewAdapter friendRecyclerViewAdapter;

    private List<UserBean> friendsList;
    private List<MessageBean> offlineMsg;

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean getFriends,getMsg;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case FRIENDS:
                    getFriends = true;
                    if(msg.obj != null){
                        friendsList = (List<UserBean>)msg.obj;

                        for(int i = 0 ; i<friendsList.size() ; i++){
                            if(friendsList.get(i).getUsername().equals(LinkupApplication.getStringPref(UserUtil.UNAME)))
                                friendsList.remove(i);
                        }

                        friendRecyclerViewAdapter.setUserList(friendsList);
                    }
                    break;
                case MESSAGE:
                    getMsg = true;
                    if(msg.obj !=null){
                        offlineMsg =  (List<MessageBean>)msg.obj;
                        friendRecyclerViewAdapter.setMsgList(offlineMsg);
                    }
                    break;
            }

            refreshList();
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

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("Loading");
        loadingDialog.show();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(getContext());
        recyclerView.setAdapter(friendRecyclerViewAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getFriends = false;
                                getMsg = false;
                                initData();
                            }
                        }, 1000);
                    }
                }
        );

        getFriends = false;
        getMsg = false;

        initData();

        return rootView;
    }

    public void initData(){
        XmppUtil.getInstance().getAllFriends(handler,FRIENDS);

        XmppUtil.getInstance().getOfflineMessage(handler,MESSAGE);
    }

    public void refreshList(){
        if(getFriends && getMsg) {
            loadingDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            friendRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
