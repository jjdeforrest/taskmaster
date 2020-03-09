package com.example.taskmaster;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class AllTasksFragment extends Fragment {

    //AppDatabase myDb;
    private AWSAppSyncClient mAWSAppSyncClient;

    List<Task> listOfTasks = new ArrayList<>();
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private TaskFragment.OnListFragmentInteractionListener mListener;


    public AllTasksFragment() {
    }
    @SuppressWarnings("unused")
    public static TaskFragment newInstance(int columnCount) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getActivity().getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getActivity().getApplicationContext()))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);


        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }



            this.listOfTasks = new ArrayList<Task>();

           // runQuery();

            recyclerView.setAdapter(new MyTaskRecyclerViewAdapter2(listOfTasks, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskFragment.OnListFragmentInteractionListener) {
            mListener = (TaskFragment.OnListFragmentInteractionListener) context;
        }
//        else {
//            throw new RuntimeException(context.toString()
//                + " must implement OnListFragmentInteractionListener");
//    }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Task task);
    }


//    public void runQuery() {
//        mAWSAppSyncClient.query(ListTasksQuery.builder().build())
//                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
//                .enqueue(tasksCallback);
//    }
//
//    private GraphQLCall.Callback<ListTasksQuery.Data> tasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
//        @Override
//        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {
//            for (ListTasksQuery.Item data : response.data().listTasks().items()) {
//                Task addingTask = new Task(data.title(), data.body(), data.state());
//                listOfTasks.add(addingTask);
//
//            }
//
//        }
//
//        @Override
//        public void onFailure(@Nonnull ApolloException e) {
//            Log.e("ERROR", e.toString());
//        }
//    };
}