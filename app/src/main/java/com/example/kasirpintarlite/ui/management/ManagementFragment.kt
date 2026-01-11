package com.example.kasirpintarlite.ui.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.databinding.FragmentManagementBinding
import com.example.kasirpintarlite.ui.product.ProductFragment
import com.example.kasirpintarlite.ui.stock.StockFragment

/**
 * Fragment untuk mengelola pengaturan toko (Produk, Kategori, Stok)
 */
class ManagementFragment : Fragment() {

    private var _binding: FragmentManagementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Menu: Barang atau Jasa
        binding.menuProduct.setOnClickListener {
            navigateToFragment(ProductFragment())
        }

        // 2. Menu: Kategori Barang
        binding.menuCategory.setOnClickListener {
            // Jika Anda sudah membuat CategoryFragment, ganti baris ini:
            // navigateToFragment(CategoryFragment())

            // Sementara: Navigasi ke ProductFragment karena kategori biasanya nempel di produk
            navigateToFragment(ProductFragment())
        }

        // 3. Menu: Manajemen Stok
        binding.menuStock.setOnClickListener {
            navigateToFragment(StockFragment())
        }
    }

    /**
     * Fungsi Helper untuk pindah fragment dengan animasi halus
     */
    private fun navigateToFragment(fragment: Fragment) {
        if (!isAdded) return // Safety check

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // User bisa kembali ke menu Manajemen dengan tombol Back
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
