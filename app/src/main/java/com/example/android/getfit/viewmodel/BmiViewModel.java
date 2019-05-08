package com.example.android.getfit.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.getfit.db.Bmi;
import com.example.android.getfit.repository.BmiRepository;

import java.util.List;

public class BmiViewModel extends AndroidViewModel {
    private LiveData<List<Bmi>> mAllListBmi;
    private LiveData<Bmi> mLastBmi;
    private BmiRepository mBmiRepository;

    public BmiViewModel(@NonNull Application application) {
        super(application);
        mBmiRepository = new BmiRepository(application);
        mAllListBmi = mBmiRepository.getAllBmi();
        mLastBmi = mBmiRepository.getLastBmi();
    }

    public LiveData<List<Bmi>> getAllBmi() {
        return mAllListBmi;
    }

    public LiveData<Bmi> getLastBmi() {
        return mLastBmi;
    }

    public void addBmi(Bmi bmi) { mBmiRepository.addBmi(bmi); }

    public void deleteBmi(Bmi bmi) {
        mBmiRepository.deleteBmi(bmi);
    }

    public void updateBmi(Bmi bmi) {
        mBmiRepository.updateBmi(bmi);
    }

    public void deleteAllBmi() {
        mBmiRepository.deleteAllBmi();
    }

}
