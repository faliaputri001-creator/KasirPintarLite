package com.example.kasirpintarlite.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object PdfHelper {

    fun exportTransactionPdf(
        context: Context,
        data: List<Pair<String, Long>>
    ): File {

        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var y = 40
        canvas.drawText("LAPORAN TRANSAKSI", 80f, y.toFloat(), paint)

        y += 30
        for (item in data) {
            canvas.drawText("${item.first} : Rp ${item.second}", 20f, y.toFloat(), paint)
            y += 20
        }

        pdfDocument.finishPage(page)

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "laporan_transaksi.pdf"
        )

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }
}
