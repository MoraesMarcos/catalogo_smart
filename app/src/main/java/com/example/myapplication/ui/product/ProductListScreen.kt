package com.example.myapplication.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.shimmer.ShimmerProductListPlaceholder
import com.example.myapplication.ui.state.ProductUiState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    uiStateFlow: StateFlow<ProductUiState>,
    favoriteIdsFlow: StateFlow<Set<Int>>,
    onReload: () -> Unit,
    onProductClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenCategories: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by uiStateFlow.collectAsState()
    val favoriteIds by favoriteIdsFlow.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = { Text("Ofertas") },
                modifier = Modifier.shadow(8.dp),
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair"
                        )
                    }
                },

                actions = {
                    IconButton(onClick = onOpenCategories) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Categorias"
                        )
                    }
                    IconButton(onClick = onOpenFavorites) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favoritos"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (uiState) {
                is ProductUiState.Loading -> { ShimmerProductListPlaceholder() }
                is ProductUiState.Error -> {
                    val msg = (uiState as ProductUiState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = msg, color = MaterialTheme.colorScheme.onBackground)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = onReload) { Text("Tentar novamente") }
                        }
                    }
                }
                is ProductUiState.Success -> {
                    val products = (uiState as ProductUiState.Success).products
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            ProductItemCard(
                                product = product,
                                isFavorite = favoriteIds.contains(product.id),
                                onClick = { onProductClick(product.id) },
                                onToggleFavorite = { onToggleFavorite(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}