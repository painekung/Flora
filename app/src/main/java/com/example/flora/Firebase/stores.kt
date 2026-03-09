package com.example.flora.Firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ── Data Class: ข้อมูล User ที่ฝังอยู่ใน Store ──
data class UserEmbedded(
    val userID     : String = "",
    val name       : String = "",
    val email      : String = "",
    val username   : String = "",
    val phoneNumber: String = "",
    val sex        : String = ""
)

// ── Data Class: ร้านค้า ที่มี field "User" เป็น Map ──
data class Stores(
    val storeID    : String       = "",
    val storeName  : String       = "",
    val location   : String       = "",
    val description: String       = "",
    val createAt   : Timestamp? = null,
    val user       : UserEmbedded = UserEmbedded()
)

// ── UI State สำหรับ Store ──
sealed class StoreState {
    object Idle    : StoreState()
    object Loading : StoreState()
    object Success : StoreState()
    data class Error(val message: String) : StoreState()
}

// ── DataSource ──
class FirebaseManagementStore {
    private val storeCollection = Firebase.firestore.collection("Stores")
    private val userCollection  = Firebase.firestore.collection("Users")

    // ── Insert Store พร้อม nested User map ──
    suspend fun insert(store: Stores): Result<Unit> {
        return try {
            storeCollection.document(store.storeID).set(store).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Update Store ──
    suspend fun update(store: Stores): Result<Unit> {
        return try {
            storeCollection.document(store.storeID).update(
                mapOf(
                    "storeName"   to store.storeName,
                    "location"    to store.location,
                    "description" to store.description
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Delete Store ──
    suspend fun delete(storeID: String): Result<Unit> {
        return try {
            storeCollection.document(storeID).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── ดึง userID จาก email ──
    suspend fun getUserIdByEmail(email: String): String? {
        return try {
            val snapshot = userCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.getString("userID")
        } catch (e: Exception) {
            null
        }
    }

    // ── ดึงข้อมูล User เต็ม จาก email ──
    suspend fun getUserByEmail(email: String): Users? {
        return try {
            val snapshot = userCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()
            snapshot.toObjects(Users::class.java).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    // ── อัปเดต role ของ User เป็น store_owner ──
    suspend fun updateUserRole(userID: String, role: String): Result<Unit> {
        return try {
            val snapshot = userCollection
                .whereEqualTo("userID", userID)
                .limit(1)
                .get()
                .await()
            val docID = snapshot.documents.firstOrNull()?.id
                ?: return Result.failure(Exception("ไม่พบ User"))
            userCollection.document(docID)
                .update("role", role)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── ดึงร้านค้าตาม userID แบบ Real-time ──
    fun getStoreByUser(userId: String): Flow<List<Stores>> = callbackFlow {
        val listener = storeCollection
            .whereEqualTo("user.userID", userId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    val stores = snapshot.toObjects(Stores::class.java)
                    trySend(stores)
                }
            }
        awaitClose { listener.remove() }
    }

    // ── ดึงร้านค้าทั้งหมด ──
    suspend fun getAllStores(): List<Stores> {
        return try {
            val snapshot = storeCollection.get().await()
            snapshot.toObjects(Stores::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── [NEW] อัปเดต embedded user ใน Store document ──
    suspend fun updateEmbeddedUser(userID: String, updatedUser: UserEmbedded): Result<Unit> {
        return try {
            // ค้นหา Store ที่มี user.userID ตรงกัน
            val snapshot = storeCollection
                .whereEqualTo("user.userID", userID)
                .get()
                .await()

            if (snapshot.isEmpty) {
                // ไม่มีร้านค้า → ไม่ต้อง sync ก็ได้ (ถือว่าสำเร็จ)
                return Result.success(Unit)
            }

            // Batch update ทุก store ที่ user นี้เป็นเจ้าของ (ปกติมีแค่ 1 ร้าน)
            val batch = Firebase.firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.update(
                    doc.reference,
                    mapOf(
                        "user.name"        to updatedUser.name,
                        "user.username"    to updatedUser.username,
                        "user.phoneNumber" to updatedUser.phoneNumber,
                        "user.sex"         to updatedUser.sex
                    )
                )
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ── Repository ──
class StoreRepository(
    private val dataSource: FirebaseManagementStore = FirebaseManagementStore()
) {
    fun getStore(userId: String)                    = dataSource.getStoreByUser(userId)
    suspend fun getUserByEmail(email: String)       = dataSource.getUserByEmail(email)
    suspend fun getUserIdByEmail(email: String)     = dataSource.getUserIdByEmail(email)
    suspend fun getAllStores()                       = dataSource.getAllStores()
    suspend fun insert(store: Stores)               = dataSource.insert(store)
    suspend fun update(store: Stores)               = dataSource.update(store)
    suspend fun delete(storeID: String)             = dataSource.delete(storeID)
    suspend fun updateUserRole(userID: String, role: String) = dataSource.updateUserRole(userID, role)
    // ── [NEW] ──
    suspend fun updateEmbeddedUser(userID: String, updatedUser: UserEmbedded) =
        dataSource.updateEmbeddedUser(userID, updatedUser)
}

// ── ViewModel ──
class StoreViewModel(
    private val repository: StoreRepository = StoreRepository()
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = StoreViewModel() as T
        }
    }

    private val _currentStore = MutableStateFlow<List<Stores>>(emptyList())
    val currentStore = _currentStore.asStateFlow()

    private val _storeState = MutableStateFlow<StoreState>(StoreState.Idle)
    val storeState = _storeState.asStateFlow()

    fun resetStoreState() { _storeState.value = StoreState.Idle }

    // ── ดึงร้านค้าของ User จาก email ──
    fun fetchStoreByEmail(email: String) {
        viewModelScope.launch {
            try {
                val userId = repository.getUserIdByEmail(email)
                android.util.Log.d("StoreVM", "fetchStoreByEmail: email=$email, userId=$userId")

                if (userId != null) {
                    repository.getStore(userId).collect { stores ->
                        android.util.Log.d("StoreVM", "stores found: ${stores.size}")
                        _currentStore.value = stores
                    }
                } else {
                    android.util.Log.w("StoreVM", "userId is null for email=$email")
                    _currentStore.value = emptyList()
                }
            } catch (e: Exception) {
                android.util.Log.e("StoreVM", "fetchStoreByEmail error: ${e.message}", e)
                _currentStore.value = emptyList()
            }
        }
    }

    // ── ดึงร้านค้าทั้งหมด ──
    fun fetchAllStores() {
        viewModelScope.launch {
            _currentStore.value = repository.getAllStores()
        }
    }

    // ── สมัครร้านค้าใหม่ พร้อม embed User และอัปเดต role ──
    fun registerStore(
        email      : String,
        storeName  : String,
        location   : String,
        description: String
    ) {
        viewModelScope.launch {
            _storeState.value = StoreState.Loading
            try {
                val user = repository.getUserByEmail(email)
                    ?: run {
                        _storeState.value = StoreState.Error("ไม่พบข้อมูลผู้ใช้")
                        return@launch
                    }

                val storeID = com.google.firebase.Firebase.firestore
                    .collection("Stores").document().id

                val embeddedUser = UserEmbedded(
                    userID      = user.userID,
                    name        = user.name,
                    email       = user.email,
                    username    = user.username,
                    phoneNumber = user.phoneNumber,
                    sex         = user.sex
                )

                val newStore = Stores(
                    storeID     = storeID,
                    storeName   = storeName,
                    location    = location,
                    description = description,
                    user        = embeddedUser,
                    createAt    = Timestamp.now()
                )

                repository.insert(newStore)
                    .onFailure { e ->
                        _storeState.value = StoreState.Error(e.message ?: "บันทึกร้านไม่สำเร็จ")
                        return@launch
                    }

                repository.updateUserRole(user.userID, "store_owner")
                _storeState.value = StoreState.Success

            } catch (e: Exception) {
                _storeState.value = StoreState.Error(e.message ?: "เกิดข้อผิดพลาด")
            }
        }
    }

    // ── Update Store ──
    fun updateStore(store: Stores) {
        viewModelScope.launch { repository.update(store) }
    }

    // ── Delete Store ──
    fun deleteStore(storeID: String) {
        viewModelScope.launch { repository.delete(storeID) }
    }

    fun getMyStore(userId: String): Flow<List<Stores>> = repository.getStore(userId)

    // ── [NEW] sync embedded user ใน Store ──
    fun syncEmbeddedUser(userID: String, updatedUser: UserEmbedded) {
        viewModelScope.launch {
            repository.updateEmbeddedUser(userID, updatedUser)
                .onFailure { e ->
                    android.util.Log.e("StoreVM", "syncEmbeddedUser error: ${e.message}", e)
                }
        }
    }
}