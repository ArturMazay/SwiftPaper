package com.swiftpaper.app.feature.pdfjob

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.swiftpaper.app.R
import com.swiftpaper.app.SwiftPaperApp
import com.swiftpaper.app.core.FileExporter
import com.swiftpaper.app.core.HistoryItem
import com.swiftpaper.app.core.PdfEngine
import com.swiftpaper.app.navigation.JobType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

data class JobUiState(
    val uris: List<Uri> = emptyList(),
    val isWorking: Boolean = false,
    val error: String? = null,
    val compressQuality: Int = 70,
    val useHd: Boolean = false,
    val skipWatermark: Boolean = false
)

class PdfJobViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as SwiftPaperApp

    private val _state = MutableStateFlow(JobUiState())
    val state: StateFlow<JobUiState> = _state.asStateFlow()

    fun setUris(uris: List<Uri>) {
        _state.value = _state.value.copy(uris = uris, error = null)
    }

    fun addUris(uris: List<Uri>) {
        _state.value = _state.value.copy(uris = _state.value.uris + uris, error = null)
    }

    fun removeAt(index: Int) {
        val next = _state.value.uris.toMutableList().also { it.removeAt(index) }
        _state.value = _state.value.copy(uris = next)
    }

    fun move(from: Int, to: Int) {
        val list = _state.value.uris.toMutableList()
        if (from !in list.indices || to !in list.indices) return
        val item = list.removeAt(from)
        list.add(to, item)
        _state.value = _state.value.copy(uris = list)
    }

    fun setCompressQuality(quality: Int) {
        _state.value = _state.value.copy(compressQuality = quality)
    }

    fun unlockHdNoWatermark() {
        _state.value = _state.value.copy(useHd = true, skipWatermark = true)
    }

    fun clear() {
        _state.value = JobUiState()
    }

    fun export(
        jobType: JobType,
        onDone: (path: String, mime: String) -> Unit
    ) {
        viewModelScope.launch {
            val current = _state.value
            if (current.uris.isEmpty()) {
                _state.value = current.copy(
                    error = getApplication<Application>().getString(R.string.add_at_least_one_file)
                )
                return@launch
            }
            _state.value = current.copy(isWorking = true, error = null)
            val isPro = app.userPrefs.isPro.first()
            val sessionHd = app.userPrefs.sessionHdUnlock.first()
            val hd = isPro || current.useHd || sessionHd
            val watermark = !(isPro || current.skipWatermark || sessionHd)
            runCatching {
                when (jobType) {
                    JobType.IMAGE_TO_PDF, JobType.SCAN -> {
                        val out = FileExporter.createOutputFile(getApplication(), "swiftpaper", "pdf")
                        PdfEngine.imagesToPdf(getApplication(), current.uris, out, hd, watermark)
                        HistoryItem(
                            id = UUID.randomUUID().toString(),
                            name = out.name,
                            path = out.absolutePath,
                            mimeType = "application/pdf",
                            createdAt = System.currentTimeMillis()
                        ).also { app.historyRepository.add(it) }
                        onDone(out.absolutePath, "application/pdf")
                    }
                    JobType.COMPRESS -> {
                        val out = FileExporter.createOutputFile(getApplication(), "compressed", "jpg")
                        PdfEngine.compressImage(
                            getApplication(),
                            current.uris.first(),
                            out,
                            current.compressQuality
                        )
                        HistoryItem(
                            id = UUID.randomUUID().toString(),
                            name = out.name,
                            path = out.absolutePath,
                            mimeType = "image/jpeg",
                            createdAt = System.currentTimeMillis()
                        ).also { app.historyRepository.add(it) }
                        onDone(out.absolutePath, "image/jpeg")
                    }
                    JobType.MERGE -> {
                        val out = FileExporter.createOutputFile(getApplication(), "merged", "pdf")
                        PdfEngine.mergePdfs(getApplication(), current.uris, out, hd, watermark)
                        HistoryItem(
                            id = UUID.randomUUID().toString(),
                            name = out.name,
                            path = out.absolutePath,
                            mimeType = "application/pdf",
                            createdAt = System.currentTimeMillis()
                        ).also { app.historyRepository.add(it) }
                        onDone(out.absolutePath, "application/pdf")
                    }
                }
            }.onFailure {
                _state.value = _state.value.copy(
                    isWorking = false,
                    error = it.message
                        ?: getApplication<Application>().getString(R.string.export_failed)
                )
            }.onSuccess {
                if (sessionHd) app.userPrefs.setSessionHdUnlock(false)
                _state.value = _state.value.copy(isWorking = false, useHd = false, skipWatermark = false)
            }
        }
    }
}
