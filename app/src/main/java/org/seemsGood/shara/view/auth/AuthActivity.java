package org.seemsGood.shara.view.auth;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.auth.fragment.AddMainDataFragment;
import org.seemsGood.shara.view.auth.fragment.AddPhoneFragment;
import org.seemsGood.shara.view.auth.fragment.LaunchFragment;
import org.seemsGood.shara.view.auth.fragment.RegFragment;
import org.seemsGood.shara.view.auth.fragment.SignInFragment;
import org.seemsGood.shara.viewModel.AuthViewModel;

public class AuthActivity extends AppCompatActivity implements Observer<AuthViewModel.AuthState> {

    private AuthViewModel auth;

    private Fragment openedFragment;
    private boolean debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        auth = ViewModelProviders.of(this).get(AuthViewModel.class);
        auth.getAuthState().observe(this, this);
    }

    @Override
    public void onBackPressed() {

        if (openedFragment instanceof RegFragment) {
            auth.backToSignIn();
            return;
        } else if (openedFragment instanceof AddMainDataFragment ||
                openedFragment instanceof AddPhoneFragment) {
            loadFragment(new LaunchFragment());
        } else if (openedFragment instanceof LaunchFragment) return;

        super.onBackPressed();
    }

    @Override
    public void onChanged(@Nullable AuthViewModel.AuthState authState) {

        Log.d("AuthActivity", "onCreate: " + authState);

        if (authState == null) {
            Log.d("AuthActivity", "onCreate: FUCK YOU");
            return;
        }

        if(debug) {
            loadFragment(new LaunchFragment());
            return;
        }

        switch (authState) {
            case AUTHORIZED:
                openedFragment = new LaunchFragment();
                break;
            case UNAUTHORIZED:
                openedFragment = new SignInFragment();
                break;
            case REGISTRATION:
                openedFragment = new RegFragment();
                break;
            case ADD_DATA:
                openedFragment = new AddMainDataFragment();
                break;
            case ADD_PHONE:
                openedFragment = new AddPhoneFragment();
                break;
            default:
                return;
        }

        loadFragment(openedFragment);
    }


    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.launch_fragment_container, fragment)
                .commit();
    }

}
