// File: MainActivity.kt

package com.von.pokedex2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.von.pokedex2.ui.navigation.NavGraph
import com.von.pokedex2.ui.theme.Pokedex2Theme
import com.von.pokedex2.ui.theme.ThemePreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {

            val storedTheme = ThemePreference.getThemeFlow(this@MainActivity).first()
            setContent {
                var isDarkTheme by remember { mutableStateOf(storedTheme) }

                Pokedex2Theme(darkTheme = isDarkTheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()

                        NavGraph(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = {
                                isDarkTheme = !isDarkTheme
                                lifecycleScope.launch {
                                    ThemePreference.saveTheme(this@MainActivity, isDarkTheme)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
