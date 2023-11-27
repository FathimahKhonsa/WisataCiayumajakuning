package com.example.wisataciayumajakuning.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.wisataciayumajakuning.HomeFragment;
import com.example.wisataciayumajakuning.MainActivity;
import com.example.wisataciayumajakuning.ProfileFragment;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityUbahEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UbahEmailActivity extends AppCompatActivity {
    private ActivityUbahEmailBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userOldEmail, userNewEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUbahEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //mengisinisialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //mendapatkan currentUser
        currentUser = mAuth.getCurrentUser();

        binding.btnUpdateEmail.setEnabled(false);
        binding.inputEmailBaru.setEnabled(false);

        //mendapatkan email user sebelum diubah
        userOldEmail = currentUser.getEmail();
        binding.inputEmail.setText(userOldEmail);

        //mengecek akun user sudah masuk ke dalam aplikasi atau belum
        if (currentUser.equals("")){
            Toast.makeText(this, "Terjadi kesalahan ketika login", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginUserActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticate(currentUser);
        }
    }

    private void reAuthenticate(FirebaseUser currentUser) {
        binding.btnAutentikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword = binding.inputPassword.getText().toString();

                if (TextUtils.isEmpty(userPassword)){
                    binding.passwordLayout.setError("Isikan Password Anda");
                    binding.passwordLayout.requestFocus();
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    //mendapatkan auth credential dari user yang digunakan untuk autentikasi ulang
                    AuthCredential authCredential = EmailAuthProvider.getCredential(userOldEmail, userPassword);
                    //meminta user untuk memberikan kembali credential masuknya
                    currentUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                binding.progressBar.setVisibility(View.GONE);

                                binding.keterangan.setText("Sekarang Anda dapat mengubah email Anda");
                                binding.inputEmailBaru.setEnabled(true);
                                binding.inputPassword.setEnabled(false);
                                binding.btnAutentikasi.setEnabled(false);
                                binding.btnUpdateEmail.setEnabled(true);

                                binding.btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //mengecek email baru user yang telah diinput di edit text
                                        userNewEmail = binding.inputEmailBaru.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)){
                                            binding.newEmailLayout.setError("Isikan Email baru Anda");
                                            binding.newEmailLayout.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            binding.newEmailLayout.setError("Isikan Email yang valid");
                                            binding.newEmailLayout.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            binding.newEmailLayout.setError("Email Anda tidak boleh sama dengan email yang lama");
                                            binding.newEmailLayout.requestFocus();
                                        } else {
                                            binding.progressBar.setVisibility(View.GONE);
                                            //memanggil method updateEmail( ) untuk meng- update email user
                                            updateEmail(currentUser);
                                            //mengambil referensi database user
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                            String uId = currentUser.getUid();
                                            //menyimpan email baru user di database
                                            reference.child(uId).child("email").setValue(userNewEmail);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(UbahEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser currentUser) {
        //meng-update email user
        currentUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(UbahEmailActivity.this, "Anda berhasil mengupdate email Anda", Toast.LENGTH_SHORT).show();
                    reload();
                }else {
                    Toast.makeText(UbahEmailActivity.this, "Anda gagal mengupdate email Anda", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reload(){
        Intent intent = new Intent(UbahEmailActivity.this, MainActivity.class);
        startActivity(intent);
//        Fragment fragment = new ProfileFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
    }
}