package com.example.wisataciayumajakuning;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.databinding.FragmentProfileBinding;
import com.example.wisataciayumajakuning.model.User;
import com.example.wisataciayumajakuning.user.EditProfileActivity;
import com.example.wisataciayumajakuning.user.LoginUserActivity;
import com.example.wisataciayumajakuning.user.UbahEmailActivity;
import com.example.wisataciayumajakuning.user.UbahPasswordActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private Uri uriImage;
    private String username, phone, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //menginialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //mendapatkan currentuser
        currentUser = mAuth.getCurrentUser();
        //mendapatkan referensi folder profilepics di firebase storage
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");

        //mengecek apakah user sudah melakukan login atau belum
        if (currentUser == null){
            Toast.makeText(getActivity(), "Terjadi kesalahan, tidak dapat memuat data User", Toast.LENGTH_LONG).show();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            //menampilkan data user
            showProfileData();
        }
        //mendapatkan url foto profile user
        Uri uri = currentUser.getPhotoUrl();
        //memuat foto profile user
        Glide.with(this).load(uri).centerCrop().into(binding.imgProfile);

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //memilih foto profile
                ImagePicker.with(ProfileFragment.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mengarahkan user ke halaman edit profile
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mengarahkan user ke halaman ubah email
                Intent intent = new Intent(getContext(), UbahEmailActivity.class);
                startActivity(intent);
            }
        });

        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mengarahkan user ke halaman ubah password
                Intent intent = new Intent(getContext(), UbahPasswordActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    private void showProfileData() {
        //mendapatkan id user
        String uId = currentUser.getUid();
        //mngambil referensi database user dari firebase real time database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //mengambil data user berdasarkan id user
        reference.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mendapatkan objek user dan menggunakan nilainya untuk update UI
                User user = snapshot.getValue(User.class);
                if (user != null){
                    username = user.getUsername();
                    email = currentUser.getEmail();
                    phone = user.getPhone();
                    //memuat data user di halaman profile
                    binding.username.setText(username);
                    binding.email.setText(email);
                    binding.phoneNumber.setText(phone);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Terjadi kesalahan, tidak dapat memuat data user", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //membuat url foto profil user
        uriImage = data.getData();
        binding.imgProfile.setImageURI(uriImage);
        //
        uploadPict();
    }

    private void uploadPict() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (uriImage != null){
            //membuat referensi sebagai penunjuk ke file
            StorageReference fileReferences = storageReference.child(mAuth.getCurrentUser().getUid() + "."
            + getFileExtension(uriImage));
            //meng-upload file dengan method putFile( )
            fileReferences.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //mendapatkan url untuk mendownload file
                    fileReferences.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //url yang telah di download
                            Uri downloadUri = uri;
                            currentUser = mAuth.getCurrentUser();
                            //memperbarui informasi user dengan menambahkan foto profil
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            //mengupdate profil yang telah diperbaharui
                            currentUser.updateProfile(profileUpdates);
                        }
                    });
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Anda berhasil mengupdate foto profil Anda", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    //mengambil ekstensi file dari file android
    private String getFileExtension(Uri uri) {
       ContentResolver cR = getActivity().getContentResolver();
       MimeTypeMap mime = MimeTypeMap.getSingleton();
       return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}