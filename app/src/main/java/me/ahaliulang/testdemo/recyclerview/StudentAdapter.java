package me.ahaliulang.testdemo.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import me.ahaliulang.testdemo.R;

/**
 * author:tdn
 * time:2020/1/19
 * description:
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    private Context mContext;
    private SortedList<Student> mDatas;

    public StudentAdapter(Context context) {
        mContext = context;
        mDatas = new SortedList<Student>(Student.class, new SortedListAdapterCallback<Student>(this) {
            @Override
            public int compare(Student o1, Student o2) {
                return 0;
            }

            @Override
            public boolean areContentsTheSame(Student oldItem, Student newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areItemsTheSame(Student item1, Student item2) {
                return item1.getId() == item2.getId();
            }
        });
    }


    public SortedList<Student> getDataSet(){
        return mDatas;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mIdTv.setText(mDatas.get(position).getId()+"");
        holder.mNameTv.setText(mDatas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mIdTv, mNameTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mIdTv = itemView.findViewById(R.id.id_tv);
            mNameTv = itemView.findViewById(R.id.name_tv);
        }
    }
}
