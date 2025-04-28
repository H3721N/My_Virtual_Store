package com.gomez.herlin.mi_tiendita_virtual.Modelos

class ModeloImgSlider {
    var id : String = ""
    var imgUrl : String = ""

    constructor()

    constructor(id: String, imgUrl: String) {
        this.id = id
        this.imgUrl = imgUrl
    }
}