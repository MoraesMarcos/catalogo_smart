package com.example.myapplication.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.repository.AuthRepositoryImpl
import com.example.myapplication.data.repository.ProductRepositoryImpl
import com.example.myapplication.ui.category.CategoryProductsScreen
import com.example.myapplication.ui.category.CategoryScreen
import com.example.myapplication.ui.favorites.FavoriteScreen
import com.example.myapplication.ui.login.LoginScreen
import com.example.myapplication.ui.login.LoginViewModel
import com.example.myapplication.ui.login.LoginViewModelFactory
import com.example.myapplication.ui.login.SignUpScreen
import com.example.myapplication.ui.login.SignUpViewModel
import com.example.myapplication.ui.login.SignUpViewModelFactory
import com.example.myapplication.ui.product.ProductDetailScreen
import com.example.myapplication.ui.product.ProductDetailViewModel
import com.example.myapplication.ui.product.ProductDetailViewModelFactory
import com.example.myapplication.ui.product.ProductListScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.shimmer.ShimmerProductListPlaceholder
import com.example.myapplication.ui.welcome.WelcomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val context = LocalContext.current


    val authRepository = remember { AuthRepositoryImpl() }


    val startDestination = if (authRepository.isUserLoggedIn()) "productList" else "welcome"

    val viewModel: ProductViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        composable("welcome") {
            WelcomeScreen(
                onEnterClick = {
                    navController.navigate("login")
                }
            )
        }


        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authRepository)
            )

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {

                    navController.navigate("productList") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToSignUp = {

                    navController.navigate("signup")
                }
            )
        }


        composable("signup") {
            val signUpViewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModelFactory(authRepository)
            )

            SignUpScreen(
                viewModel = signUpViewModel,
                onSignUpSuccess = {

                    authRepository.logout()

                    Toast.makeText(context, "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show()


                    navController.popBackStack()
                },
                onBackToLogin = {

                    navController.popBackStack()
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

                    authRepository.logout()


                    navController.navigate("welcome") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }


        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("productId") ?: 0
            val repository = ProductRepositoryImpl(context)
            val detailVM: ProductDetailViewModel = viewModel(
                factory = ProductDetailViewModelFactory(repository)
            )
            LaunchedEffect(id) { detailVM.loadProduct(id) }

            ProductDetailScreen(
                productId = id,
                viewModel = detailVM,
                onBackClick = { navController.popBackStack() }
            )
        }


        composable("favorites") {
            val favoriteIds by viewModel.favoriteIds.collectAsState(initial = emptySet())
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
                    val encoded = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
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
            val favoriteIds by viewModel.favoriteIds.collectAsState(initial = emptySet())
            val products = viewModel.getProductsByCategory(category)

            if (products.isEmpty()) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Nenhum produto encontrado para $category", Toast.LENGTH_SHORT).show()
                }
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
            ShimmerProductListPlaceholder()
        }
    }
}