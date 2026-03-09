package com.example.flora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flora.ui.theme.FloraTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flora.Authentication.AuthViewModel
import com.example.flora.NavHost.AppNavhost
import com.example.flora.NavigationBar.Bottombar
import com.example.flora.NavigationBar.Topbar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val authVM = viewModel<AuthViewModel>()
            val authState by authVM.authState.collectAsState() // Chk State

            LaunchedEffect(authState) {
                if (authState is AuthViewModel.AuthState.Unauthenticated) {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            }



            val showNavBar = listOf( // หน้าที่ต้องการ โชว์ Topbar and Bottombar
                "MainScreen",
                "ShopScreen",
                "WhisListScreen",

            )

            val showBottomBar = listOf(
                "MainScreen",
                "ShopScreen",
                "WhisListScreen",
                "ProfileScreen"
            )

            FloraTheme {
                Scaffold(
                    topBar = {
                         if(currentRoute in showNavBar){
                             Topbar(navController = navController, authVM = authVM)
                         }
                    },
                    bottomBar = {
                        if(currentRoute in showBottomBar){
                            Bottombar(navController)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    containerColor = Color.Transparent
                    ) { innerPadding ->
                    AppNavhost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    )
                } // End Scaffold

            }
        }
    }
}
