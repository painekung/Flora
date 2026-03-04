package com.example.flora.viewmodels

import androidx.lifecycle.ViewModel
import com.example.flora.models.Flower

class FlowerViewModel: ViewModel() {
    var selectedFlower: Flower? = null
}