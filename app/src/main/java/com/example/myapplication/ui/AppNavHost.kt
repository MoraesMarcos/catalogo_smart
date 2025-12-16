package com.example.myapplication.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.myapplication.ui.login.LoginScreen
import com.example.myapplication.ui.login.LoginViewModel
import com.example.myapplication.ui.login.LoginViewModelFactory
import com.example.myapplication.ui.login.SignUpScreen
import com.example.myapplication.ui.login.SignUpViewModel
import com.example.myapplication.ui.login.SignUpViewModelFactory
import com.example.myapplication.ui.main.MainContainerScreen
import com.example.myapplication.ui.orders.OrdersScreen
import com.example.myapplication.ui.profile.AddressScreen
import com.example.myapplication.ui.product.ProductDetailScreen
import com.example.myapplication.ui.product.ProductDetailViewModel
import com.example.myapplication.ui.product.ProductDetailViewModelFactory
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.viewmodel.UserViewModel
import com.example.myapplication.ui.welcome.WelcomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authRepository = remember { AuthRepositoryImpl() }

    val startDestination = if (authRepository.isUserLoggedIn()) "main" else "welcome"

    val productViewModel: ProductViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("welcome") {
            WelcomeScreen(onEnterClick = { navController.navigate("login") })
        }

        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    userViewModel.refreshUserData()
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            val signUpViewModel: SignUpViewModel = viewModel(factory = SignUpViewModelFactory(authRepository))
            SignUpScreen(
                viewModel = signUpViewModel,
                onSignUpSuccess = {
                    authRepository.logout()
                    Toast.makeText(context, "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("main") {
            MainContainerScreen(
                viewModel = productViewModel,
                userViewModel = userViewModel,
                onProductClick = { id -> navController.navigate("productDetail/$id") },
                onNavigateToCategories = { navController.navigate("categories") },
                onLogout = {
                    userViewModel.logout()
                    navController.navigate("welcome") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onOpenOrders = { navController.navigate("orders") },
                onOpenAddress = { navController.navigate("address") }
            )
        }

        composable("categories") {
            CategoryScreen(
                categories = productViewModel.getCategories(),
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category ->
                    val encoded = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
                    navController.navigate("categoryProducts/$encoded")
                }
            )
        }

        composable("orders") {
            OrdersScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("address") {
            AddressScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("productId") ?: 0
            val repository = ProductRepositoryImpl(context)
            val detailVM: ProductDetailViewModel = viewModel(factory = ProductDetailViewModelFactory(repository))

            val favoriteIds by productViewModel.favoriteIds.collectAsState()
            val isFav = favoriteIds.contains(id)

            ProductDetailScreen(
                productId = id,
                viewModel = detailVM,
                userViewModel = userViewModel,
                isFavorite = isFav,
                onToggleFavorite = { productViewModel.toggleFavorite(id) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "categoryProducts/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { entry ->
            val encoded = entry.arguments?.getString("category") ?: ""
            val category = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            val favoriteIds by productViewModel.favoriteIds.collectAsState()
            val products = productViewModel.getProductsByCategory(category)

            CategoryProductsScreen(
                title = category,
                products = products,
                favoriteIds = favoriteIds,
                onBackClick = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate("productDetail/$id") },
                onToggleFavorite = { id -> productViewModel.toggleFavorite(id) }
            )
        }
    }
}