package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class CartItem(
    val product: Product,
    var quantity: Int
)

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val date: String,
    val status: String,
    val deliveryEstimate: String
)

data class AppNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val type: NotificationType
)

enum class NotificationType { SUCCESS, PENDING, CANCELLED, INFO }

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _userName = MutableStateFlow("Visitante")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _userAddress = MutableStateFlow("Rua Principal, 100 - Centro")
    val userAddress: StateFlow<String> = _userAddress

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        refreshUserData()
    }

    fun refreshUserData() {
        val user = auth.currentUser
        if (user != null) {
            val email = user.email ?: ""
            _userEmail.value = email

            val nameFromEmail = email.substringBefore("@")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            _userName.value = nameFromEmail
        } else {
            _userName.value = "Visitante"
            _userEmail.value = ""
        }
    }

    fun updateName(newName: String) {
        _userName.value = newName
    }

    fun updateAddress(newAddress: String) {
        _userAddress.value = newAddress
        addNotification("Endereço Atualizado", "Novo local de entrega salvo.", NotificationType.INFO)
    }

    fun addToCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cartItems.value = currentCart
        calculateTotal()
        addNotification("Adicionado", "${product.name} foi para o carrinho.", NotificationType.INFO)
    }

    fun removeFromCart(cartItem: CartItem) {
        val currentCart = _cartItems.value.toMutableList()
        currentCart.remove(cartItem)
        _cartItems.value = currentCart
        calculateTotal()
        addNotification("Removido", "${cartItem.product.name} removido.", NotificationType.CANCELLED)
    }

    private fun calculateTotal() {
        _totalPrice.value = _cartItems.value.sumOf { it.product.price * it.quantity }
    }

    fun checkout() {
        val items = _cartItems.value
        val total = _totalPrice.value

        if (items.isEmpty()) return

        viewModelScope.launch {
            addNotification("Processando", "Validando pagamento...", NotificationType.PENDING)
            delay(2500)

            val orderId = "#${Random.nextInt(1000, 9999)}"
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val diasEntrega = Random.nextInt(2, 7)

            val newOrder = Order(
                id = orderId,
                items = items.toList(),
                total = total,
                date = today,
                status = "Em separação",
                deliveryEstimate = "Chega em $diasEntrega dias úteis"
            )

            val currentOrders = _orders.value.toMutableList()
            currentOrders.add(0, newOrder)
            _orders.value = currentOrders

            addNotification("Compra Aprovada!", "Pedido $orderId confirmado!", NotificationType.SUCCESS)

            _cartItems.value = emptyList()
            calculateTotal()
        }
    }

    fun simulateDirectPurchase(product: Product) {
        viewModelScope.launch {
            addNotification("Processando", "Comprando ${product.name}...", NotificationType.PENDING)
            delay(2000)

            val orderId = "#${Random.nextInt(1000, 9999)}"
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val newOrder = Order(
                id = orderId,
                items = listOf(CartItem(product, 1)),
                total = product.price,
                date = today,
                status = "Preparando envio",
                deliveryEstimate = "Chega em 3 dias"
            )

            val currentOrders = _orders.value.toMutableList()
            currentOrders.add(0, newOrder)
            _orders.value = currentOrders

            addNotification("Sucesso", "Compra rápida realizada!", NotificationType.SUCCESS)
        }
    }

    private fun addNotification(title: String, message: String, type: NotificationType) {
        val currentList = _notifications.value.toMutableList()
        currentList.add(0, AppNotification(title = title, message = message, type = type))
        _notifications.value = currentList
    }

    fun logout() {
        auth.signOut()
        refreshUserData()
        _cartItems.value = emptyList()
        _orders.value = emptyList()
    }
}