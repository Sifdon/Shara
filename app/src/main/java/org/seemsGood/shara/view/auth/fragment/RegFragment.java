package org.seemsGood.shara.view.auth.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.seemsGood.shara.R;
import org.seemsGood.shara.viewModel.AuthViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegFragment extends Fragment {

    @BindView(R.id.email)
    EditText emailEdit;
    @BindView(R.id.password)
    EditText passwordEdit;
    @BindView(R.id.password_repeat)
    EditText passwordRepeatEdit;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private AuthViewModel auth;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg, container, false);
        ButterKnife.bind(this,view);
        auth = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);

        auth.getRegState().observe(this, regState -> {

            if(regState == null){
                Log.d("RegFragment", "onCreateView: FUCK YOU");
                return;
            }

            switch (regState){
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

    @OnClick(R.id.continue_button)
    void register() {

        resetErrors();

        boolean correct = isEmailValid() && isPasswordValid() && isPasswordsEqual();

        if (correct)
            auth.register(emailEdit.getText().toString(), passwordEdit.getText().toString());
    }

    @OnClick(R.id.back_button)
    void back(){
        auth.backToSignIn();
    }

    private void resetErrors(){
        emailEdit.setError(null);
        passwordEdit.setError(null);
        passwordRepeatEdit.setError(null);
    }

    private boolean isPasswordValid() {
        String password = passwordEdit.getText().toString();
        if(password.length()<8){
            passwordEdit.setError("Password to short");
            return false;
        }
        return true;
    }

    private boolean isPasswordsEqual() {
        String password = passwordEdit.getText().toString();
        String passwordRepeat = passwordRepeatEdit.getText().toString();
        if(!password.equals(passwordRepeat)){
            passwordRepeatEdit.setError("Passwords are not equal");
            return false;
        }
        return true;
    }

    private boolean isEmailValid(){
        String email = emailEdit.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailEdit.setError("Empty email");
            return false;
        }
        return true;
    }

}
