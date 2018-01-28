package org.seemsGood.shara.viewModel;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.seemsGood.shara.model.NewUser;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AuthViewModel extends ViewModel {

    public enum AuthState {UNAUTHORIZED, REGISTRATION, ADD_DATA, ADD_PHONE, AUTHORIZED}

    public enum State {WAITING, ERROR, PERFORMING, PERFORMING_2, COMPLETE}
    public enum PhoneState {WAITING, ERROR, CODE_ERROR, WAITING_CODE, CODE_RECEIVED, PERFORMING, COMPLETE}

    private MutableLiveData<AuthState> authState = new MutableLiveData<>();

    private MutableLiveData<State> signInState = new MutableLiveData<>();
    private MutableLiveData<State> regState = new MutableLiveData<>();
    private MutableLiveData<State> addDataState = new MutableLiveData<>();
    private MutableLiveData<PhoneState> addPhoneState = new MutableLiveData<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private NewUser newUser;
    private FirebaseUser user;
    private String verificationId;
    public String code;

    public AuthViewModel() {
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null)
            authState.setValue(AuthState.UNAUTHORIZED);
        else authState.setValue(AuthState.AUTHORIZED);

        signInState.setValue(State.WAITING);
        regState.setValue(State.WAITING);
        addDataState.setValue(State.WAITING);
        addPhoneState.setValue(PhoneState.WAITING);

    }

    public MutableLiveData<AuthState> getAuthState() {
        return authState;
    }

    public MutableLiveData<State> getSignInState() {
        return signInState;
    }

    public MutableLiveData<State> getRegState() {
        return regState;
    }

    public MutableLiveData<State> getAddDataState() {
        return addDataState;
    }

    public MutableLiveData<PhoneState> getAddPhoneState() {
        return addPhoneState;
    }

    public void showRegistration() {
        authState.setValue(AuthState.REGISTRATION);
    }

    public void backToSignIn() {
        authState.setValue(AuthState.UNAUTHORIZED);
    }

    public void signIn(String email, String password) {
        signInState.setValue(State.PERFORMING);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d("AuthViewModel", "signIn: " + authResult);
                    if (authResult.getUser() != null) {
                        signInState.setValue(State.COMPLETE);
                        authState.setValue(AuthState.AUTHORIZED);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("AuthViewModel", "signIn: " + e.getLocalizedMessage());
                    signInState.setValue(State.ERROR);
                });
    }

    public void register(String email, String password) {
        regState.setValue(State.PERFORMING);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d("AuthViewModel", "registerSUC: " + authResult.getUser());
                    regState.setValue(State.COMPLETE);
                    authState.setValue(AuthState.ADD_DATA);
                    user = authResult.getUser();
                    newUser = new NewUser(email, new Date());
                })
                .addOnFailureListener(e -> {
                    Log.d("AuthViewModel", "registerFAIL: " + e.getMessage());
                    regState.setValue(State.ERROR);
                });

    }

    public void addData(String firstName, String lastName, String fatherName, int gender, Date birthDate) {

        long days = TimeUnit.MILLISECONDS.toDays(new Date().getTime() - birthDate.getTime());
        int age = (int) (days / 365.2422);

        newUser.setData(firstName, lastName, fatherName, gender, age, birthDate);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(user.getUid())
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AuthViewModel", "addData: ");
                    addDataState.setValue(State.COMPLETE);
                    authState.setValue(AuthState.ADD_PHONE);
                })
                .addOnFailureListener(e -> {
                    Log.d("AuthViewModel", "addData: " + e.getMessage());
                    addDataState.setValue(State.ERROR);
                });

    }

    public void sendCode(String phone, Activity activity) {

        addPhoneState.setValue(PhoneState.PERFORMING);
        PhoneAuthProvider.getInstance(auth)
                .verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, activity,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                code = phoneAuthCredential.getSmsCode();
                                regWithCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Log.d("AuthViewModel", "onVerificationFailed: " + e.getMessage());
                                addPhoneState.setValue(PhoneState.CODE_ERROR);
                            }

                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = s;
                                addPhoneState.setValue(PhoneState.WAITING_CODE);
                            }
                        });

    }

    public void regWithCode(String code){
        regWithCredential(PhoneAuthProvider.getCredential(verificationId, code));
    }

    private void regWithCredential(AuthCredential credential){
        user.linkWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    addPhoneState.setValue(PhoneState.COMPLETE);
                    authState.setValue(AuthState.AUTHORIZED);
                })
                .addOnFailureListener(e -> {
                    addPhoneState.setValue(PhoneState.ERROR);
                    Log.d("AuthViewModel", "onVerificationCompleted: " + e.getMessage());
                });
    }

}