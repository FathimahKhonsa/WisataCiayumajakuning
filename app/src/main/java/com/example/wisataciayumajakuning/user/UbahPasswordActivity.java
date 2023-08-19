package com.example.wisataciayumajakuning.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.wisataciayumajakuning.ProfileFragment;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityUbahPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UbahPasswordActivity extends AppCompatActivity {
    private ActivityUbahPasswordBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String oldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUbahPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding.inputNewPassword.setEnabled(false);
        binding.inputKonfirmasiPassword.setEnabled(false);
        binding.btnChangePass.setEnabled(false);

        if (currentUser.equals("")){
            Toast.makeText(this, "Terjadi kesalahan ketika masuk", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginUserActivity.class);
            startActivity(intent);
        } else {
            reAuthenticate(currentUser);
        }
    }

    private void reAuthenticate(FirebaseUser currentUser) {
        binding.btnAutentikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = binding.inputPassword.getText().toString();
                if (TextUtils.isEmpty(oldPassword)){
                    binding.layoutPassWord.setError("Isikan Password Anda");
                    binding.layoutPassWord.requestFocus();
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                binding.progressBar.setVisibility(View.GONE);
                                binding.inputPassword.setEnabled(false);
                                binding.btnAutentikasi.setEnabled(false);
                                binding.inputNewPassword.setEnabled(true);
                                binding.inputKonfirmasiPassword.setEnabled(true);
                                binding.btnChangePass.setEnabled(true);

                                binding.keterangan.setText("Sekarang Anda dapat mengubah password Anda");
                                binding.btnChangePass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(currentUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(UbahPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser currentUser) {
        String newPassword = binding.inputNewPassword.getText().toString();
        String confirmaPassword = binding.inputKonfirmasiPassword.getText().toString();

        if (TextUtils.isEmpty(newPassword)){
            binding.layoutNewPass.setError("Isikan Password baru Anda");
            binding.layoutNewPass.requestFocus();
        } else if (TextUtils.isEmpty(confirmaPassword)) {
            binding.layoutKonfirmasi.setError("Konfirmasi password Anda");
            binding.layoutKonfirmasi.requestFocus();
        } else if (newPassword.matches(confirmaPassword)) {
            binding.layoutKonfirmasi.setError("Password Anda berbeda");
            binding.layoutKonfirmasi.requestFocus();
        } else if (!oldPassword.matches(newPassword)) {
            binding.layoutNewPass.setError("Masukkan password baru Anda");
            binding.layoutNewPass.requestFocus();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);

            currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UbahPasswordActivity.this, "Password Anda berhasil diubah", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new ProfileFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(UbahPasswordActivity.this, e.getMessage() ,Toast.LENGTH_SHORT).show();
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}