package com.example.kasirpintarlite.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.databinding.FragmentStockBinding
import com.example.kasirpintarlite.model.Product // <--- PENTING: Import ini
import com.google.firebase.database.*

class StockFragment : Fragment() {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val productList = ArrayList<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)

        val adapter = StockAdapter(requireContext(), productList)
        binding.listStock.adapter = adapter

        val dbRef = FirebaseDatabase.getInstance().getReference("products")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (data in snapshot.children) {
                    val p = data.getValue(Product::class.java)
                    p?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
