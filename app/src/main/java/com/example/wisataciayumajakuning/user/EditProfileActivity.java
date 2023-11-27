package com.example.wisataciayumajakuning.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.wisataciayumajakuning.ProfileFragment;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityEditProfileBinding;
import com.example.wisataciayumajakuning.model.EditDataUser;
import com.example.wisataciayumajakuning.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private String username, phone;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //memunculkan data user
        showUserData();
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(currentUser);
            }
        });
    }

    private void updateProfile(FirebaseUser currentUser) {
        //mengecek data user apakah sudah terisi
        boolean isEmpty = isEmptyData();
        if (isEmpty){
            //memanggil class User
            User user = new User(username, currentUser.getEmail(), phone);
            //mendapatkan referensi database user
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

            //mendapatkan id user
            String uId = currentUser.getUid();
            binding.progressBar.setVisibility(View.VISIBLE);
            //mengubah username user
            reference.child(uId).child("username").setValue(username);
            //mengubah nomor hp user
            reference.child(uId).child("phone").setValue(phone);

                    binding.progressBar.setVisibility(View.GONE);

                    //pindah ke halaman profile
                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
        }
    }

    private boolean isEmptyData(){
        //mengambil teks username dan nomor hp dari editText
        username = binding.inputUsername.getText().toString().trim();
        phone = binding.inputPhoneNumber.getText().toString().trim();

        //pengecekan apakah bagian editText sudah terisi dengan data user
        if ("".equals(username) && "".equals(phone)){
            Toast.makeText(this, "Anda belum mengisi data Anda", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(username)) {
            binding.usernameLayout.setError("Isikan Username Anda");
            binding.usernameLayout.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(phone)) {
            binding.phoneLayout.setError("Isikan Nomor Handphone Anda");
            binding.phoneLayout.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void showUserData(){
        //mendapatkan id user
        String uId = currentUser.getUid();
        //mendapatkan referensi database user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //mengambil data dari database
        reference.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null){
                    username = user.getUsername();
                    phone = user.getPhone();

                    //menampilkan data username dan nomor hp
                    binding.inputUsername.setText(username);
                    binding.inputPhoneNumber.setText(phone);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Terjadi kesalahan, tidak dapat memuat data user", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility((View.GONE));
            }
        });
    }
}