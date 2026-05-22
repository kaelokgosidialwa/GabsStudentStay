package com.example.gabsstudentstay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.navigation.NavGraph
import com.example.gabsstudentstay.navigation.Screen
import com.example.gabsstudentstay.ui.theme.GabsStudentStayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GabsStudentStayTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    UserRepository.getCurrentUser(
                        onSuccess = { user ->
                            startDestination = when {
                                user == null -> Screen.Main.route
                                user.role == "LESSOR" -> Screen.LessorDashboard.route
                                else -> Screen.Main.route
                            }
                        },
                        onError = {
                            startDestination = Screen.Main.route
                        }
                    )
                }

                startDestination?.let {
                    NavGraph(
                        navController = navController,
                        startDestination = it
                    )
                }
            }
        }
    }
}