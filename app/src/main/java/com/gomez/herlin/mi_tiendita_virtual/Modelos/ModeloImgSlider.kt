package com.gomez.herlin.mi_tiendita_virtual.Modelos

class ModeloImgSlider {
    var id : String = ""
    var imagenUrl : String = ""
    var nombre : String = ""
    var descripcion : String = ""
    var precio : String = ""
    var precioDesc : String = ""
    var notaDesc : String = ""


    constructor()

    constructor(
        id: String,
        imagenUrl: String,
        nombre: String,
        descripcion: String,
        precio: String,
        precioDesc: String,
        notaDesc: String
    ) {
        this.id = id
        this.imagenUrl = imagenUrl
        this.nombre = nombre
        this.descripcion = descripcion
        this.precio = precio
        this.precioDesc = precioDesc
        this.notaDesc = notaDesc
    }
}