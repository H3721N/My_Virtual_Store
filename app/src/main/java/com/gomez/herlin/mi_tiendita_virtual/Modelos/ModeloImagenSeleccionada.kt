package com.gomez.herlin.mi_tiendita_virtual.Modelos

import android.net.Uri

class ModeloImagenSeleccionada {
    var id = ""
    var imagenUri : Uri? = null
    var imgUrl : String? = null
    var deInternet = false

    constructor()

    constructor(id: String, imagenUri: Uri?, imgUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imagenUri = imagenUri
        this.imgUrl = imgUrl
        this.deInternet = deInternet
    }

}