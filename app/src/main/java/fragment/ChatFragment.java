package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapter.ChatRecyclerViewAdapter;
import set2.linkup.R;

/**
 * Name: ChatFragment
 * Description: Fragment for conversation list in MainActivity
 * Created on 2016/10/2 0002.
 */

public class ChatFragment extends Fragment {
    private static final String ARG_SECTION = "Chat";

    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    public static ChatFragment newInstance(){
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("type", ARG_SECTION);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(getContext());
        recyclerView.setAdapter(chatRecyclerViewAdapter);

        return rootView;
    }
}
