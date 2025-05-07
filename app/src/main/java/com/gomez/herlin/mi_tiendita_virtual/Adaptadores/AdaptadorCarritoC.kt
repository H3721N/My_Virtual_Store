package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProductoCarrito
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemCarritoCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorCarritoC : RecyclerView.Adapter<AdaptadorCarritoC.HolderProductoCarrito> {

   private lateinit var binding : ItemCarritoCBinding
   private var mContext : Context
   var productosArrayList : ArrayList<ModeloProductoCarrito>
   private var firebaseAuth : FirebaseAuth

    constructor(
        mContext: Context,
        productoArrayList: ArrayList<ModeloProductoCarrito>
    ) : super() {
        this.mContext = mContext
        this.productosArrayList = productoArrayList
        this.firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductoCarrito {
        binding = ItemCarritoCBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderProductoCarrito(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    var costo : Double = 0.0

    override fun onBindViewHolder(holder: HolderProductoCarrito, position: Int) {
        val modeloProductoCarrito = productosArrayList[position]

        val nombre = modeloProductoCarrito.nombre
        var cantidad = modeloProductoCarrito.cantidad
        val precioFinal = modeloProductoCarrito.precioFinal
        val precio = modeloProductoCarrito.precio
        val precioDesc = modeloProductoCarrito.precioDesc

        holder.nombrePCar.text = nombre
        holder.cantidadPCar.text = cantidad.toString()

        cargarPrimeraImagen(modeloProductoCarrito, holder)
        Log.d("AdaptadorCarritoC", "Imagen cargada para el producto: ${modeloProductoCarrito.nombre}")

        visualizarDescuento(modeloProductoCarrito, holder)
        Log.d("AdaptadorCarritoC", "Descuento visualizado para el producto: ${modeloProductoCarrito.nombre}")

        holder.btnEliminar.setOnClickListener {
            eliminarProdCar(mContext, modeloProductoCarrito.idProducto)
        }

        var miPrecioFinalDouble = precioFinal.toDouble()

        holder.btnAumentar.setOnClickListener {
            if ( precioDesc != "0.0") {
                costo = precioDesc.toDouble()
            } else {
                costo = precio.toDouble()
            }

            miPrecioFinalDouble += costo
            cantidad++

            holder.precioFinalPCar.text = miPrecioFinalDouble.toString()
            holder.cantidadPCar.text = cantidad.toString()

            var precioFinalString = miPrecioFinalDouble.toString()

            calcularNuevoPrecio(mContext, modeloProductoCarrito.idProducto, precio, precioFinalString, cantidad)

        }

        holder.btnDisminuir.setOnClickListener {
            if ( cantidad > 1 ) {
                if ( !precioDesc.equals("0.0") ) {
                    costo = precioDesc.toDouble()
                } else {
                    costo = precio.toDouble()
                }

                miPrecioFinalDouble = miPrecioFinalDouble - costo
                cantidad--
                var precioFinalString = miPrecioFinalDouble.toString()
                calcularNuevoPrecio(mContext, modeloProductoCarrito.idProducto, precio, precioFinalString, cantidad)
            }
        }

    }

    private fun calcularNuevoPrecio(mContext: Context, idProducto: String, precio: String, precioFinalString: String, cantidad: Int) {
        val hashMap : HashMap<String, Any> = HashMap()

        hashMap["cantidad"] = cantidad
        hashMap["precioFinal"] = precioFinalString

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(idProducto)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, (R.string.put_cantidad), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, " ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun eliminarProdCar(mContext: Context, idProducto: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            .ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(idProducto)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, " ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun visualizarDescuento(modeloProductoCarrito: ModeloProductoCarrito, holder: AdaptadorCarritoC.HolderProductoCarrito) {
        if (!modeloProductoCarrito.precioDesc.equals("0.0")) {
            holder.precioFinalPCar.text = modeloProductoCarrito.precio
            holder.precioOriginalPCar.text = modeloProductoCarrito.precioDesc
            holder.precioOriginalPCar.paintFlags = holder.precioOriginalPCar.paintFlags or
                    android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.precioOriginalPCar.visibility = View.GONE
            holder.precioFinalPCar.text = modeloProductoCarrito.precioFinal.plus(" USD")
        }
    }


    private fun cargarPrimeraImagen(modeloProductoCarrito: ModeloProductoCarrito, holder: AdaptadorCarritoC.HolderProductoCarrito) {
        val idProducto = modeloProductoCarrito.idProducto

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes").limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"
                        val imagenUrlString = imagenUrl
                        try {
                            Glide.with(mContext)
                                .load(imagenUrlString)
                                .placeholder(R.drawable.item_img_producto)
                                .into(holder.imagenPCar)
                        } catch (e: Exception) {
                            Log.e("AdaptadorCarritoC", "Error loading image: ${e.message}")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    inner class HolderProductoCarrito(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagenPCar = binding.imagenPCar
        var nombrePCar = binding.nombrePCar
        var precioFinalPCar = binding.precioFinalPCar
        var precioOriginalPCar = binding.precioOriginalPCar
        var btnDisminuir = binding.btnDisminuir
        var cantidadPCar = binding.cantidadPCar
        var btnAumentar = binding.btnAumentar
        var btnEliminar = binding.btnEliminar
    }


}