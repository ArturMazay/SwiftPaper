package com.swiftpaper.app.navigation

object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val PAYWALL = "paywall"
    const val IMAGE_TO_PDF = "image_to_pdf"
    const val SCAN = "scan"
    const val COMPRESS = "compress"
    const val MERGE = "merge"
    const val PREVIEW = "preview/{job}"
    const val EXPORT = "export/{path}/{mime}"

    fun preview(job: String) = "preview/$job"
    fun export(path: String, mime: String): String {
        val encodedPath = android.net.Uri.encode(path)
        val encodedMime = android.net.Uri.encode(mime)
        return "export/$encodedPath/$encodedMime"
    }
}

enum class JobType {
    IMAGE_TO_PDF,
    SCAN,
    COMPRESS,
    MERGE
}
