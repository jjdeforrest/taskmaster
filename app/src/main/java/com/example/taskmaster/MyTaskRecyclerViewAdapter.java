package com.example.taskmaster;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyTaskRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecyclerViewAdapter.ViewHolder> {


    static final String TAG = "stg.ViewAdapter";
    public final List<Task> mValues;
    public final TaskFragment.OnListFragmentInteractionListener mListener;
//    public final Context mContext;

    public MyTaskRecyclerViewAdapter(List<Task> items, TaskFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
//        mContext = context;
    }

//    public MyTaskRecyclerViewAdapter(List<Task> items, TaskFragment.OnListFragmentInteractionListener listener){
//        mValues = items;
//        mListener = listener;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);



        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mBodyView.setText(mValues.get(position).getBody());
        holder.mStateView.setText(mValues.get(position).getState());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, TaskDetail.class);

                i.putExtra("task", mValues.get(position).title);
                i.putExtra("description", mValues.get(position).body);
                i.putExtra("status", mValues.get(position).state);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mBodyView;
        public final TextView mStateView;
        public Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.taskTitle);
            mBodyView = (TextView) view.findViewById(R.id.body);
            mStateView = view.findViewById(R.id.state);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBodyView.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTaskInteraction(Task task);
    }


}