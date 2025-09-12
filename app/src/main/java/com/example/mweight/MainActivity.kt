package com.example.mweight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mweight.ui.theme.MWeightTheme

class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppModule.initialize(this)

        setContent {
            MWeightTheme(darkTheme = isSystemInDarkTheme()) {
                AppNav()
            }
        }
    }


    @Composable
    fun AppNav() {
        Log.d("","ciao")
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.Home.name
        ) {
            composable(Screen.Home.name) {
                HomeScreen(onNavigateToApp = { navController.navigate(Screen.Weight.name)})
            }

            composable(Screen.Weight.name) {

                val model: WeightViewModel = viewModel(
                    factory = WeightViewModelFactory(AppModule.getRepository())
                )

                val weightEntries = model.allEntries.collectAsState(initial = emptyList()).value

                Log.d("test", "${weightEntries.size}")

                WeightScreen(model)
            }
        }
    }

}