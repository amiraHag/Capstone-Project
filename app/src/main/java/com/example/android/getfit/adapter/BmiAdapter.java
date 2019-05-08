package com.example.android.getfit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.getfit.repository.OnItemClickListener;
import com.example.android.getfit.utils.Constants;
import com.example.android.getfit.utils.DateConverter;
import com.example.android.getfit.R;
import com.example.android.getfit.db.Bmi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BmiAdapter extends RecyclerView.Adapter {
    private static OnItemClickListener mOnItemClickListener;
    List<Bmi> bmiList = new ArrayList<>();

    public BmiAdapter(List<Bmi> bmiList) {
        this.bmiList = bmiList;
    }

    public BmiAdapter() {
    }


    public void setBmiList(List<Bmi> bmiList) {
        this.bmiList = bmiList;
        notifyDataSetChanged();
    }

    public Bmi getBmiAtPosition(int position) {
        return bmiList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.bmi_list_item, viewGroup, false);
        BmiViewHolder bmiViewHolder = new BmiViewHolder(itemView);
        return bmiViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        Bmi currentBmi = bmiList.get(position);
        BmiViewHolder bmiViewHolder = (BmiViewHolder) viewHolder;
        bmiViewHolder.onBind(currentBmi);
    }

    @Override
    public int getItemCount() {
        return bmiList.size();
    }


    public class BmiViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bmiValue)  TextView mBmiValue;
        @BindView(R.id.bmiStatusDescription)  TextView mBmiDescription;
        @BindView(R.id.createdAt)  TextView mBmiDate;
        @BindView(R.id.backStrip)   FrameLayout mBmiItemDecoration;

        public BmiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener!=null) {
                        mOnItemClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }

        public void onBind(Bmi bmi) {

            mBmiValue.setText(bmi.getBmiValue());
            mBmiDescription.setText(bmi.getBmiStatusDescription());
            mBmiDate.setText(DateConverter.getDayMonth(bmi.getCreatedAt()));
            mBmiItemDecoration.setBackgroundColor(Constants.getColor());

        }



    }


}
