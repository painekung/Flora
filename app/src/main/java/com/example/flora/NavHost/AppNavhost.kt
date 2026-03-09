package com.example.flora.NavHost

import android.util.Log
import com.example.flora.ScreenMain.MainScreen.MainScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.flora.Authentication.AuthViewModel
import com.example.flora.Authentication.LoginScreen

import com.example.flora.ScreenMain.ProfileScreen
import com.example.flora.Authentication.RegisterScreen

import com.example.flora.Firebase.FlowersViewModel
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.ScreenMain.AddFlowerScreen
import com.example.flora.ScreenMain.CartScreen
import com.example.flora.ScreenMain.FlowerDetailScreen
import com.example.flora.ScreenMain.OrderHistoryScreen
import com.example.flora.ScreenMain.ShopScreen



@Composable
fun AppNavhost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val flowersViewModel: FlowersViewModel = viewModel()
    val storeViewModel: StoreViewModel = viewModel()
    val authVM = viewModel<AuthViewModel>()

    LaunchedEffect(Unit) {
        authVM.logout()
    }

    val startDestination = if (authVM.isLoggedIn) "MainScreen" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("flower_detail") {
            FlowerDetailScreen(modifier = Modifier, navController = navController, flowerViewModel = flowersViewModel)
        }
        composable("login") {
            LoginScreen(modifier = Modifier, navController = navController, authVM = authVM)
        }
        composable("register") {
            RegisterScreen(modifier = Modifier, navController = navController, authVM = authVM)
        }
        composable("MainScreen") {
            MainScreen(modifier = Modifier, navController = navController, flowerViewModel = flowersViewModel, storeViewModel = storeViewModel)
        }
        composable(
            route = "AddFlowerScreen/{ownerEmail}",
            arguments = listOf(navArgument("ownerEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val ownerEmail = backStackEntry.arguments?.getString("ownerEmail") ?: ""
            AddFlowerScreen(ownerEmail = ownerEmail, onNavigateBack = { navController.popBackStack() })
        }
        composable("ShopScreen") {
            ShopScreen(modifier = Modifier, navController = navController, flowerViewModel = flowersViewModel, storeViewModel = storeViewModel)
        }
        composable("WhisListScreen") {
            OrderHistoryScreen(modifier = Modifier, navController = navController)
        }
        composable("ProfileScreen") {
            ProfileScreen(modifier = Modifier, navController = navController, authVM = authVM)
        }
        composable("cart") {
            CartScreen(modifier = Modifier, navController = navController)
        }
    }
}