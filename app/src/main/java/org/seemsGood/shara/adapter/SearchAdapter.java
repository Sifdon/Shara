package org.seemsGood.shara.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import org.seemsGood.shara.R;

public class SearchAdapter extends ArrayAdapter<String> {

    public SearchAdapter(@NonNull Context context){
        super(context, R.layout.template_spinner_item);
    }

}
