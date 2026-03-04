package com.example.flora.models

data class Flower(
    val id: Int,
    val name: String,
    val rating: Double,
    val price: Double,
    val image: Int?,
    val description: String = "ดอกทิวลิปสีชมพูเปรียบถึงความรักที่อ่อนหวานและอ่อนโยนเหมือนผู้หญิง เป็นความรักที่สดใส มีชีวิตชีวา ไม่จำเป็นต้องเสน่หา เป็นสัญลักษณ์แห่งความสุข รวมไปถึงการมองโลกในแง่ดี สามารถมอบให้ทั้งคนรัก ครอบครัว หรือเพื่อนฝูง",
    val color: String = "0xFFFFFFFF",
    val category: String = "ยอดนิยม",
    val storeId: String = "",  // ✅ เหลือแค่ storeId
    var count: Int = 1
)