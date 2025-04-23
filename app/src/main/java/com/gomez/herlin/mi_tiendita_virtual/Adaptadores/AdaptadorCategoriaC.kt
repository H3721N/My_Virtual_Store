package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloCategoria
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemCategoriaCBinding

class AdaptadorCategoriaC : RecyclerView.Adapter<AdaptadorCategoriaC.HolderCategoriaC> {

    private lateinit var binding : ItemCategoriaCBinding

    private var mContext : Context
    private var categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(mContext: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.mContext = mContext
        this.categoriaArrayList = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaC {
        binding = ItemCategoriaCBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderCategoriaC(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaC, position: Int) {
        val modelo = categoriaArrayList[position]
        val categoria = modelo.categoria
        val imagen = modelo.img

        holder.item_nombre_categoria.text = categoria

        Glide.with(mContext)
            .load(imagen)
            .placeholder(R.drawable.categorias)
            .into(holder.item_img_cat)
    }

    inner class HolderCategoriaC ( itemView : View) : RecyclerView.ViewHolder(itemView) {
        var item_nombre_categoria = binding.itemNombreCC
        var item_img_cat = binding.imageCateg
        var item_ver_productos = binding.itemVerProductos
    }


}