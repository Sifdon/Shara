package org.seemsGood.shara.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import org.seemsGood.shara.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(@NonNull Context context) {
        super(context, R.layout.template_spinner_item_head);
        setDropDownViewResource(R.layout.template_spinner_item);
    }

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }
}
