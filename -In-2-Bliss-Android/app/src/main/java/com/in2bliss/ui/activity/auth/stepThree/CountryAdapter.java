package com.in2bliss.ui.activity.auth.stepThree;

import android.content.Context;
import android.view.LayoutInflater; 
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.ArrayAdapter; 
import android.widget.TextView; 
import androidx.annotation.NonNull; 
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.in2bliss.R;

import java.util.ArrayList;
  
public class CountryAdapter extends ArrayAdapter<CountryName> {
  
    public CountryAdapter(Context context,
                          ArrayList<CountryName> algorithmList)
    { 
        super(context, 0, algorithmList); 
    } 
  
    @NonNull
    @Override
    public View getView(int position, @Nullable
                                      View convertView, @NonNull ViewGroup parent) 
    { 
        return initView(position, convertView, parent); 
    } 
  
    @Override
    public View getDropDownView(int position, @Nullable
                                              View convertView, @NonNull ViewGroup parent) 
    { 
        return initView(position, convertView, parent); 
    } 
  
    private View initView(int position, View convertView, 
                          ViewGroup parent) 
    { 
        // It is used to set our custom view. 
        if (convertView == null) { 
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner, parent, false);
        } 
  
        TextView textViewName = convertView.findViewById(R.id.textView);
        AppCompatImageView imageView = convertView.findViewById(R.id.ivFlagImage);
        CountryName currentItem = getItem(position);
  
        // It is used the name to the TextView when the 
        // current item is not null. 
        if (currentItem != null) { 
            textViewName.setText(currentItem.getName());
            imageView.setImageResource(currentItem.getFlagImage());
        }
        return convertView; 
    } 
}