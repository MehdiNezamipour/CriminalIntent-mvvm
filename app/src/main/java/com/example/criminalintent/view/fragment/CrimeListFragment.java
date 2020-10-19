package com.example.criminalintent.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.criminalintent.R;
import com.example.criminalintent.adapters.CrimeAdapter;
import com.example.criminalintent.databinding.FragmentCrimeListBinding;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeDBRepository;
import com.example.criminalintent.repository.IRepository;

import java.util.List;

public class CrimeListFragment extends Fragment {

    public static final String BUNDLE_IS_SUBTITLE_VISIBLE = "isSubtitleVisible";
    private boolean mIsSubtitleVisible = false;

    private FragmentCrimeListBinding mFragmentCrimeListBinding;

    private IRepository<Crime> mRepository;
    private CrimeAdapter mAdapter;
    private CrimeAdapter.OnCrimeSelectListener mOnCrimeSelectListener;


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

        if (context instanceof CrimeAdapter.OnCrimeSelectListener) {
            mOnCrimeSelectListener = (CrimeAdapter.OnCrimeSelectListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement onCrimeSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnCrimeSelectListener = null;
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
        mFragmentCrimeListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime_list, container, false);
        mFragmentCrimeListBinding.recyclerViewCrimes.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return mFragmentCrimeListBinding.getRoot();
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


    public void updateUI() {
        List<Crime> crimes = mRepository.getList();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes, getContext());
            mFragmentCrimeListBinding.recyclerViewCrimes.setAdapter(mAdapter);
            mAdapter.setOnCrimeSelectListener(mOnCrimeSelectListener);


        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void addCrime() {
        Crime crime = new Crime();
        mRepository.insert(crime);
        mOnCrimeSelectListener.onCrimeSelected(crime);
        updateUI();

    }

    private void updateSubtitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        int numberOfCrimes = mRepository.getList().size();
        String crimesString = getString(R.string.subtitle_format, numberOfCrimes);

        if (!mIsSubtitleVisible)
            crimesString = null;

        activity.getSupportActionBar().setSubtitle(crimesString);
    }


}