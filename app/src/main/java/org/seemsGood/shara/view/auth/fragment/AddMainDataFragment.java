package org.seemsGood.shara.view.auth.fragment;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.util.CalendarUtil;
import org.seemsGood.shara.viewModel.AuthViewModel;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMainDataFragment extends Fragment {

    @BindView(R.id.first_name)
    EditText firstNameEdit;
    @BindView(R.id.last_name)
    EditText lastNameEdit;
    @BindView(R.id.father_name)
    EditText fatherNameEdit;

    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.gender_spinner)
    Spinner genderSpinner;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private DatePickerDialog datePickerDialog;

    private Calendar selectedDate;
    private AuthViewModel auth;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_data, container, false);
        ButterKnife.bind(this, view);

        selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        auth = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);

        auth.getAddDataState().observe(this, dataState -> {

            if (dataState == null) {
                Log.d("RegFragment", "onCreateView: FUCK YOU");
                return;
            }

            switch (dataState) {
                case ERROR:
                    break;
                case WAITING:
                    break;
                case PERFORMING:
                    break;
                case COMPLETE:
            }

        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {

            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            dateText.setText(CalendarUtil.getDateString(selectedDate));

        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));

        dateText.setText(CalendarUtil.getDateString(selectedDate));

    }

    @OnClick(R.id.date_button)
    void chooseDate() {
        datePickerDialog.show();
    }

    @OnClick(R.id.finish_button)
    void finish() {

        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String fatherName = fatherNameEdit.getText().toString();

        int gender = genderSpinner.getSelectedItemPosition();

        boolean correct = isFirstNameValid() && isLastNameValid() && isFatherNameValid();

        if (correct) auth.addData(firstName, lastName, fatherName, gender, selectedDate.getTime());
    }

    boolean isFirstNameValid(){
        return isFieldValid(firstNameEdit, "Wrong firstname");
    }

    boolean isLastNameValid(){
        return isFieldValid(lastNameEdit, "Wrong lastname");
    }

    boolean isFatherNameValid(){
        return isFieldValid(fatherNameEdit, "Wrong fathername");
    }

    boolean isFieldValid(EditText text, String error){
        String name = text.getText().toString();
        if(name.contains(" ") || TextUtils.isEmpty(name)){
            text.setError(error);
            return false;
        }
        return true;
    }

}
