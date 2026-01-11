package com.example.kasirpintarlite.ui.chart

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.*

class SalesChartFragment : Fragment() {

    private lateinit var chart: LineChart
    private val dailySales = linkedMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (context == null) {
            return inflater.inflate(R.layout.fragment_sales_chart, container, false)
        }
        val view = inflater.inflate(
            R.layout.fragment_sales_chart,
            container,
            false
        )

        chart = view.findViewById(R.id.lineChart)
        loadChartData()

        return view
    }

    private fun loadChartData() {
        val ref = FirebaseDatabase.getInstance().getReference("transactions")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dailySales.clear()

                for (data in snapshot.children) {
                    val date = data.child("date").getValue(Long::class.java) ?: continue
                    val total = data.child("total").getValue(Int::class.java) ?: 0

                    val day = DateFormat.format("dd MMM", date).toString()
                    dailySales[day] = (dailySales[day] ?: 0) + total
                }

                showChart()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showChart() {
        val entries = mutableListOf<Entry>()
        val labels = dailySales.keys.toList()

        labels.forEachIndexed { index, label ->
            entries.add(
                Entry(index.toFloat(), dailySales[label]!!.toFloat())
            )
        }

        val dataSet = LineDataSet(entries, "Total Penjualan")
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.valueTextSize = 12f

        chart.data = LineData(dataSet)

        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.animateX(1000)
        chart.invalidate()
    }
}
