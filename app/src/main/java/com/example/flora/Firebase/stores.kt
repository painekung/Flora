package com.example.flora.Firebase//package com.example.flora.Firebase


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flora.models.Store
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// 1. ข้อมูลผู้ใช้
data class User(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "customer" // "customer" หรือ "store_owner"
)

// 2. ข้อมูลร้านค้า (เชื่อมกับ User ด้วย userId)
data class Stores(
    val storeID: String = "",
    val userID: String = "", // อ้างอิงถึง User.userId
    val storeName: String = "",
    val location: String = "",
    val description: String = ""
)

class FirebaseManagementStore {
    private val collection = Firebase.firestore.collection("Stores")

    // Insert: ใช้ set(store) พร้อมระบุ ID ของตัวเอง หรือใช้ add() ก็ได้
    suspend fun insert(store: Stores) {
        // แนะนำให้ใช้ storeID ที่เรากำหนดเองเป็น Document ID ไปเลยเพื่อการจัดการที่ง่าย
        collection.document(store.storeID).set(store).await()
    }

    // Update: แก้ไขเฉพาะฟิลด์ที่ต้องการ
    suspend fun update(store: Stores) {
        collection.document(store.storeID).update(
            mapOf(
                "storeName" to store.storeName,
                "location" to store.location,
            )
        ).await()
    }

    // Delete
    suspend fun delete(storeID: String) {
        collection.document(storeID).delete().await()
    }

    suspend fun getUserIdByEmail(email: String): String? {
        val db = Firebase.firestore
        val snapshot = db.collection("Users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        // ✅ เปลี่ยนจาก .id → .getString("userID")
        return snapshot.documents.firstOrNull()?.getString("userID")
    }

    suspend fun getAllStores(): List<Stores> {
        val snapshot = collection.get().await()
        return snapshot.toObjects(Stores::class.java)
    }

    // Get Store By User: ดึงข้อมูลแบบ Real-time Flow
    fun getStoreByUser(userId: String): Flow<List<Stores>> = callbackFlow {
        val listenerRegistration = collection
            .whereEqualTo("userID", userId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val stores = snapshot.toObjects(Stores::class.java)
                    trySend(stores)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}

class StoreRepository(
    private val dataSource: FirebaseManagementStore = FirebaseManagementStore()
) {
    // ฟังก์ชันดึงข้อมูลร้านค้าตาม User ID
    fun getStore(userId: String) = dataSource.getStoreByUser(userId)

    suspend fun getUserByEmail(email: String) = dataSource.getUserIdByEmail(email)

    suspend fun getAllStores() = dataSource.getAllStores()

    suspend fun insert(store: Stores) {
        dataSource.insert(store)
    }

    suspend fun update(store: Stores) {
        dataSource.update(store)
    }

    suspend fun delete(storeID: String) {
        dataSource.delete(storeID)
    }
}

class StoreViewModel(
    private val repository: StoreRepository = StoreRepository()
) : ViewModel() {

    init {
        android.util.Log.d("StoreViewModel", "ViewModel created successfully")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StoreViewModel() as T
            }
        }
    }

    // เก็บข้อมูลร้านค้าที่จะโชว์ใน UI
    private val _currentStore = MutableStateFlow<List<Stores>>(emptyList())
    private  val _allStore = MutableStateFlow<List<Store>>(emptyList())
    val currentStore = _currentStore.asStateFlow()



    fun fetchStoreByEmail(email: String) {
        viewModelScope.launch {
            // 1. หา userID จาก email ก่อน
            // 1. หา userID
            val userId = repository.getUserByEmail(email)
            android.util.Log.d("StoreDebug", "หา UserID จาก $email เจอเป็น: $userId")


            if (userId != null) {
                // 2. ดึงข้อมูลร้าน
                repository.getStore(userId).collect { storeList ->
                    android.util.Log.d("StoreDebug", "จำนวนร้านที่เจอสำหรับ $userId: ${storeList.toString()}")
                    _currentStore.value = storeList
                }
            } else {
                android.util.Log.e("StoreDebug", "ไม่พบ User ที่ใช้อีเมลนี้ในระบบ")
                _currentStore.value = emptyList()
            }
        }
    }

    fun fetchAllStores() {
        viewModelScope.launch {
            val stores = repository.getAllStores()
            _currentStore.value = stores
            android.util.Log.d("fff", "จำนวนร้านที่เจอสำหรับ ${_currentStore.value.toString()}")

        }
    }

    // ฟังก์ชันสร้างร้านค้าใหม่
    fun insertStore(storeID: String, userID: String, storeName: String, location: String) {
        viewModelScope.launch {
            repository.insert(
                Stores(
                    storeID = storeID,
                    userID = userID,
                    storeName = storeName,
                    location = location
                )
            )
        }
    }

    // ฟังก์ชันอัปเดตข้อมูลร้าน
    fun updateStore(store: Stores) {
        viewModelScope.launch {
            repository.update(store)
        }
    }

    // ฟังก์ชันลบร้าน
    fun deleteStore(storeID: String) {
        viewModelScope.launch {
            repository.delete(storeID)
        }
    }

    // วิธีการดึงข้อมูลมาโชว์ใน UI (ใช้ร่วมกับ collectAsState ใน Compose)
    fun getMyStore(userId: String): Flow<List<Stores>> {
        return repository.getStore(userId)
    }
}