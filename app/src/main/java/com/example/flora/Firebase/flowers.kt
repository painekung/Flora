package com.example.flora.Firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ─────────────────────────────────────────
// 1. Data Classes
// ─────────────────────────────────────────

data class Flowers(
    val flowerID     : String       = "",
    val category     : String       = "",
    val categoryColor: String       = "",
    val desc         : String       = "",
    val image        : String       = "",
    val name         : String       = "",
    val price        : Int          = 0,
    val store        : StoreEmbedded = StoreEmbedded(),  // ← เปลี่ยนจาก storeID เป็น store object
    val color        : String       = "",
    val colorBtn     : String       = "",
    val rating       : Double       = 0.0
)

// ── ข้อมูลร้านที่ฝังอยู่ใน Flower ──
data class StoreEmbedded(
    val storeID  : String = "",
    val storeName: String = "",
    val location : String = ""
)

data class FlowerWithStore(
    val flower: Flowers,
    val store : Stores? = null
)

// ─────────────────────────────────────────
// 2. UI State สำหรับ Insert Form
// ─────────────────────────────────────────

sealed class InsertState {
    object Idle    : InsertState()
    object Loading : InsertState()
    object Success : InsertState()
    data class Error(val message: String) : InsertState()
}

// ─────────────────────────────────────────
// 3. FirebaseMangementFlower (DataSource)
// ─────────────────────────────────────────

class FirebaseMangementFlower {
    private val collection = Firebase.firestore.collection("Flowers")

    // ── READ ──
    suspend fun getAllFlowers(): List<Flowers> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Flowers::class.java)
        } catch (e: Exception) {
            Log.e("Firebase", "getAllFlowers error: ${e.message}")
            emptyList()
        }
    }

    // ── INSERT ──
    suspend fun insertFlower(flower: Flowers): Result<Unit> {
        return try {
            val existing = collection.document(flower.flowerID).get().await()
            if (existing.exists()) {
                return Result.failure(Exception("Flower ID '${flower.flowerID}' มีอยู่แล้ว"))
            }

            val data = mapOf(
                "flowerID"      to flower.flowerID,
                "category"      to flower.category,
                "categoryColor" to flower.categoryColor,
                "desc"          to flower.desc,
                "image"         to flower.image,
                "name"          to flower.name,
                "price"         to flower.price,
                "store"         to mapOf(           // ← เก็บ store เป็น nested map
                    "storeID"   to flower.store.storeID,
                    "storeName" to flower.store.storeName,
                    "location"  to flower.store.location
                ),
                "color"         to flower.color,
                "colorBtn"      to flower.colorBtn,
                "rating"        to flower.rating
            )

            collection.document(flower.flowerID).set(data).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("Firebase", "insertFlower error: ${e.message}")
            Result.failure(e)
        }
    }

    // ── UPDATE ──
    suspend fun updateFlower(flower: Flowers): Result<Unit> {
        return try {
            val data = mapOf(
                "category"  to flower.category,
                "desc"      to flower.desc,
                "image"     to flower.image,
                "name"      to flower.name,
                "price"     to flower.price,
                "store"     to mapOf(
                    "storeID"   to flower.store.storeID,
                    "storeName" to flower.store.storeName,
                    "location"  to flower.store.location
                ),
                "color"     to flower.color,
                "colorBtn"  to flower.colorBtn,
                "rating"    to flower.rating
            )
            collection.document(flower.flowerID).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firebase", "updateFlower error: ${e.message}")
            Result.failure(e)
        }
    }

    // ── DELETE ──
    suspend fun deleteFlower(flowerID: String): Result<Unit> {
        return try {
            collection.document(flowerID).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firebase", "deleteFlower error: ${e.message}")
            Result.failure(e)
        }
    }
}

// ─────────────────────────────────────────
// 4. Repository
// ─────────────────────────────────────────

