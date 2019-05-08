package com.example.android.getfit.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.example.android.getfit.utils.DateConverter;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface BmiDao {

    // Dao method to get all bmi
    @Query("SELECT * FROM Bmi ORDER BY ID DESC ")
    LiveData<List<Bmi>> getAllBmi();

    // Dao method to get last bmi
    @Query("SELECT * FROM Bmi ORDER BY ID DESC LIMIT 1")
    LiveData<Bmi> getlastBmi();

    // Dao method to insert bmi
    @Insert(onConflict = REPLACE)
    void insertBmi(Bmi bmi);

    // Dao method to delete bmi
    @Delete
    void deleteBmi(Bmi bmi);

    // Dao method to delete all bmi
    @Query("DELETE FROM Bmi")
    void deleteAllBmi();

    // Dao method to update bmi
    @Update(onConflict = REPLACE)
    void updateBmi(Bmi bmi);
}
