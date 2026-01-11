package com.example.kasirpintarlite.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.databinding.FragmentProductBinding
import com.example.kasirpintarlite.model.Product
import com.google.firebase.database.*

class ProductFragment : Fragment() {

    // View Binding
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()

    // Listener Firebase sebagai variabel agar bisa dihapus saat fragment hancur
    private var productListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("products")

        // 2. Setup Adapter
        // Gunakan context yang aman dengan let atau check
        adapter = ProductAdapter(requireContext(), productList)
        binding.listView.adapter = adapter

        // 3. Tombol Tambah Produk (Pastikan ID di XML adalah btnAddProduct)
        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(requireContext(), AddEditProductActivity::class.java)
            startActivity(intent)
        }

        // 4. Load Data
        loadData()
    }

    private fun loadData() {
        productListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Cek apakah fragment masih menempel pada Activity
                if (!isAdded || _binding == null) return

                productList.clear()
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Log error jika diperlukan
            }
        }

        dbRef.addValueEventListener(productListener!!)
    }

    override fun onDestroyView() {
        // Penting: Hapus listener Firebase agar tidak terjadi kebocoran memori (memory leak)
        productListener?.let { dbRef.removeEventListener(it) }

        super.onDestroyView()
        _binding = null
    }
}
