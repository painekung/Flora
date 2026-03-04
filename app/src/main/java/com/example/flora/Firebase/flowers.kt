package com.example.flora.Firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class Flowers(
    val flowerID: String = "",
    val category: String = "",
    val desc: String = "",
    val image: String = "",
    val name: String = "",
    val price: Int,
    val storeID: String,
    val color: String = ""

)

class FirebaseMangementFlower{
    private val collection = Firebase.firestore.collection("Flowers")

    suspend fun getAllFlowers(): List<Flowers>{
        val snapshot = collection.get().await()
        return snapshot.toObjects(Flowers::class.java)
    }
}


class FlowersRepository(
    private val  dataSource: FirebaseMangementFlower = FirebaseMangementFlower()
){
    suspend fun getAllFlowers() = dataSource.getAllFlowers()
}


class  FlowersViewModel(
    private val repository: FlowersRepository = FlowersRepository()
){
    init {
        android.util.Log.d("FlowersViewModel", "ViewModel created successfully")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FlowersViewModel() as T
            }
        }
    }





}