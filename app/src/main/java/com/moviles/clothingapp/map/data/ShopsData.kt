package com.moviles.clothingapp.map.data

import com.google.android.gms.maps.model.LatLng




data class Shop(val name: String, val location: LatLng, val address: String)
object ShopsData{
    val shopLocations = listOf(
        Shop("E-Social", LatLng(4.653340400226082, -74.06102567572646), "Cra. 11 #67-46, Bogotá"),
        Shop("Planeta Vintage", LatLng(4.623326334617368, -74.06886427667965), "Cra. 13a #34-57, Bogotá"),
        Shop("El Segundazo", LatLng(4.674183693422512, -74.05288283864145), "Cl. 90 #14-45, Chapinero, Bogotá"),
        Shop("El Baulito de Mr.Bean", LatLng(4.653041858350189, -74.06386070551471), "Av. Caracas #65a-66, Bogotá"),
        Shop("Closet Up", LatLng(4.654346890637457, -74.06057896133835), "Cra. 11 #69-26, Bogotá"),
        Shop("El Cuchitril", LatLng(4.693857895240825, -74.03082026318502), "Cl. 117 #5A-13, Bogotá"),
        Shop("Herbario Vintage", LatLng(4.624205328397987, -74.07014419017378), "Cra 15 #35-12, Bogotá")
    )
}
