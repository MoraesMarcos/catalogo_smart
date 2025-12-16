package com.example.myapplication.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.cart.CartScreen
import com.example.myapplication.ui.favorites.FavoriteScreen
import com.example.myapplication.ui.notification.NotificationScreen
import com.example.myapplication.ui.product.ProductListScreen
import com.example.myapplication.ui.profile.ProfileScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.viewmodel.UserViewModel

@Composable
fun MainContainerScreen(
    viewModel: ProductViewModel,
    userViewModel: UserViewModel,
    onProductClick: (Int) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenAddress: () -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val cartItems by userViewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current

    fun checkAuth(action: () -> Unit) {
        if (isLoggedIn) {
            action()
        } else {
            Toast.makeText(context, "Faça login para continuar", Toast.LENGTH_SHORT).show()
            onNavigateToLogin()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )

                NavigationBarItem(
                    icon = {
                        BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrinho")
                        }
                    },
                    label = { Text("Carrinho") },
                    selected = selectedItem == 1,
                    onClick = {
                        checkAuth { selectedItem = 1 }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") },
                    selected = selectedItem == 2,
                    onClick = {
                        checkAuth { selectedItem = 2 }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "Avisos") },
                    label = { Text("Avisos") },
                    selected = selectedItem == 3,
                    onClick = {
                        checkAuth { selectedItem = 3 }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = selectedItem == 4,
                    onClick = {
                        checkAuth { selectedItem = 4 }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> ProductListScreen(
                    uiStateFlow = viewModel.uiState,
                    favoriteIdsFlow = viewModel.favoriteIds,
                    isLoggedIn = isLoggedIn,
                    onReload = { viewModel.loadProducts() },
                    onProductClick = onProductClick,
                    onToggleFavorite = { id ->
                        checkAuth { viewModel.toggleFavorite(id) }
                    },
                    onOpenFavorites = { checkAuth { selectedItem = 2 } },
                    onOpenCategories = onNavigateToCategories,
                    onLoginClick = onNavigateToLogin,
                    onLogout = onLogout
                )
                1 -> CartScreen(userViewModel = userViewModel)
                2 -> FavoriteScreen(
                    favorites = viewModel.getFavoriteProducts(),
                    favoriteIds = favoriteIds,
                    onBackClick = { selectedItem = 0 },
                    onProductClick = onProductClick,
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
                3 -> NotificationScreen(userViewModel = userViewModel)
                4 -> ProfileScreen(
                    userViewModel = userViewModel,
                    onLogout = onLogout,
                    onOpenOrders = onOpenOrders,
                    onOpenAddress = onOpenAddress
                )
            }
        }
    }
}