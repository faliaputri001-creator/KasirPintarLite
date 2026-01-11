package com.example.kasirpintarlite.ui.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.Product

class ProductAdapter(
    private val context: Context,
    private val list: List<Product>
) : BaseAdapter() {

    override fun getCount() = list.size
    override fun getItem(position: Int) = list[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.item_product, parent, false)

            holder = ViewHolder(
                name = view.findViewById(R.id.txtName),
                price = view.findViewById(R.id.txtPrice),
                stock = view.findViewById(R.id.txtStock)
            )

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val product = list[position]

        holder.name.text = product.name ?: "-"
        holder.price.text = "Rp ${product.price}"
        holder.stock.text = "Stok: ${product.stock}"

        return view
    }

    // ✅ ViewHolder HARUS ADA
    private data class ViewHolder(
        val name: TextView,
        val price: TextView,
        val stock: TextView
    )
}
