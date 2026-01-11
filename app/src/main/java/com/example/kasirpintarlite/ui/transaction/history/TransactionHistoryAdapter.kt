package com.example.kasirpintarlite.ui.transaction.history

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.TransactionHistory

class TransactionHistoryAdapter(
    private val context: Context,
    private val list: List<TransactionHistory>
) : BaseAdapter() {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_transaction_history, parent, false)

        val txtDate = view.findViewById<TextView>(R.id.txtDate)
        val txtTotal = view.findViewById<TextView>(R.id.txtTotal)

        val item = list[position]

        val dateText = DateFormat.format(
            "dd MMM yyyy - HH:mm",
            item.date
        )

        txtDate.text = dateText
        txtTotal.text = "Total: Rp ${item.total}"

        return view
    }
}
