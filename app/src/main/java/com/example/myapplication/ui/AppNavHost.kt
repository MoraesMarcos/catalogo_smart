package com.example.myapplication.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.category.CategoryProductsScreen
import com.example.myapplication.ui.category.CategoryScreen
import com.example.myapplication.ui.favorites.FavoriteScreen
import com.example.myapplication.ui.product.ProductDetailScreen
import com.example.myapplication.ui.product.ProductDetailViewModel
import com.example.myapplication.ui.product.ProductListScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.shimmer.ShimmerListScreen
import com.example.myapplication.ui.state.ProductUiState
import com.example.myapplication.ui.welcome.WelcomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: ProductViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen(
                onEnterClick = {
                    navController.navigate("productList") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("productList") {
            ProductListScreen(
                uiStateFlow = viewModel.uiState,
                favoriteIdsFlow = viewModel.favoriteIds,
                onReload = { viewModel.loadProducts() },
                onProductClick = { id -> navController.navigate("productDetail/$id") },
                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                onOpenFavorites = { navController.navigate("favorites") },
                onOpenCategories = { navController.navigate("categories") },
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->

            val id = entry.arguments?.getInt("productId") ?: 0
            val detailVM: ProductDetailViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            LaunchedEffect(uiState) {
                val products = (uiState as? ProductUiState.Success)?.products ?: emptyList()
                detailVM.setProducts(products)
            }

            ProductDetailScreen(
                productId = id,
                viewModel = detailVM,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("favorites") {

            val favoriteIds by viewModel.favoriteIds.collectAsState()

            FavoriteScreen(
                favorites = viewModel.getFavoriteProducts(),
                favoriteIds = favoriteIds,
                onBackClick = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate("productDetail/$id") },
                onToggleFavorite = { id -> viewModel.toggleFavorite(id) }
            )
        }

        composable("categories") {

            CategoryScreen(
                categories = viewModel.getCategories(),
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category ->
                    val encoded = URLEncoder.encode(
                        category,
                        StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate("categoryProducts/$encoded")
                }
            )
        }

        composable(
            route = "categoryProducts/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { entry ->

            val encoded = entry.arguments?.getString("category") ?: ""
            val category = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())

            val favoriteIds by viewModel.favoriteIds.collectAsState()
            val products = viewModel.getProductsByCategory(category)

            if (products.isEmpty()) {
                Toast.makeText(
                    context,
                    "Nenhum produto encontrado para $category",
                    Toast.LENGTH_SHORT
                ).show()
            }

            CategoryProductsScreen(
                title = category,
                products = products,
                favoriteIds = favoriteIds,
                onBackClick = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate("productDetail/$id") },
                onToggleFavorite = { id -> viewModel.toggleFavorite(id) }
            )
        }

        composable("loading") {
            ShimmerListScreen()
        }
    }
}