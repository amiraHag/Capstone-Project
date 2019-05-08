package com.example.android.getfit.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.getfit.db.BmiDao;
import com.example.android.getfit.db.BmiDb;
import com.example.android.getfit.db.Bmi;

import java.util.List;
public class BmiRepository {

    private LiveData<List<Bmi>> mAllBmi;
    private LiveData<Bmi> mLastBmi;
    private BmiDao mBmiDao;

    public BmiRepository(@NonNull Application application) {
        BmiDb bmiDatabase = BmiDb.getInstance(application);
        mBmiDao = bmiDatabase.bmiDao();
        mAllBmi = mBmiDao.getAllBmi();
        mLastBmi = mBmiDao.getlastBmi();
    }

    public LiveData<Bmi> getLastBmi() {
        return mLastBmi;
    }
    public LiveData<List<Bmi>> getAllBmi() {
        return mAllBmi;
    }
    public void addBmi(Bmi bmi) {
        new AddBmi().execute(bmi);
    }
    public void deleteBmi(Bmi bmi) {
        new DeleteBmi().execute(bmi);
    }
    public void deleteAllBmi() {
        new DeleteAllBmi().execute();
    }
    public void updateBmi(Bmi bmi) {
        new UpdateBmi().execute(bmi);
    }


    public class AddBmi extends AsyncTask<Bmi, Void, Void> {
        @Override
        protected Void doInBackground(Bmi... bmiList) {
            mBmiDao.insertBmi(bmiList[0]);
            return null;
        }
    }

    public class DeleteBmi extends AsyncTask<Bmi, Void, Void> {
        @Override
        protected Void doInBackground(Bmi... bmiList) {
            mBmiDao.deleteBmi(bmiList[0]);
            return null;
        }
    }


    public class UpdateBmi extends AsyncTask<Bmi, Void, Void> {
        @Override
        protected Void doInBackground(Bmi... bmiList) {
            mBmiDao.updateBmi(bmiList[0]);
            return null;
        }
    }

    public class DeleteAllBmi extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mBmiDao.deleteAllBmi();
            return null;
        }
    }


}
