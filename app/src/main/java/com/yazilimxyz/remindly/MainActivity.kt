package com.yazilimxyz.remindly

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yazilimxyz.remindly.screens.AddMeetingSheet
import com.yazilimxyz.remindly.screens.LoginScreen
import com.yazilimxyz.remindly.ui.theme.RemindlyTheme
import com.yazilimxyz.remindly.utilities.BottomNavigationBar
import com.yazilimxyz.remindly.utilities.NavigationHost
import kotlinx.coroutines.launch

class RemindlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.setLoggingEnabled(true)
    }
}

class MainActivity : ComponentActivity() {

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            RemindlyTheme {
                val navController = rememberNavController()

                var startDes : String = "loginScreen"

                if (user == null) {
                    startDes = "loginScreen"
                } else {
                    startDes = "mainScreen"
                }

                NavHost(navController = navController, startDestination = startDes) {
                    composable("loginScreen") { LoginScreen(navController) }
                    composable("mainScreen") { MainScreen(navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        BottomNavigationBar(navController = navController,
            onAddMeetingClick = { scope.launch { sheetState.show() } })
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {

            // sayfalara yönlendirir
            NavigationHost(navController = navController)

            // eğer add tıklanmışsa sheet'e yönlendirir
            if (sheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { scope.launch { sheetState.hide() } },
                    sheetState = sheetState
                ) {
                    AddMeetingSheet(LocalContext.current)
                }
            }
        }
    }
}



