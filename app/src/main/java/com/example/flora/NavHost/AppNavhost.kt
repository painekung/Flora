package com.example.flora.NavHost

import com.example.flora.ScreenMain.MainScreen.MainScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.flora.Authentication.LoginScreen
import com.example.flora.ScreenMain.ProfileScreen
import com.example.flora.Authentication.RegisterScreen
import com.example.flora.Authentication.RegisterScreenTwo
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.ScreenMain.CartScreen
import com.example.flora.ScreenMain.FlowerDetailScreen
import com.example.flora.ScreenMain.ShopScreen
import com.example.flora.ScreenMain.WhisListScreen
import com.example.flora.viewmodels.FlowerViewModel



@Composable
fun AppNavhost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // 1. สร้าง ViewModel ไว้ที่นี่เพื่อใช้ร่วมกันใน NavHost
    val flowerViewModel: FlowerViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val storeViewModel: StoreViewModel = viewModel() // ✅ ใช้แบบนี้พอ

    NavHost(
        navController = navController,
        startDestination = "ShopScreen",
        modifier = modifier
    ) {
        // 2. ส่ง flowerViewModel ให้หน้า Detail
        composable("flower_detail") {
            FlowerDetailScreen(
                modifier = Modifier,
                navController = navController,
                flowerViewModel = flowerViewModel,

            )
        }

        composable("home") {
            LoginScreen(modifier = Modifier, navController = navController)
        }
        composable("register") {
            RegisterScreen(modifier = Modifier, navController = navController)
        }
        composable("registerTwo") {
            RegisterScreenTwo(modifier = Modifier, navController = navController)
        }

        // 3. ส่ง flowerViewModel ให้หน้า Main (เพื่อให้เซตค่าดอกไม้ที่คลิกได้)
        composable("MainScreen") {
            MainScreen(
                modifier = Modifier,
                navController = navController,
                flowerViewModel = flowerViewModel,
                storeViewModel = storeViewModel
            )
        }

        composable("ShopScreen") {
            ShopScreen(modifier = Modifier, navController = navController)
        }
        composable("WhisListScreen") {
            WhisListScreen(modifier = Modifier, navController = navController)
        }
        composable("ProfileScreen") {
            ProfileScreen(modifier = Modifier, navController = navController)
        }

        composable("cart"){
            CartScreen(modifier = Modifier, navController = navController)
        }
    }
}


