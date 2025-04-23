package com.gomez.herlin.mi_tiendita_virtual.Modelos

class ModeloCategoria {
    var id : String = ""
    var categoria : String = ""
    var img : String = ""

    constructor() {}

    constructor(id: String, categoria: String) {
        this.id = id
        this.categoria = categoria
        this.img = img
    }
}