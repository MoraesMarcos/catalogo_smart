package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.app_catalogo_produto.ui.AppNavHost
import com.example.app_catalogo_produto.ui.theme.CatalogoSmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatalogoSmartTheme {
                AppNavHost()
            }
        }
    }
}