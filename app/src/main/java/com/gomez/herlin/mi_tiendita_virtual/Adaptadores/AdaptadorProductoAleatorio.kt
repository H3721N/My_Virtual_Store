package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.DetalleProducto.DetalleProductoActivity
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemProductoAleatorioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProductoAleatorio : RecyclerView.Adapter<AdaptadorProductoAleatorio.HolderProdutosAleatorios> {

    private lateinit var binding : ItemProductoAleatorioBinding

    private var mContext : Context
    var productosArrayList : List<ModeloProducto>

    constructor(mContext: Context, productosArrayList: List<ModeloProducto>) {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProdutosAleatorios {
        binding = ItemProductoAleatorioBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderProdutosAleatorios(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProdutosAleatorios, position: Int) {
        val modeloProducto = productosArrayList[position]

        val nombreP = modeloProducto.nombre

        cargarPrimeraImg(modeloProducto, holder)
        visualizarDescuento(modeloProducto, holder)

        holder.nombreP.text = "${nombreP}"

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetalleProductoActivity::class.java)
            intent.putExtra("idProducto", modeloProducto.id)
            mContext.startActivity(intent)
        }
    }
    private fun visualizarDescuento(modeloProducto: ModeloProducto,holder: AdaptadorProductoAleatorio.HolderProdutosAleatorios) {
        if(!modeloProducto.precioDesc.equals("0") && !modeloProducto.notaDesc.equals("")) {
            // Habilitar vista
            holder.notaDescP.visibility = View.VISIBLE
            holder.precioDescP.visibility = View.VISIBLE

            holder.notaDescP.text = "${modeloProducto.notaDesc}"
            holder.precioDescP.text = "${modeloProducto.precioDesc}${" USD"}"
            holder.precioP.text = "${modeloProducto.precio}${" USD"}"
            // tacha el precio original
            holder.precioP.paintFlags = holder.precioP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            // sin descuento
            holder.notaDescP.visibility = View.GONE
            holder.precioDescP.visibility = View.GONE

            holder.precioP.text = "${modeloProducto.precio}${" USD"}"
            holder.precioP.paintFlags = holder.precioP.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdaptadorProductoAleatorio.HolderProdutosAleatorios) {
        val idProducto = modeloProducto.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.item_img_producto)
                                .into(holder.imagenP)
                        } catch (e:Exception) {

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


    inner class HolderProdutosAleatorios(item: View) : RecyclerView.ViewHolder(item) {
        var imagenP = binding.imagenP
        var nombreP = binding.itemNombreP
        var precioP = binding.itemPrecioP
        var precioDescP = binding.itemPrecioPDesc
        var notaDescP = binding.itemNotaP
    }
}