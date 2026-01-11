package com.example.kasirpintarlite.utils

import android.content.Context
import android.os.Environment
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter

object CsvHelper {
    // PERBAIKAN: Menerima List<Array<String>> agar fleksibel
    fun exportTransactionCsv(context: Context, data: List<Array<String>>): File {
        // Tentukan folder Download
        val exportDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "KasirPintar")
        if (!exportDir.exists()) exportDir.mkdirs()

        val file = File(exportDir, "Laporan_Transaksi_${System.currentTimeMillis()}.csv")

        try {
            val writer = CSVWriter(FileWriter(file))
            writer.writeAll(data) // Menulis semua data sekaligus (termasuk header)
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return file
    }
}
