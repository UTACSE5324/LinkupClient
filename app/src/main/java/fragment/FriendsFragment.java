package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapter.ChatRecyclerViewAdapter;
import adapter.FriendRecyclerViewAdapter;
import set2.linkup.R;

/**
 * Name: FriendsFragment
 * Description: Fragment for friends list in MainActivity
 * Created on 2016/10/2 0002.
 */

public class FriendsFragment extends Fragment {
    private static final String ARG_SECTION = "Friends";

    private RecyclerView recyclerView;
    private FriendRecyclerViewAdapter friendRecyclerViewAdapter;

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

        return rootView;
    }
}
