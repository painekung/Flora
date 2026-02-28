package com.example.flora.NavHost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.flora.Authentication.LoginScreen
import com.example.flora.ScreenMain.MainScreen
import com.example.flora.ScreenMain.ProfileScreen
import com.example.flora.Authentication.RegisterScreen
import com.example.flora.Authentication.RegisterScreenTwo
import com.example.flora.ScreenMain.ShopScreen
import com.example.flora.ScreenMain.WhisListScreen

@Composable
fun AppNavhost(
    modifier: Modifier = Modifier,
    navController: NavHostController
)
{

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute= navBackStackEntry?.destination?.route


    NavHost(
        navController = navController,
        startDestination = "ShopScreen", // Screen Start,
        modifier = modifier
    ){

        // Screen
        composable("home"){
            LoginScreen(modifier = Modifier, navController = navController)
        }
        composable("register"){
            RegisterScreen(modifier = Modifier, navController = navController)
        }
        composable("registerTwo"){
            RegisterScreenTwo(modifier = Modifier, navController = navController)
        }


        // Menu Bottombar
        composable("MainScreen"){
            MainScreen(modifier = Modifier, navController = navController)
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
    }
}
