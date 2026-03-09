package com.example.flora.Firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

// ─────────────────────────────────────────
// 1. Data Classes
// ─────────────────────────────────────────

data class EmbeddedStore(
    val storeID   : String = "",
    val storeName : String = "",
    val location  : String = ""
)

data class OrderItem(
    val flowerID   : String        = "",
    val flowerName : String        = "",
    val price      : Int           = 0,
    val quantity   : Int           = 0,
    val image      : String        = "",
    val store      : EmbeddedStore = EmbeddedStore()
)

data class Order(
    val orderID    : String          = "",
    val email      : String          = "",
    val items      : List<OrderItem> = emptyList(),
    val totalPrice : Int             = 0,
    val status     : String          = "pending",
    val createdAt  : Date?           = null
)

// ─────────────────────────────────────────
// 2. UI State
// ─────────────────────────────────────────

sealed class OrderState {
    object Idle     : OrderState()
    object Loading  : OrderState()
    object Success  : OrderState()
    object Checkout : OrderState()
    data class Error(val message: String) : OrderState()
}

// ─────────────────────────────────────────
// 3. Helper
// ─────────────────────────────────────────

private fun OrderItem.toMap() = mapOf(
    "flowerID"   to flowerID,
    "flowerName" to flowerName,
    "price"      to price,
    "quantity"   to quantity,
    "image"      to image,
    "store"      to mapOf(
        "storeID"   to store.storeID,
        "storeName" to store.storeName,
        "location"  to store.location
    )
)

// ─────────────────────────────────────────
// 4. DataSource
// ─────────────────────────────────────────

