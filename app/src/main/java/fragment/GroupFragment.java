package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapter.GroupRecyclerViewAdapter;
import set2.linkup.R;

/**
 * Name: GroupFragment
 * Description: Fragment for conversation list in MainActivity
 * Created on 2016/10/2 0002.
 */

public class GroupFragment extends Fragment {
    private static final String ARG_SECTION = "Group";

    private RecyclerView recyclerView;
    private GroupRecyclerViewAdapter groupRecyclerViewAdapter;

    public static GroupFragment newInstance(){
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString("type", ARG_SECTION);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        groupRecyclerViewAdapter = new GroupRecyclerViewAdapter(getContext());
        recyclerView.setAdapter(groupRecyclerViewAdapter);

        return rootView;
    }
}
