package com.example.wisataciayumajakuning.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.wisataciayumajakuning.MainActivity;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityRegisterUserBinding;
import com.example.wisataciayumajakuning.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity {
    private ActivityRegisterUserBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private String username, email, phone, password, confirmationPass;
    private static final String TAG = "RegisterUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        binding.btnRegister.setOnClickListener(v -> {
            registerUser();
        });
    }

    public static boolean isValidEmail(CharSequence email) {
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isValidData(){
        username = binding.inputUsername.getText().toString().trim();
        email = binding.inputEmail.getText().toString().trim();
        phone = binding.inputPhoneNumber.getText().toString().trim();
        password = binding.inputPassword.getText().toString().trim();
        confirmationPass = binding.konfirmasiPassword.getText().toString().trim();

        if ("".equals(username) && "".equals(email) && "".equals(phone)
                && "".equals(password) && "".equals(confirmationPass)) {
            Toast.makeText(this, "Anda belum mengisi data Anda", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmail(email)) {
            binding.inputEmail.setError("Periksa kembali alamat email Anda");
            binding.inputEmail.requestFocus();
            return false;
        } else if (!confirmationPass.equals(password)) {
            binding.konfirmasiPassword.setError("Password Anda tidak sama");
            binding.konfirmasiPassword.requestFocus();
            return false;
        } else if (password.length() < 6) {
            binding.inputPassword.setError("Password Anda harus lebih dari 6 karakter");
            binding.inputPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void registerUser(){
        mAuth = FirebaseAuth.getInstance();
        boolean isValid = isValidData();
        if (isValid){
            binding.progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterUserActivity.this, "Anda berhasil registrasi", Toast.LENGTH_SHORT).show();

                        User user = new User(username, email, phone);

                        currentUser = mAuth.getCurrentUser();

                        mDatabase.child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    currentUser.sendEmailVerification();

                                    Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterUserActivity.this, "Anda gagal registrasi", Toast.LENGTH_SHORT).show();
                                }
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e){
                            binding.inputPassword.setError("Password Anda terlalu lemah. Gunakan campuran alfabet, angka, dan simbol");
                            binding.inputPassword.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e){
                            binding.inputPassword.setError("Email Anda invalid atau sudah digunakan untuk registrasi");
                            binding.inputPassword.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e){
                            binding.inputPassword.setError("Anda sudah registrasi dengan email ini. Gunakan email yang lain");
                            binding.inputPassword.requestFocus();
                        } catch (Exception e){
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(RegisterUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}