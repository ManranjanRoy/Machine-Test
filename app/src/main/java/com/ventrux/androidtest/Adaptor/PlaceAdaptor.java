package com.ventrux.androidtest.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventrux.androidtest.MapsActivity;
import com.ventrux.androidtest.Model.PlaceModel;
import com.ventrux.androidtest.Model.StaticData;
import com.ventrux.androidtest.R;

import java.text.DecimalFormat;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PlaceAdaptor extends RecyclerView.Adapter<PlaceAdaptor.ImageViewHolder> {
private Context mContext;
private List<PlaceModel> mUploads;
    private  OnitemClickListner mlistner;

    public interface  OnitemClickListner{

    }

    public  void setonItemClickListner(OnitemClickListner listner){
        mlistner=listner;

    }
public PlaceAdaptor(Context mContext, List<PlaceModel> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
        }

@Override
public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_place, parent, false);
    return new ImageViewHolder(v,mlistner);
        }

@Override
public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final PlaceModel uploadCurrent = mUploads.get(position);

            holder.pname.setText(uploadCurrent.getPlace());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mContext, MapsActivity.class);
                    i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    StaticData.postion=1;
                    StaticData.location=uploadCurrent.getPlace();
                    mContext.startActivity(i);
                }
            });

    }

@Override
public int getItemCount() {
        return mUploads.size();
        }


public class ImageViewHolder extends RecyclerView.ViewHolder {
    public TextView pname;

    RelativeLayout ll;
    public ImageViewHolder(View itemView, final OnitemClickListner listner) {
        super(itemView);
        pname = itemView.findViewById(R.id.name);

    }


}


}