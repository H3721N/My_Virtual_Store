package com.gomez.herlin.mi_tiendita_virtual.Modelos

class ModeloProductoCarrito {
    var idProducto:String = ""
    var nombre:String = ""
    var precio:String = "0.0"

    var precioFinal:String = "0.0"
    var precioDesc:String = "0.0"
    var cantidad:Long = 0

    constructor()

    constructor(
        idProducto: String,
        nombre: String,
        precio: String,
        precioFinal: String,
        precioDesc: String,
        cantidad: Long
    ) {
        this.idProducto = idProducto
        this.nombre = nombre
        this.precio = precio
        this.precioFinal = precioFinal
        this.precioDesc = precioDesc
        this.cantidad = cantidad
    }
}