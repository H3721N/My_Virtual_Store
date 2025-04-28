package com.gomez.herlin.mi_tiendita_virtual.Modelos

class ModeloImgSlider {
    var id : String = ""
    var imagenUrl : String = ""

    constructor()

    constructor(id: String, imagenUrl: String) {
        this.id = id
        this.imagenUrl = imagenUrl
    }
}