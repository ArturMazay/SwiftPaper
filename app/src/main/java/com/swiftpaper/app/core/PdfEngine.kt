package com.swiftpaper.app.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.swiftpaper.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

object PdfEngine {

    suspend fun imagesToPdf(
        context: Context,
        imageUris: List<Uri>,
        outputFile: File,
        hd: Boolean,
        watermark: Boolean
    ): File = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        try {
            imageUris.forEachIndexed { index, uri ->
                val bitmap = decodeBitmap(context, uri, hd) ?: return@forEachIndexed
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                val page = document.startPage(pageInfo)
                val canvas = page.canvas
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                if (watermark) {
                    drawWatermark(
                        canvas,
                        bitmap.width,
                        bitmap.height,
                        context.getString(R.string.app_name)
                    )
                }
                document.finishPage(page)
                bitmap.recycle()
            }
            FileOutputStream(outputFile).use { document.writeTo(it) }
        } finally {
            document.close()
        }
        outputFile
    }

    suspend fun mergePdfs(
        context: Context,
        pdfUris: List<Uri>,
        outputFile: File,
        hd: Boolean,
        watermark: Boolean
    ): File = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        var pageIndex = 0
        try {
            pdfUris.forEach { uri ->
                context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                    PdfRenderer(pfd).use { renderer ->
                        for (i in 0 until renderer.pageCount) {
                            renderer.openPage(i).use { page ->
                                val scale = if (hd) 2 else 1
                                val width = page.width * scale
                                val height = page.height * scale
                                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                bitmap.eraseColor(Color.WHITE)
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                pageIndex += 1
                                val pageInfo = PdfDocument.PageInfo.Builder(width, height, pageIndex).create()
                                val outPage = document.startPage(pageInfo)
                                outPage.canvas.drawBitmap(bitmap, 0f, 0f, null)
                                if (watermark) {
                                    drawWatermark(
                                        outPage.canvas,
                                        width,
                                        height,
                                        context.getString(R.string.app_name)
                                    )
                                }
                                document.finishPage(outPage)
                                bitmap.recycle()
                            }
                        }
                    }
                }
            }
            FileOutputStream(outputFile).use { document.writeTo(it) }
        } finally {
            document.close()
        }
        outputFile
    }

    suspend fun compressImage(
        context: Context,
        imageUri: Uri,
        outputFile: File,
        quality: Int
    ): File = withContext(Dispatchers.IO) {
        val bitmap = decodeBitmap(context, imageUri, hd = true)
            ?: error(context.getString(R.string.error_unable_to_decode_image))
        FileOutputStream(outputFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(10, 100), out)
        }
        bitmap.recycle()
        outputFile
    }

    private fun decodeBitmap(context: Context, uri: Uri, hd: Boolean): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        val maxSide = if (hd) 2400 else 1280
        val sample = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxSide)
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxSide: Int): Int {
        var inSampleSize = 1
        val largest = maxOf(width, height)
        if (largest > maxSide) {
            inSampleSize = largest / maxSide
            if (inSampleSize < 1) inSampleSize = 1
        }
        return inSampleSize
    }

    private fun drawWatermark(canvas: Canvas, width: Int, height: Int, text: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(90, 15, 23, 42)
            textSize = min(width, height) * 0.06f
            textAlign = Paint.Align.CENTER
        }
        canvas.save()
        canvas.rotate(-28f, width / 2f, height / 2f)
        canvas.drawText(text, width / 2f, height / 2f, paint)
        canvas.restore()
    }
}