class FirebaseManagementOrder {
    private val collection = Firebase.firestore.collection("Orders")

    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef   = collection.document()
            val newOrder = order.copy(orderID = docRef.id, createdAt = Date())
            val data = mapOf(
                "orderID"    to newOrder.orderID,
                "email"      to newOrder.email,
                "items"      to newOrder.items.map { it.toMap() },
                "totalPrice" to newOrder.totalPrice,
                "status"     to newOrder.status,
                "createdAt"  to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("OrderDB", "createOrder error: ${e.message}")
            Result.failure(e)
        }
    }

    // ── แก้ไขหลัก: ถ้า items หมดหลัง filter → ลบ document ทิ้งเลย ──
    suspend fun updateItemQuantity(orderID: String, flowerID: String, newQuantity: Int): Result<Unit> {
        return try {
            val doc   = collection.document(orderID).get().await()
            val order = doc.toObject(Order::class.java)
                ?: return Result.failure(Exception("Order ไม่พบ"))

            val updatedItems = order.items
                .map { if (it.flowerID == flowerID) it.copy(quantity = newQuantity) else it }
                .filter { it.quantity > 0 }

            if (updatedItems.isEmpty()) {
                // ── items หมดแล้ว → ลบ order document ออกเลย ──
                collection.document(orderID).delete().await()
                Log.d("OrderDB", "ลบ order $orderID เพราะ items หมด")
            } else {
                val newTotal = updatedItems.sumOf { it.price * it.quantity }
                collection.document(orderID).update(
                    mapOf(
                        "items"      to updatedItems.map { it.toMap() },
                        "totalPrice" to newTotal
                    )
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OrderDB", "updateItemQuantity error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun checkoutOrder(orderID: String): Result<Unit> {
        return try {
            collection.document(orderID).update(
                mapOf(
                    "status"    to "success",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OrderDB", "checkoutOrder error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPendingOrder(email: String): Order? {
        return try {
            val snapshot = collection
                .whereEqualTo("email", email)
                .whereEqualTo("status", "pending")
                .limit(1)
                .get().await()
            snapshot.toObjects(Order::class.java).firstOrNull()
                .also { Log.d("OrderDB", "getPendingOrder: $it") }
        } catch (e: Exception) {
            Log.e("OrderDB", "getPendingOrder error: ${e.message}")
            null
        }
    }

    suspend fun getOrdersByUser(email: String): List<Order> {
        return try {
            val snapshot = collection
                .whereEqualTo("email", email)
                .get().await()
            snapshot.toObjects(Order::class.java)
        } catch (e: Exception) {
            Log.e("OrderDB", "getOrdersByUser error: ${e.message}")
            emptyList()
        }
    }
}

// ─────────────────────────────────────────
// 5. Repository
// ─────────────────────────────────────────

class OrderRepository(
    private val dataSource: FirebaseManagementOrder = FirebaseManagementOrder()
) {
    suspend fun createOrder(order: Order)                                        = dataSource.createOrder(order)
    suspend fun updateItemQuantity(orderID: String, flowerID: String, qty: Int) = dataSource.updateItemQuantity(orderID, flowerID, qty)
    suspend fun getOrdersByUser(email: String)                                  = dataSource.getOrdersByUser(email)
    suspend fun getPendingOrder(email: String)                                  = dataSource.getPendingOrder(email)
    suspend fun checkoutOrder(orderID: String)                                  = dataSource.checkoutOrder(orderID)
}

// ─────────────────────────────────────────
// 6. ViewModel
// ─────────────────────────────────────────

class OrderViewModel(
    private val repo: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _currentOrder     = MutableStateFlow<Order?>(null)
    val currentOrder              = _currentOrder.asStateFlow()

    private val _cartItems        = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems                 = _cartItems.asStateFlow()

    private val _orderState       = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState                = _orderState.asStateFlow()

    private val _orderHistory     = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory              = _orderHistory.asStateFlow()

    private val _isHistoryLoading = MutableStateFlow(false)
    val isHistoryLoading          = _isHistoryLoading.asStateFlow()

    // ─────────────────────────────────────
    // Cart Functions
    // ─────────────────────────────────────

    fun loadCart(email: String = "user@example.com") {
        viewModelScope.launch {
            val pending = repo.getPendingOrder(email)
            _currentOrder.value = pending
            _cartItems.value    = pending?.items ?: emptyList()
            Log.d("OrderVM", "loadCart: ${_cartItems.value.size} items")
        }
    }

    fun addToCart(flower: Flowers, email: String = "user@example.com") {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading
            try {
                if (_currentOrder.value == null) {
                    val pending = repo.getPendingOrder(email)
                    _currentOrder.value = pending
                    _cartItems.value    = pending?.items ?: emptyList()
                }

                val existingOrder = _currentOrder.value
                val embeddedStore = EmbeddedStore(
                    storeID   = flower.store.storeID,
                    storeName = flower.store.storeName,
                    location  = flower.store.location
                )

                if (existingOrder == null) {
                    val newOrder = Order(
                        email  = email,
                        items  = listOf(
                            OrderItem(
                                flowerID   = flower.flowerID,
                                flowerName = flower.name,
                                price      = flower.price,
                                quantity   = 1,
                                image      = flower.image,
                                store      = embeddedStore
                            )
                        ),
                        totalPrice = flower.price,
                        status     = "pending"
                    )
                    repo.createOrder(newOrder)
                        .onSuccess { id ->
                            _currentOrder.value = newOrder.copy(orderID = id)
                            _cartItems.value    = newOrder.items
                            _orderState.value   = OrderState.Success
                            Log.d("OrderVM", "สร้าง order ใหม่: $id")
                        }
                        .onFailure { e ->
                            _orderState.value = OrderState.Error(e.message ?: "เกิดข้อผิดพลาด")
                        }
                } else {
                    val currentItems  = existingOrder.items.toMutableList()
                    val existingIndex = currentItems.indexOfFirst { it.flowerID == flower.flowerID }

                    val updatedItems = if (existingIndex >= 0) {
                        currentItems[existingIndex] = currentItems[existingIndex]
                            .copy(quantity = currentItems[existingIndex].quantity + 1)
                        currentItems
                    } else {
                        currentItems.add(
                            OrderItem(
                                flowerID   = flower.flowerID,
                                flowerName = flower.name,
                                price      = flower.price,
                                quantity   = 1,
                                image      = flower.image,
                                store      = embeddedStore
                            )
                        )
                        currentItems
                    }

                    val newTotal     = updatedItems.sumOf { it.price * it.quantity }
                    val updatedOrder = existingOrder.copy(items = updatedItems, totalPrice = newTotal)

                    Firebase.firestore.collection("Orders")
                        .document(existingOrder.orderID)
                        .update(
                            mapOf(
                                "items"      to updatedItems.map { it.toMap() },
                                "totalPrice" to newTotal
                            )
                        ).await()

                    _currentOrder.value = updatedOrder
                    _cartItems.value    = updatedItems
                    _orderState.value   = OrderState.Success
                }
            } catch (e: Exception) {
                _orderState.value = OrderState.Error(e.message ?: "เกิดข้อผิดพลาด")
                Log.e("OrderVM", "addToCart error: ${e.message}")
            }
        }
    }

    fun updateQuantity(flowerID: String, newQty: Int) {
        viewModelScope.launch {
            val order = _currentOrder.value ?: return@launch

            repo.updateItemQuantity(order.orderID, flowerID, newQty)
                .onSuccess {
                    val updatedItems = _cartItems.value
                        .map { if (it.flowerID == flowerID) it.copy(quantity = newQty) else it }
                        .filter { it.quantity > 0 }

                    // ── ถ้า items หมด → clear state ด้วย ──
                    if (updatedItems.isEmpty()) {
                        _cartItems.value    = emptyList()
                        _currentOrder.value = null
                        Log.d("OrderVM", "cart ว่างแล้ว clear state")
                    } else {
                        _cartItems.value    = updatedItems
                        _currentOrder.value = order.copy(
                            items      = updatedItems,
                            totalPrice = updatedItems.sumOf { it.price * it.quantity }
                        )
                    }
                }
                .onFailure { e -> Log.e("OrderVM", "updateQuantity error: ${e.message}") }
        }
    }

    // ── removeItem ใช้ updateQuantity(qty=0) เหมือนเดิม ──
    // ── logic ลบ order อัตโนมัติอยู่ใน updateQuantity แล้ว ──
    fun removeItem(flowerID: String) { updateQuantity(flowerID, 0) }

    fun checkoutOrder() {
        viewModelScope.launch {
            val order = _currentOrder.value ?: return@launch
            _orderState.value = OrderState.Loading
            repo.checkoutOrder(order.orderID)
                .onSuccess {
                    _currentOrder.value = null
                    _cartItems.value    = emptyList()
                    _orderState.value   = OrderState.Checkout
                    Log.d("OrderVM", "checkout สำเร็จ: ${order.orderID}")
                }
                .onFailure { e ->
                    _orderState.value = OrderState.Error(e.message ?: "ชำระเงินไม่สำเร็จ")
                    Log.e("OrderVM", "checkoutOrder error: ${e.message}")
                }
        }
    }

    // ─────────────────────────────────────
    // History Functions
    // ─────────────────────────────────────

    fun loadOrderHistory(email: String) {
        viewModelScope.launch {
            _isHistoryLoading.value = true
            try {
                val all = repo.getOrdersByUser(email)
                _orderHistory.value = all.sortedByDescending { it.createdAt }
                Log.d("OrderVM", "loadOrderHistory: ${all.size} orders")
            } catch (e: Exception) {
                Log.e("OrderVM", "loadOrderHistory error: ${e.message}")
            } finally {
                _isHistoryLoading.value = false
            }
        }
    }

    fun resetOrderState() { _orderState.value = OrderState.Idle }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderViewModel() as T
        }
    }
}