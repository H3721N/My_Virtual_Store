package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.Filtro.FiltroProducto
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemCategoriaCBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemProductoCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProductoC : RecyclerView.Adapter<AdaptadorProductoC.HolderProducto>, Filterable {

    private lateinit var binding : ItemProductoCBinding

    private var mContext : Context
    var productosArrayList : ArrayList<ModeloProducto>
    private var filtroLista : ArrayList<ModeloProducto>
    private var filtro : FiltroProducto? = null
    private var firebaseAuth : FirebaseAuth

    constructor(mContext: Context, productosArrayList: ArrayList<ModeloProducto>) {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
        this.filtroLista = productosArrayList
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoCBinding.inflate(LayoutInflater.from(mContext), parent, false)
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

        comprobarFavorito(modeloProducto, holder)

        holder.item_nombre_p.text = "${nombre}"

        // evento al precionar el boton de favorito

        holder.Ib_fav.setOnClickListener {
            val favorito = modeloProducto.favorito
            if (favorito) {
                // fav = true
                Constantes().eliminarProductoFav(mContext, modeloProducto.id)
            } else {
                // fav = false
                Constantes().agregarProductoFav(mContext, modeloProducto.id)
            }

        }


    }

    private fun comprobarFavorito(modeloProducto: ModeloProducto, holder: HolderProducto) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(modeloProducto.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorito = snapshot.exists()
                    modeloProducto.favorito = favorito

                    if (favorito) {
                        holder.Ib_fav.setImageResource(R.drawable.icon_favorite)
                        modeloProducto.favorito = true
                    } else {
                        // producto no en favoritos
                        holder.Ib_fav.setImageResource(R.drawable.icon_no_favorite)
                        modeloProducto.favorito = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun visualizarDescuento(modeloProducto: ModeloProducto,holder: AdaptadorProductoC.HolderProducto) {
        if(!modeloProducto.precioDesc.equals("0") && !modeloProducto.notaDesc.equals("")) {
            // Habilitar vista
            holder.item_nota_desc.visibility = View.VISIBLE
            holder.item_precio_p_desc.visibility = View.VISIBLE

            holder.item_nota_desc.text = "${modeloProducto.notaDesc}"
            holder.item_precio_p_desc.text = "${modeloProducto.precioDesc}${" USD"}"
            holder.item_precio_p.text = "${modeloProducto.precio}${" USD"}"
            // tacha el precio original
            holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            // sin descuento
            holder.item_nota_desc.visibility = View.GONE
            holder.item_precio_p_desc.visibility = View.GONE

            holder.item_precio_p.text = "${modeloProducto.precio}${" USD"}"
            holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdaptadorProductoC.HolderProducto) {
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
        var item_nota_desc = binding.itemNotaP
        var Ib_fav = binding.IbFav
    }

    override fun getFilter(): Filter {
        if (filtro == null) {
            filtro = FiltroProducto(this, filtroLista)
        }
        return filtro as FiltroProducto
    }


}
