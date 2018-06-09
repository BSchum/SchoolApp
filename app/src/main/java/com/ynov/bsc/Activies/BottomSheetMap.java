package com.ynov.bsc.Activies;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brice.messagemanager.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Brice on 05/06/2018.
 */

public class BottomSheetMap extends BottomSheetDialogFragment {
    View v;

    ArrayList<String> data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bottom_sheet_frag, container, false);
        TextView school = v.findViewById(R.id.school);
        TextView address = v.findViewById(R.id.address);
        TextView nbelevetext = v.findViewById(R.id.nbeleve);
        TextView distance = v.findViewById(R.id.distance);
        school.setText(getArguments().getString("nom"));
        address.setText(getArguments().getString("addresse"));
        nbelevetext.setText(getArguments().getString("nbeleve") + " élèves");
        distance.setText(String.valueOf(getArguments().getFloat("distance"))+"km");

        int nbeleve = Integer.parseInt(getArguments().getString("nbeleve"));
        ImageView image = v.findViewById(R.id.nbeleveimage);
        if(nbeleve < 50){
            image.setImageResource(R.drawable.ko);
        }
        else{
            image.setImageResource(R.drawable.ok);
        }
        return v;
    }



}
