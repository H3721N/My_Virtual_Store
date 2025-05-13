package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemProductoBinding
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Productos.AgregarProductoActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProducto : RecyclerView.Adapter<AdaptadorProducto.HolderProducto> {

    private lateinit var binding : ItemProductoBinding

    private var mContext : Context
    private var productosArrayList : ArrayList<ModeloProducto>

    constructor(mContext: Context, productosArrayList: ArrayList<ModeloProducto>) {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderProducto(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val modeloProducto = productosArrayList[position]

        val nombre = modeloProducto.nombre

        cargarPrimeraImg(modeloProducto, holder)

        visualizarDescuento(modeloProducto, holder)

        holder.item_nombre_p.text = "${nombre}"

        holder.Ib_editar.setOnClickListener {
            val intent = Intent(mContext, AgregarProductoActivity::class.java)
            intent.putExtra("Edicion", true)
            intent.putExtra("idProducto", modeloProducto.id)
            mContext.startActivity(intent)
        }

        holder.Ib_eliminar.setOnClickListener {
            val mAlertDialog = MaterialAlertDialogBuilder(mContext)
            mAlertDialog.setTitle(R.string.delete_p)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.delete) { dialog, which ->
                    eliminarProducto(modeloProducto)
                }
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
        }

    }

    private fun eliminarProducto(modeloProducto: ModeloProducto) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(modeloProducto.id)
            .removeValue()
            .addOnSuccessListener {
                val intent = Intent(mContext, AgregarProductoActivity::class.java)
                mContext.startActivity(intent)
                Toast.makeText(mContext, "${R.string.delete_p}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun visualizarDescuento(modeloProducto: ModeloProducto, holder: AdaptadorProducto.HolderProducto) {
        try {
            if (!modeloProducto.precioDesc.equals("0.0") && !modeloProducto.notaDesc.equals("")) {
                // Habilitar vista
                holder.item_nota_p.visibility = View.VISIBLE
                holder.item_precio_p_desc.visibility = View.VISIBLE

                // Convertir notaDesc a Double
                holder.item_precio_p_desc.text = String.format("%.2f USD", modeloProducto.precioDesc.toDouble())
                holder.item_precio_p.text = String.format("%.2f USD", modeloProducto.precio.toDouble())
                // Tacha el precio original
                holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                // Sin descuento
                holder.item_nota_p.visibility = View.GONE
                holder.item_precio_p_desc.visibility = View.GONE

                holder.item_precio_p.text = String.format("%.2f USD", modeloProducto.precio.toDouble())
                holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } catch (e: NumberFormatException) {
            Log.e("AdaptadorProducto", "Error al convertir notaDesc a Double: ${modeloProducto.nombre}", e)
        }
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdaptadorProducto.HolderProducto) {
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

    inner class HolderProducto (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imagenP = binding.imagenP
        var item_nombre_p = binding.itemNombreP
        var item_precio_p = binding.itemPrecioP
        var item_precio_p_desc = binding.itemPrecioPDesc
        var item_nota_p = binding.itemNotaP
        var Ib_editar = binding.IbEditar
        var Ib_eliminar = binding.IbEliminar
    }


}