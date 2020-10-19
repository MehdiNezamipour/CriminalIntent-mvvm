package com.example.criminalintent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.criminalintent.R;
import com.example.criminalintent.databinding.ListRowCrimeBinding;
import com.example.criminalintent.model.Crime;

import java.util.List;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.CrimeHolder> {

    private List<Crime> mCrimes;
    private final Context mContext;

    public interface OnCrimeSelectListener {
        void onCrimeSelected(Crime crime);
    }
    private OnCrimeSelectListener mOnCrimeSelectListener;

    public void setOnCrimeSelectListener(OnCrimeSelectListener onCrimeSelectListener) {
        mOnCrimeSelectListener = onCrimeSelectListener;
    }

    public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;
    }

    public CrimeAdapter(List<Crime> crimes, Context context) {
        mCrimes = crimes;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ListRowCrimeBinding listRowCrimeBinding = DataBindingUtil
                .inflate(inflater, R.layout.list_row_crime, parent, false);

        return new CrimeHolder(listRowCrimeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
        Crime crime = mCrimes.get(position);
        holder.bindCrime(crime);
    }



    public class CrimeHolder extends RecyclerView.ViewHolder {


        private Crime mCrime;
        private ListRowCrimeBinding mListRowCrimeBinding;


        public CrimeHolder(ListRowCrimeBinding listRowCrimeBinding) {
            super(listRowCrimeBinding.getRoot());
            mListRowCrimeBinding = listRowCrimeBinding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnCrimeSelectListener.onCrimeSelected(mCrime);
                }
            });
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mListRowCrimeBinding.listRowCrimeTitle.setText(crime.getTitle());
            mListRowCrimeBinding.listRowCrimeDate.setText(crime.getDate().toString());
            mListRowCrimeBinding.imgviewSolved.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
