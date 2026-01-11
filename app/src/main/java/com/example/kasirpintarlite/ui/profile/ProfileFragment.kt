package com.example.kasirpintarlite.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.example.kasirpintarlite.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isEditMode = false
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    // 1. Picker Foto Galeri
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processAndUploadImage(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnEditSave.setOnClickListener {
            if (isEditMode) saveChanges() else enterEditMode()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            activity?.finish()
        }

        binding.cardProfileImage.setOnClickListener {
            if (isEditMode) {
                imagePickerLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Klik 'Edit Profil' untuk mengubah foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processAndUploadImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val inputStream = activity?.contentResolver?.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, true)

            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            db.child(userId).child("profileImageUrl").setValue(base64Image).addOnSuccessListener {
                binding.imgProfile.load(resizedBitmap) {
                    transformations(CircleCropTransformation())
                }
                Toast.makeText(context, "Foto profil diperbarui!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        db.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return

                val name = snapshot.child("name").value?.toString() ?: ""
                val email = snapshot.child("email").value?.toString() ?: auth.currentUser?.email
                val phone = snapshot.child("phone").value?.toString() ?: ""
                val dataImage = snapshot.child("profileImageUrl").value?.toString() ?: ""

                binding.etName.setText(name)
                binding.etEmail.setText(email)
                binding.etPhone.setText(phone)

                if (dataImage.isNotEmpty() && dataImage != "null") {
                    try {
                        if (dataImage.startsWith("http")) {
                            binding.imgProfile.load(dataImage) {
                                transformations(CircleCropTransformation())
                            }
                        } else {
                            val imageBytes = Base64.decode(dataImage, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            binding.imgProfile.load(decodedImage) {
                                transformations(CircleCropTransformation())
                            }
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveChanges() {
        val userId = auth.currentUser?.uid ?: return
        val newName = binding.etName.text.toString().trim()
        val newPhone = binding.etPhone.text.toString().trim()

        if (newName.isEmpty()) {
            binding.etName.error = "Nama wajib diisi"
            return
        }

        val updates = mapOf("name" to newName, "phone" to newPhone)
        db.child(userId).updateChildren(updates).addOnSuccessListener {
            Toast.makeText(context, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
            exitEditMode()
        }
    }

    private fun enterEditMode() {
        isEditMode = true
        binding.btnEditSave.text = "Simpan"
        binding.etName.isEnabled = true
        binding.etPhone.isEnabled = true
        binding.txtTapToChange.visibility = View.VISIBLE
        binding.cardProfileImage.isClickable = true
        binding.etName.requestFocus()
    }

    private fun exitEditMode() {
        isEditMode = false
        binding.btnEditSave.text = "Edit Profil"
        binding.etName.isEnabled = false
        binding.etPhone.isEnabled = false
        binding.txtTapToChange.visibility = View.GONE
        binding.cardProfileImage.isClickable = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
