package com.example.kasirpintarlite.ui.transaction.history.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.TransactionItem

class TransactionDetailAdapter(
    private val context: Context,
    private val items: List<TransactionItem>
) : BaseAdapter() {

    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]
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
                .inflate(R.layout.item_transaction_detail, parent, false)

            holder = ViewHolder(
                name = view.findViewById(R.id.txtName),
                qty = view.findViewById(R.id.txtQty),
                subtotal = view.findViewById(R.id.txtSubtotal)
            )

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = items[position]

        holder.name.text = item.name ?: "-"
        holder.qty.text = "Qty: ${item.qty ?: 0}"
        holder.subtotal.text = "Rp ${item.subtotal ?: 0}"

        return view
    }

    data class ViewHolder(
        val name: TextView,
        val qty: TextView,
        val subtotal: TextView
    )
}
