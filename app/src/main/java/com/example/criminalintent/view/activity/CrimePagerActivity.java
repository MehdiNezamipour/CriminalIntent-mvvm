package com.example.criminalintent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.criminalintent.R;
import com.example.criminalintent.adapters.CrimeViewPagerAdapter;
import com.example.criminalintent.databinding.ActivityCrimePagerBinding;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeDBRepository;
import com.example.criminalintent.repository.IRepository;
import com.example.criminalintent.view.fragment.CrimeDetailFragment;

import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeDetailFragment.Callbacks {

    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeId";
    public static final String TAG = "CPA";

    private ActivityCrimePagerBinding mActivityCrimePagerBinding;

    private IRepository mRepository;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCrimePagerBinding = DataBindingUtil.setContentView(this, R.layout.activity_crime_pager);


        mRepository = CrimeDBRepository.getInstance(this);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int position = mRepository.getPosition(crimeId);


        setUI(position);
    }

    private void setUI(int position) {
        FragmentStateAdapter adapter = new CrimeViewPagerAdapter(this, mRepository.getList());
        mActivityCrimePagerBinding.crimeViewPager.setAdapter(adapter);

        //this method "must" be placed after setAdapter.
        mActivityCrimePagerBinding.crimeViewPager.setCurrentItem(position);
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        //nothing
    }

}