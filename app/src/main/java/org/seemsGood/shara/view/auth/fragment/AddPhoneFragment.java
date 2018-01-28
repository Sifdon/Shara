package org.seemsGood.shara.view.auth.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.custom.PhoneEditText;
import org.seemsGood.shara.viewModel.AuthViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPhoneFragment extends Fragment {

    @BindView(R.id.phone)
    PhoneEditText phoneEdit;
    @BindView(R.id.code)
    EditText codeEdit;

    @BindView(R.id.code_input_layout)
    TextInputLayout codeLayout;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private AuthViewModel auth;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_phone, container, false);
        ButterKnife.bind(this, view);

        auth = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);

        auth.getAddPhoneState().observe(this, phoneState -> {

            Log.d("AddPhoneFragment", "onCreateView: "+phoneState);

            if (phoneState == null) {
                Log.d("RegFragment", "onCreateView: FUCK YOU");
                return;
            }

            switch (phoneState) {
                case ERROR:
                    break;
                case CODE_ERROR:
                    break;
                case WAITING:
                    break;
                case WAITING_CODE:
                    codeLayout.setVisibility(View.VISIBLE);
                    break;
                case CODE_RECEIVED:
                    codeEdit.setText(auth.code);
                    break;
                case PERFORMING:
                    break;
                case COMPLETE:
            }

        });
        return view;
    }

    @OnClick(R.id.continue_button)
    void continueAuth() {

        resetErrors();

        AuthViewModel.PhoneState state = auth.getAddPhoneState().getValue();

        if (state == AuthViewModel.PhoneState.WAITING_CODE) {
            if (isCodeValid()) auth.regWithCode(codeEdit.getText().toString());
        } else {
            if (isPhoneValid()) auth.sendCode(phoneEdit.getPhoneNumber(), getActivity());
        }

    }

    void resetErrors(){
        phoneEdit.setError(null);
        codeEdit.setError(null);
    }

    boolean isPhoneValid(){
        if(phoneEdit.getPhoneNumber().length()!=12){
            phoneEdit.setError("Short phone number");
            return false;
        }
        return true;
    }

    boolean isCodeValid(){
        if(codeEdit.getText().length()!=6){
            codeEdit.setError(null);
            return false;
        }
        return true;
    }


//        fabForward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (verificationId.equals("")) {
//                    if (phoneEdit.getPhoneNumber().length() != 12) {
//                        phoneEdit.setError("Short phone number");
//                    } else {
//                        progressForward.setVisibility(View.VISIBLE);
//                        fabForward.setVisibility(View.GONE);
//                        phoneEdit.setEnabled(false);
//                        sendRequest(phoneEdit.getPhoneNumber());
//                    }
//                } else if (!codeEdit.getText().toString().equals("")) {
//                    signIn();
//                }
//            }
//        });
//
//
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                if (auth.getCurrentUser() == null) {
//
//                    logoView.animate()
//                            .yBy(-60 * density)
//                            .setDuration(500);
//
//                    phoneLayout.setVisibility(View.VISIBLE);
//                    phoneLayout.animate()
//                            .alpha(1)
//                            .setStartDelay(200)
//                            .setDuration(300)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    phoneEdit.requestFocus();
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                                }
//                            });
//
//                } else {
//
//                    mottoView
//                            .animate()
//                            .setDuration(500)
//                            .alpha(0);
//
//                    logoView
//                            .animate()
//                            .setDuration(500)
//                            .alpha(0)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    startActivity(new Intent(getBaseContext(), MainActivity.class));
//                                }
//                            });
//                }
//
//            }
//        }, delay);

//    }

//    private void sendRequest(String phone) {
//
//        PhoneAuthProvider.getInstance(auth)
//                .verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                        Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential.getSmsCode());
//                        codeEdit.setText(phoneAuthCredential.getSmsCode());
//                        signIn(phoneAuthCredential);
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//                        Log.d(TAG, "onVerificationFailed: " + e);
//                        phoneEdit.setError("Wrong phone number");
//                        phoneEdit.setEnabled(true);
//                        fabForward.setVisibility(View.VISIBLE);
//                        progressForward.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        Log.d(TAG, "onCodeSent: " + s);
//                        verificationId = s;
//                        fabForward.setVisibility(View.VISIBLE);
//                        progressForward.setVisibility(View.GONE);
//                        ((View)codeEdit.getParent()).setVisibility(View.VISIBLE);
//                        ((View)phoneEdit.getParent()).setVisibility(View.GONE);
//                    }
//
//                });
//    }
//
//    private void signIn(PhoneAuthCredential credential){
//        codeEdit.setEnabled(false);
//        progressForward.setVisibility(View.VISIBLE);
//        fabForward.setVisibility(View.GONE);
//
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "onComplete: verification+");
//
//                            logoView.animate()
//                                    .y(30*density)
//                                    .setDuration(500);
//
//                            mottoView.animate()
//                                    .setDuration(500)
//                                    .alpha(0);
//
//                            phoneLayout.animate()
//                                    .setDuration(500)
//                                    .y(getResources().getDisplayMetrics().heightPixels)
//                                    .alpha(0)
//                                    .setListener(new AnimatorListenerAdapter() {
//                                        @Override
//                                        public void onAnimationEnd(Animator animation) {
//                                            getSupportFragmentManager().beginTransaction()
//                                                    .add(R.id.launch_fragment_container, new RegFragment())
//                                                    .commit();
//
//                                            phoneLayout.setVisibility(View.GONE);
//                                        }
//                                    });
//
//                        } else {
//                            Log.d(TAG, "onComplete: " + task.getException());
//                        }
//                    }
//                });
//    }
}
