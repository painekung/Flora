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

// ── 1. Data Class (ไม่มีฟิลด์ Password) ──
data class Users(
    val userID     : String = "",
    val name       : String = "",
    val email      : String = "",
    val username   : String = "",
    val phoneNumber: String = "",
    val role       : String = "customer",
    val sex        : String = ""
)

// ── 2. UI State ──
sealed class UserState {
    object Idle    : UserState()
    object Loading : UserState()
    object Success : UserState()
    data class Error(val message: String) : UserState()
}

// ── 3. DataSource ──
class FirebaseManagementUser {
    private val collection = Firebase.firestore.collection("Users")

    suspend fun getUserByEmail(email: String): Users? {
        return try {
            val snapshot = collection.whereEqualTo("email", email).limit(1).get().await()
            snapshot.toObjects(Users::class.java).firstOrNull()
        } catch (e: Exception) {
            Log.e("UserDB", "Error: ${e.message}")
            null
        }
    }

    suspend fun upsertProfile(user: Users): Result<Unit> {
        return try {
            val existing = collection.whereEqualTo("email", user.email).limit(1).get().await()

            if (existing.isEmpty) {
                val docRef = collection.document()
                val newUser = user.copy(userID = docRef.id)
                docRef.set(newUser).await()
            } else {
                val docID = existing.documents.first().id
                collection.document(docID).set(
                    mapOf(
                        "name"        to user.name,
                        "username"    to user.username,
                        "phoneNumber" to user.phoneNumber,
                        "sex"         to user.sex
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ── 4. Repository ──
class UserRepository(private val dataSource: FirebaseManagementUser = FirebaseManagementUser()) {
    suspend fun getUserByEmail(email: String) = dataSource.getUserByEmail(email)
    suspend fun upsertProfile(user: Users)    = dataSource.upsertProfile(user)
}

// ── 5. ViewModel ──
class UserViewModel(
    private val repo     : UserRepository  = UserRepository(),
    private val storeRepo: StoreRepository = StoreRepository()   // ← [NEW] inject StoreRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Users?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val userState = _userState.asStateFlow()

    fun loadUserByEmail(email: String) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            val user = repo.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
                _userState.value = UserState.Idle
            } else {
                _userState.value = UserState.Error("ไม่พบข้อมูลผู้ใช้")
            }
        }
    }

    fun saveProfile(
        email      : String,
        name       : String,
        username   : String,
        phoneNumber: String,
        sex        : String
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            val userToSave = (_currentUser.value ?: Users(email = email)).copy(
                email       = email,
                name        = name,
                username    = username,
                phoneNumber = phoneNumber,
                sex         = sex
            )

            // 1. บันทึกข้อมูล User ใน Users collection
            repo.upsertProfile(userToSave)
                .onSuccess {
                    _currentUser.value = userToSave
                    _userState.value = UserState.Success

                    // 2. [NEW] sync ข้อมูลที่อัปเดตเข้า Store.user (embedded) ด้วย
                    val userID = userToSave.userID
                    if (userID.isNotBlank()) {
                        val embeddedUser = UserEmbedded(
                            userID      = userID,
                            name        = name,
                            email       = email,
                            username    = username,
                            phoneNumber = phoneNumber,
                            sex         = sex
                        )
                        storeRepo.updateEmbeddedUser(userID, embeddedUser)
                            .onFailure { e ->
                                // log เท่านั้น ไม่ทำให้ save profile fail
                                Log.e("UserViewModel", "sync store embedded user failed: ${e.message}")
                            }
                    }
                }
                .onFailure { e ->
                    _userState.value = UserState.Error(e.message ?: "บันทึกไม่สำเร็จ")
                }
        }
    }

    fun resetUserState() { _userState.value = UserState.Idle }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = UserViewModel() as T
        }
    }
}