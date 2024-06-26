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
        //mendapatkan referensi database user
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
            binding.emailLayout.setError("Periksa kembali alamat email Anda");
            binding.emailLayout.requestFocus();
            return false;
        } else if (!confirmationPass.equals(password)) {
            binding.konfirmasiLayout.setError("Password Anda tidak sama");
            binding.konfirmasiLayout.requestFocus();
            return false;
        } else if (password.length() < 6) {
            binding.passwordLayout.setError("Password Anda harus lebih dari 6 karakter");
            binding.passwordLayout.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void registerUser(){
        //mengisialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //mengecek data user yang telah diinputkan di edit text
        boolean isValid = isValidData();
        if (isValid){
            binding.progressBar.setVisibility(View.VISIBLE);
            //membuat akun user dengan menggunakan email dan password
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterUserActivity.this, "Anda berhasil registrasi", Toast.LENGTH_SHORT).show();

                        User user = new User(username, email, phone);

                        currentUser = mAuth.getCurrentUser();
                        //menyimpan data user ke database user di firebase real time database
                        mDatabase.child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //mengirimkan email untuk verifikasi
                                    currentUser.sendEmailVerification();
                                    //pindah ke halaman utama aplikasi
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
                            binding.passwordLayout.setError("Password Anda terlalu lemah. Gunakan campuran alfabet, angka, dan simbol");
                            binding.passwordLayout.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e){
                            binding.passwordLayout.setError("Email Anda invalid atau sudah digunakan untuk registrasi");
                            binding.passwordLayout.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e){
                            binding.passwordLayout.setError("Anda sudah registrasi dengan email ini. Gunakan email yang lain");
                            binding.passwordLayout.requestFocus();
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