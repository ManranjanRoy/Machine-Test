package com.ventrux.androidtest.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.libraries.places.api.model.Place;
import com.ventrux.androidtest.Adaptor.PlaceAdaptor;
import com.ventrux.androidtest.Model.PlaceModel;
import com.ventrux.androidtest.R;
import com.ventrux.androidtest.Sqlite.DatabaseHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity {
    List<PlaceModel> cartMedicineModels;
    RecyclerView chapterrecycler;
    DatabaseHelper myDb;
    PlaceAdaptor medicinelistAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        myDb = new DatabaseHelper(this);
        cartMedicineModels=new ArrayList<>();
        chapterrecycler = findViewById(R.id.recyclerview);
        chapterrecycler.setHasFixedSize(true);
        chapterrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addproducts();
    }
    private void addproducts() {
        cartMedicineModels.clear();
        Cursor ress = myDb.getAllData();
        if (ress.getCount() == 0) {
            // show message
            Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();
            return;
        } else {
            Cursor res = myDb.getAllData();
            // //String id,title,details,code,price,category_name,store_name,quantity,totalprice;
            StringBuffer buffer = new StringBuffer();
            cartMedicineModels.clear();
            while (res.moveToNext()) {
                cartMedicineModels.add(new PlaceModel(
                        res.getString(0), res.getString(1)) {
                });
                buffer.append("id :" + res.getString(0));
                buffer.append("title :" + res.getString(1));



            }
            medicinelistAdaptor = new PlaceAdaptor(getApplicationContext(), cartMedicineModels);
            chapterrecycler.setAdapter(medicinelistAdaptor);

        }
    }
}
