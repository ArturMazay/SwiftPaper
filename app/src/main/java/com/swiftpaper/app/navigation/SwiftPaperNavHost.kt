package com.swiftpaper.app.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swiftpaper.app.R
import com.swiftpaper.app.SwiftPaperApp
import com.swiftpaper.app.feature.compress.CompressScreen
import com.swiftpaper.app.feature.export.ExportScreen
import com.swiftpaper.app.feature.history.HistoryScreen
import com.swiftpaper.app.feature.home.HomeScreen
import com.swiftpaper.app.feature.merge.MergeScreen
import com.swiftpaper.app.feature.paywall.PaywallScreen
import com.swiftpaper.app.feature.pdfjob.PdfJobViewModel
import com.swiftpaper.app.feature.pdfjob.PreviewScreen
import com.swiftpaper.app.feature.scan.ScanScreen

@Composable
fun SwiftPaperNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as SwiftPaperApp
    val jobViewModel: PdfJobViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                isProFlow = app.userPrefs.isPro,
                onScan = { navController.navigate(Routes.SCAN) },
                onImageToPdf = { navController.navigate(Routes.IMAGE_TO_PDF) },
                onCompress = { navController.navigate(Routes.COMPRESS) },
                onMerge = { navController.navigate(Routes.MERGE) },
                onHistory = { navController.navigate(Routes.HISTORY) },
                onPaywall = { navController.navigate(Routes.PAYWALL) }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                repository = app.historyRepository,
                onBack = { navController.popBackStack() },
                onOpen = { item ->
                    navController.navigate(Routes.export(item.path, item.mimeType))
                }
            )
        }
        composable(Routes.PAYWALL) {
            PaywallScreen(
                billingManager = app.billingManager,
                isProFlow = app.userPrefs.isPro,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.IMAGE_TO_PDF) {
            PreviewScreen(
                title = stringResource(R.string.action_image_to_pdf),
                jobType = JobType.IMAGE_TO_PDF,
                viewModel = jobViewModel,
                onBack = { navController.popBackStack() },
                onExported = { path, mime ->
                    navController.navigate(Routes.export(path, mime)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(Routes.SCAN) {
            ScanScreen(
                viewModel = jobViewModel,
                onBack = { navController.popBackStack() },
                onContinue = {
                    navController.navigate(Routes.preview(JobType.SCAN.name))
                }
            )
        }
        composable(
            route = Routes.PREVIEW,
            arguments = listOf(navArgument("job") { type = NavType.StringType })
        ) { entry ->
            val job = remember(entry) {
                JobType.valueOf(entry.arguments?.getString("job") ?: JobType.SCAN.name)
            }
            PreviewScreen(
                title = if (job == JobType.SCAN) {
                    stringResource(R.string.scan_preview)
                } else {
                    stringResource(R.string.action_image_to_pdf)
                },
                jobType = job,
                viewModel = jobViewModel,
                onBack = { navController.popBackStack() },
                onExported = { path, mime ->
                    navController.navigate(Routes.export(path, mime)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(Routes.COMPRESS) {
            CompressScreen(
                viewModel = jobViewModel,
                onBack = { navController.popBackStack() },
                onExported = { path, mime ->
                    navController.navigate(Routes.export(path, mime)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(Routes.MERGE) {
            MergeScreen(
                viewModel = jobViewModel,
                onBack = { navController.popBackStack() },
                onExported = { path, mime ->
                    navController.navigate(Routes.export(path, mime)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(
            route = Routes.EXPORT,
            arguments = listOf(
                navArgument("path") { type = NavType.StringType },
                navArgument("mime") { type = NavType.StringType }
            )
        ) { entry ->
            val path = Uri.decode(entry.arguments?.getString("path").orEmpty())
            val mime = Uri.decode(entry.arguments?.getString("mime").orEmpty())
            ExportScreen(
                filePath = path,
                mimeType = mime,
                adsManager = app.adsManager,
                historyRepository = app.historyRepository,
                userPrefs = app.userPrefs,
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onPaywall = { navController.navigate(Routes.PAYWALL) }
            )
        }
    }
}
