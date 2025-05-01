package com.example.cookiesales

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cookiesales.data.DataSource
import com.example.cookiesales.ui.OrderSummaryScreen
import com.example.cookiesales.ui.OrderViewModel
import com.example.cookiesales.ui.SelectOptionPreview
import com.example.cookiesales.ui.SelectOptionScreen
import com.example.cookiesales.ui.StartOrderScreen

enum class CookieScreen(){
    Start,
    Flavor,
    Pickup,
    Summary,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookieAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CookieApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    Scaffold(
        topBar = {
            CookieAppBar(
                canNavigateBack = false,
                navigateUp = {  }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = CookieScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = CookieScreen.Start.name){
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CookieScreen.Flavor.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            composable(route = CookieScreen.Flavor.name){
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {navController.navigate(CookieScreen
                        .Pickup.name)},
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel,navController)},
                    options = DataSource.flavors.map{
                        id -> context.resources.getString(id)
                    },
                    onSelectionChanged = {viewModel.setFlavor(it)},
                    modifier = Modifier.fillMaxHeight(),
                )
            }

            composable(route = CookieScreen.Pickup.name){
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {navController.navigate(CookieScreen
                        .Summary.name)},
                    onCancelButtonClicked = {cancelOrderAndNavigateToStart(viewModel,navController)},
                    options = uiState.pickupOptions,
                    onSelectionChanged = {viewModel.setDate(it)},
                    modifier = Modifier.fillMaxHeight(),
                )
            }

            composable(route = CookieScreen.Summary.name){
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {cancelOrderAndNavigateToStart(viewModel,navController)},
                    onSendButtonClicked = {subject:String, summary:String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }

    }
}

private fun shareOrder(
    context: Context,
    subject:String,
    summary: String,
    ){
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_cookie_order)

        )
    )

}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
){
    viewModel.resetOrder()
    navController.popBackStack(
        CookieScreen.Start.name, inclusive = false
    )
}