class FlowersRepository(
    private val dataSource: FirebaseMangementFlower = FirebaseMangementFlower()
) {
    suspend fun getAllFlowers()           = dataSource.getAllFlowers()
    suspend fun insertFlower(f: Flowers) = dataSource.insertFlower(f)
    suspend fun updateFlower(f: Flowers) = dataSource.updateFlower(f)
    suspend fun deleteFlower(id: String) = dataSource.deleteFlower(id)
}

// ─────────────────────────────────────────
// 5. ViewModel
// ─────────────────────────────────────────

class FlowersViewModel(
    private val flowerRepo: FlowersRepository = FlowersRepository(),
    private val storeRepo : StoreRepository   = StoreRepository()
) : ViewModel() {

    private val _allFlowers    = MutableStateFlow<List<Flowers>>(emptyList())
    val allFlowers = _allFlowers.asStateFlow()

    private val _selectedFlower = MutableStateFlow<Flowers?>(null)
    val selectedFlower = _selectedFlower.asStateFlow()

    private val _selectedStore = MutableStateFlow<Stores?>(null)
    val selectedStore = _selectedStore.asStateFlow()

    private val _insertState = MutableStateFlow<InsertState>(InsertState.Idle)
    val insertState = _insertState.asStateFlow()

    init { fetchAllFlowers() }

    // ── READ ──
    fun fetchAllFlowers() {
        viewModelScope.launch {
            _allFlowers.value = flowerRepo.getAllFlowers()
        }
    }

    // ── SELECT ──
    fun selectFlower(flower: Flowers) {
        _selectedFlower.value = flower
        fetchStoreData(flower.store.storeID)  // ← ดึง storeID จาก nested object
    }

    private fun fetchStoreData(storeId: String) {
        viewModelScope.launch {
            try {
                val stores = storeRepo.getAllStores()
                _selectedStore.value = stores.find { it.storeID == storeId }
            } catch (e: Exception) {
                Log.e("Firebase", "fetchStoreData error: ${e.message}")
            }
        }
    }

    // ── INSERT ──
    fun insertFlower(flower: Flowers) {
        val error = validateFlower(flower)
        if (error != null) {
            _insertState.value = InsertState.Error(error)
            return
        }

        viewModelScope.launch {
            _insertState.value = InsertState.Loading
            flowerRepo.insertFlower(flower)
                .onSuccess {
                    _insertState.value = InsertState.Success
                    fetchAllFlowers()
                    Log.d("Firebase", "Insert '${flower.name}' สำเร็จ")
                }
                .onFailure { e ->
                    _insertState.value = InsertState.Error(e.message ?: "เกิดข้อผิดพลาด")
                    Log.e("Firebase", "Insert failed: ${e.message}")
                }
        }
    }

    // ── UPDATE ──
    fun updateFlower(flower: Flowers) {
        viewModelScope.launch {
            _insertState.value = InsertState.Loading
            flowerRepo.updateFlower(flower)
                .onSuccess {
                    _insertState.value = InsertState.Success
                    fetchAllFlowers()
                }
                .onFailure { e ->
                    _insertState.value = InsertState.Error(e.message ?: "อัปเดตไม่สำเร็จ")
                }
        }
    }

    // ── DELETE ──
    fun deleteFlower(flowerID: String) {
        viewModelScope.launch {
            flowerRepo.deleteFlower(flowerID)
                .onSuccess {
                    fetchAllFlowers()
                    Log.d("Firebase", "Delete '$flowerID' สำเร็จ")
                }
                .onFailure { e ->
                    Log.e("Firebase", "Delete failed: ${e.message}")
                }
        }
    }

    fun resetInsertState() { _insertState.value = InsertState.Idle }

    // ── Validation ──
    private fun validateFlower(flower: Flowers): String? = when {
        flower.flowerID.isBlank()       -> "กรุณากรอก Flower ID"
        flower.name.isBlank()           -> "กรุณากรอกชื่อดอกไม้"
        flower.category.isBlank()       -> "กรุณาเลือกหมวดหมู่"
        flower.price <= 0               -> "ราคาต้องมากกว่า 0"
        flower.store.storeID.isBlank()  -> "กรุณาเลือกร้านค้า"
        else                            -> null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FlowersViewModel() as T
        }
    }
}