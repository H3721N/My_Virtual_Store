package com.gomez.herlin.mi_tiendita_virtual.Modelos

import android.net.Uri

class ModeloImagenSeleccionada {
    var id = ""
    var imageUri : Uri? = null
    var imgUrl : String? = null
    var deInternet = false

    constructor()

    constructor(id: String, imageUri: Uri?, imgUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imgUrl = imgUrl
        this.deInternet = deInternet
    }

}