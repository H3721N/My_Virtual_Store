package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onBindViewHolder(holder: HolderProductoCarrito, position: Int) {
        val modeloProductoCarrito = productosArrayList[position]

        val nombre = modeloProductoCarrito.nombre
        val cantidad = modeloProductoCarrito.cantidad

        holder.nombrePCar.text = nombre
        holder.cantidadPCar.text = cantidad?.toString() ?:"1"

        cargarPrimeraImagen(modeloProductoCarrito, holder)
        Log.d("AdaptadorCarritoC", "Imagen cargada para el producto: ${modeloProductoCarrito.nombre}")

        visualizarDescuento(modeloProductoCarrito, holder)
        Log.d("AdaptadorCarritoC", "Descuento visualizado para el producto: ${modeloProductoCarrito.nombre}")

    }

    private fun visualizarDescuento(modeloProductoCarrito: ModeloProductoCarrito, holder: AdaptadorCarritoC.HolderProductoCarrito) {
        if (!modeloProductoCarrito.precioDesc.equals("0")) {
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
                        val imagenUrlString = imagenUrl?.toString() // Convert to String safely
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