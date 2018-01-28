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

public class SignInFragment extends Fragment {

    @BindView(R.id.email_edit)
    EditText emailEdit;
    @BindView(R.id.password_edit)
    EditText passwordEdit;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private AuthViewModel auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this,view);
        auth = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);

        auth.getSignInState().observe(this, signInState -> {

            if(signInState==null){
                Log.d("SignInFragment", "onCreateView: FUCK YOU");
                return;
            }

            switch (signInState){
                case WAITING:
                    Log.d("SignInFragment", "onCreateView: HELLO");
                    break;
                case ERROR:
                    emailEdit.setError("Wrong email");
                    progressBar.setVisibility(View.GONE);
                    emailEdit.setEnabled(true);
                    passwordEdit.setEnabled(true);
                    break;
                case PERFORMING:
                    progressBar.setVisibility(View.VISIBLE);
                    emailEdit.setEnabled(false);
                    passwordEdit.setEnabled(false);
                case COMPLETE:
                    progressBar.setVisibility(View.GONE);
                    Log.d("SignInFragment", "onCreateView: " + signInState);
            }
        });

        return view;
    }

    @OnClick(R.id.register_button)
    void showRegistration(){
        auth.showRegistration();
    }

    @OnClick(R.id.sign_in_button)
    void signIn() {

        resetErrors();

        boolean correct = isEmailValid() && isPasswordValid();

        if (correct) auth.signIn(emailEdit.getText().toString(), passwordEdit.getText().toString());
    }

    boolean isEmailValid(){

        String email = emailEdit.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Empty email");
            return false;
        }
        return true;
    }

    boolean isPasswordValid(){
        String password = passwordEdit.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Empty password");
            return false;
        }
        return true;
    }

    void resetErrors(){
        emailEdit.setError(null);
        passwordEdit.setError(null);
    }

}