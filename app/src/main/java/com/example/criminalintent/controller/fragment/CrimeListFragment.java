package com.example.criminalintent.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.criminalintent.R;
import com.example.criminalintent.controller.activity.CrimePagerActivity;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeDBRepository;
import com.example.criminalintent.repository.IRepository;

import java.util.List;

public class CrimeListFragment extends Fragment {

    public static final String TAG = "CLF";
    public static final String BUNDLE_IS_SUBTITLE_VISIBLE = "isSubtitleVisible";
    private RecyclerView mRecyclerView;
    private IRepository<Crime> mRepository;
    private CrimeAdapter mAdapter;
    private CallBacks mCallBacks;

    private boolean mIsSubtitleVisible = false;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    public static CrimeListFragment newInstance() {

        Bundle args = new Bundle();

        CrimeListFragment fragment = new CrimeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CallBacks) {
            mCallBacks = (CallBacks) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement onCrimeSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallBacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mRepository = CrimeDBRepository.getInstance(getActivity());

        if (savedInstanceState != null)
            mIsSubtitleVisible = savedInstanceState.getBoolean(BUNDLE_IS_SUBTITLE_VISIBLE, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        findViews(view);

        //recyclerview responsibility: positioning
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //performance issues
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_crime_list, menu);
        updateMenuItemSubtitle(menu.findItem(R.id.menu_item_subtitle));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                addCrime();
                return true;
            case R.id.menu_item_subtitle:
                mIsSubtitleVisible = !mIsSubtitleVisible;
//                updateMenuItemSubtitle(item);
                updateSubtitle();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMenuItemSubtitle(@NonNull MenuItem item) {
        if (mIsSubtitleVisible)
            item.setTitle(R.string.hide_subtitle);
        else
            item.setTitle(R.string.show_subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(BUNDLE_IS_SUBTITLE_VISIBLE, mIsSubtitleVisible);
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_crimes);
    }

    public void updateUI() {
        List<Crime> crimes = mRepository.getList();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void addCrime() {
        Crime crime = new Crime();
        mRepository.insert(crime);

        mCallBacks.onCrimeSelected(crime);
        updateUI();

//        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//        startActivity(intent);
    }

    private void updateSubtitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        int numberOfCrimes = mRepository.getList().size();
        String crimesString = getString(R.string.subtitle_format, numberOfCrimes);

        if (!mIsSubtitleVisible)
            crimesString = null;

        activity.getSupportActionBar().setSubtitle(crimesString);
    }

    //view holder responsibility: hold reference to row views.
    private class CrimeHolder extends RecyclerView.ViewHolder {

        private Crime mCrime;
        private TextView mTextViewTitle;
        private TextView mTextViewDate;
        private ImageView mImageViewSolved;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewTitle = itemView.findViewById(R.id.list_row_crime_title);
            mTextViewDate = itemView.findViewById(R.id.list_row_crime_date);
            mImageViewSolved = itemView.findViewById(R.id.imgview_solved);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallBacks.onCrimeSelected(mCrime);
                }
            });
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTextViewTitle.setText(crime.getTitle());
            mTextViewDate.setText(crime.getDate().toString());

            mImageViewSolved.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /*adapter responsibilities:
        1. getItemCounts
        2. create view holder
        3. bind view holder
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public List<Crime> getCrimes() {
            return mCrimes;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_row_crime, parent, false);

            CrimeHolder crimeHolder = new CrimeHolder(view);
            return crimeHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);

            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
    }

    public interface CallBacks {
        void onCrimeSelected(Crime crime);
    }
}