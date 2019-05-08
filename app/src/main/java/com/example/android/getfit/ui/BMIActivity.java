package com.example.android.getfit.ui;


import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.getfit.utils.Constants;
import com.example.android.getfit.widget.MyAppWidgetService;
import com.example.android.getfit.R;
import com.example.android.getfit.adapter.BmiAdapter;
import com.example.android.getfit.db.Bmi;
import com.example.android.getfit.repository.OnItemClickListener;
import com.example.android.getfit.utils.RecyclerItemDecoration;
import com.example.android.getfit.viewmodel.BmiViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BMIActivity extends AppCompatActivity {
    BmiViewModel mBmiViewModel;
    @BindView(R.id.fab)  FloatingActionButton fab;
    @BindView(R.id.recyclerViewBmi) RecyclerView mBmiRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        mBmiRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final BmiAdapter bmiAdapter = new BmiAdapter();
        mBmiRecyclerView.setAdapter(bmiAdapter);
        mBmiRecyclerView.addItemDecoration(new RecyclerItemDecoration(20));
        mBmiViewModel = ViewModelProviders.of(this).get(BmiViewModel.class);
        mBmiViewModel.getAllBmi().observe(this, new Observer<List<Bmi>>() {
            @Override
            public void onChanged(@Nullable List<Bmi> bmis) {
                bmiAdapter.setBmiList(bmis);
            }
        });


        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override

                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Bmi mCurrentBmi = bmiAdapter.getBmiAtPosition(position);
                        if(position == 0  ) {
                            if (bmiAdapter.getItemCount() > 1) {
                                MyAppWidgetService.initWidgetContent(getBaseContext(), bmiAdapter.getBmiAtPosition(position + 1), true);
                            }
                            if (bmiAdapter.getItemCount() <= 1) {
                                MyAppWidgetService.initEmptyWidgetContent(getBaseContext(),  false);
                            }
                        }
                        Constants.displayToast(BMIActivity.this, getString(R.string.delete_bmi));

                        mBmiViewModel.deleteBmi(mCurrentBmi);
                    }
                });
        // Attach the item touch helper to the recycler view.
        helper.attachToRecyclerView(mBmiRecyclerView);

        bmiAdapter.setOnItemClickListener(new OnItemClickListener()  {

            @Override
            public void onItemClick(View v, int position) {
                Bmi currentBmi = bmiAdapter.getBmiAtPosition(position);
                showUpdateDialog(currentBmi, position);


            }
        });
    }

    public void showDialog() {
        fab.hide();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_bmi_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText editWeight = dialog.findViewById(R.id.et_weight);
        final EditText editHeight = dialog.findViewById(R.id.et_height);
        TextView textViewAdd = dialog.findViewById(R.id.textViewAdd);
        TextView textViewCancel = dialog.findViewById(R.id.textViewCancel);
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Weight = editWeight.getText().toString();
                String Height = editHeight.getText().toString();
                Double weightValue= Double.parseDouble(Weight);
                Double heightValue = Double.parseDouble(Height);
                Date createdAt = Calendar.getInstance().getTime();
                Bmi lastBmiAdded = new Bmi(getApplicationContext(), weightValue, heightValue, createdAt);
                mBmiViewModel.addBmi(lastBmiAdded);
                MyAppWidgetService.initWidgetContent(getBaseContext(), lastBmiAdded, true);
                fab.show();
                dialog.dismiss();
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fab.show();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public void showUpdateDialog(final Bmi bmi, final int position) {
        fab.hide();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_bmi_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText editWeight = dialog.findViewById(R.id.et_weight);
        final EditText editHeight = dialog.findViewById(R.id.et_height);
        editWeight.setText(" ");
        editHeight.setText("");
        TextView textViewAdd = dialog.findViewById(R.id.textViewAdd);
        textViewAdd.setText(getString(R.string.update_bmi));
        TextView textViewCancel = dialog.findViewById(R.id.textViewCancel);
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update bmi
                String Weight = editWeight.getText().toString();
                String Height = editHeight.getText().toString();
                Double weightValue= Double.parseDouble(Weight);
                Double heightValue = Double.parseDouble(Height);
                String personBmi = Constants.getBmiValue(weightValue,heightValue) ;
                String bmiStatusDescription = Constants.getBmiStatus(getApplicationContext(),weightValue,heightValue);
                bmi.setBmiValue(personBmi);
                bmi.setBmiStatusDescription(bmiStatusDescription) ;


                if(position==0) {
                    MyAppWidgetService.initWidgetContent(getBaseContext(), bmi, true);

                }

                mBmiViewModel.updateBmi(bmi);
                fab.show();
                dialog.dismiss();
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fab.show();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_all:
                // Add a toast just for confirmation
                Constants.displayToast(this,getString(R.string.delete_all_bmi));
                // Delete the existing data.
                mBmiViewModel.deleteAllBmi();
                MyAppWidgetService.initEmptyWidgetContent(getBaseContext(),  false);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
