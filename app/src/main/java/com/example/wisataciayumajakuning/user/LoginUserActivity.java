package com.example.wisataciayumajakuning.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.wisataciayumajakuning.MainActivity;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityLoginUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginUserActivity extends AppCompatActivity {
    private ActivityLoginUserBinding binding;
    private FirebaseAuth mAuth;
    private String email, password;
    private static final String TAG = "LoginUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.registerUser.setOnClickListener(v -> {
            Intent intent = new Intent(LoginUserActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(v -> {
            loginUser();
        });

        binding.lupaPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginUserActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private boolean isValidData(){
        //mengambil teks email dan password
        email = binding.inputEmail.getText().toString().trim();
        password = binding.inputPassword.getText().toString().trim();

        //mengecek email dan password user apakah sudah diisi dan sesuai
        if (TextUtils.isEmpty(email)){
            binding.emailLayout.setError("Masukkan Email Anda");
            binding.emailLayout.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Tolong isikan kembali email Anda");
            binding.emailLayout.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)){
            binding.passLayout.setError("Masukkan password Anda");
            binding.passLayout.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void loginUser(){
        //mengecek email dan password user
        boolean isValid = isValidData();
        if (isValid){
            binding.progressBar.setVisibility(View.VISIBLE);
            //masuk ke aplikasi dengan menggunakan email dan password
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginUserActivity.this, "Anda berhasil login", Toast.LENGTH_LONG).show();
                        //user diarahkan ke halaman utama aplikasi
                        reload();
                    } else {
                       try {
                           throw task.getException();
                       } catch (FirebaseAuthInvalidUserException e){
                           binding.emailLayout.setError("User tidak ditemukan. Tolong register terlebih dahulu");
                           binding.emailLayout.requestFocus();
                       } catch (FirebaseAuthInvalidCredentialsException e){
                           binding.emailLayout.setError("Invalid credentials, tolong cek email Anda lagi");
                           binding.emailLayout.requestFocus();
                       } catch (Exception e){
                           Log.e(TAG, e.getMessage());
                           Toast.makeText(LoginUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                       }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void reload(){
        Intent intent = new Intent(LoginUserActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}