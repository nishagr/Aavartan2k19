package com.technocracy.app.aavartan.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.api.APIServices;
import com.technocracy.app.aavartan.api.AppClient;
import com.technocracy.app.aavartan.api.data_models.LoginData;
import com.technocracy.app.aavartan.ui.activities.MainActivity;
import com.technocracy.app.aavartan.utils.SessionManager;
import com.technocracy.app.aavartan.utils.ValidationManager;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private Button buLogin;
    private TextInputEditText etUsername, etEmail, etPassword;

    private boolean isValidUserName = false, isValidEmail = false, isValidPassword = false;
    private String email, password, username;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        setListeners();
        return view;
    }

    private void initView(View view) {

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        buLogin = view.findViewById(R.id.buLogin);

    }

    private void setListeners() {

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationManager.isFieldEmpty(Objects.requireNonNull(etUsername.getText()).toString())) {
                    etUsername.setError("Field Cannot be Empty");
                    isValidUserName = false;
                } else if (!ValidationManager.isValidMobileNumber(etUsername.getText().toString())) {
                    etUsername.setError("Enter Valid Mobile Number");
                    isValidUserName = false;
                } else isValidUserName = true;
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                isValidEmail = false;
                if (ValidationManager.isFieldEmpty(Objects.requireNonNull(etEmail.getText()).toString())) {
                    etEmail.setError("Field Cannot be Empty");
                } else if (ValidationManager.isEmailValid(etEmail.getText().toString())) {
                    etEmail.setError("Enter Valid Email");
                } else isValidEmail = true;
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                isValidPassword = false;
                if (ValidationManager.isFieldEmpty(Objects.requireNonNull(etPassword.getText()).toString())) {
                    etPassword.setError("Field Cannot be Empty");
                } else isValidPassword = true;
            }
        });

        buLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidUserName && isValidEmail && isValidPassword) {
                    username = String.valueOf(etUsername.getText());
                    email = String.valueOf(etEmail.getText());
                    password = String.valueOf(etPassword.getText());
                    apiCall();
                } else {
                    Toasty.error(Objects.requireNonNull(getActivity()), "One or more Fields are Incorrect", Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void apiCall() {

        APIServices apiServices = AppClient.getInstance().createService(APIServices.class);
        Call<LoginData> call = apiServices.getLogin(username, email, password);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(@NonNull Call<LoginData> call, @NonNull Response<LoginData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getUserToken().equals("")) {
                            Log.d("LOG Login Key",response.body().getUserToken());
                            SessionManager.setUserToken(response.body().getUserToken());
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginData> call, @NonNull Throwable t) {
                Log.d("LOG Login :", t.toString());
            }
        });

    }

}
