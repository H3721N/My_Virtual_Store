package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloImagenSeleccionada
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityAgregarProductoBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemImagenesSeleccionadasBinding

class AdaptadorIMagenSeleccionada(
    private val context: Context,
    private val imagenesSelectArrayList: ArrayList<ModeloImagenSeleccionada>
) : Adapter<AdaptadorIMagenSeleccionada.HolderImageSeleccionada>() {
    private lateinit var binding: ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImageSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImageSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenesSelectArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImageSeleccionada, position: Int) {
        val modelo = imagenesSelectArrayList[position]
        val imagenUri = modelo.imagenUri

        if (modelo.deInternet) {
            try {
                val imagenUrl = modelo.imagenUrl
                Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.item_imagen)
                    .into(holder.imagenItem)
            } catch (e: Exception) {
            }
        } else {

            val imsgenUri = modelo.imagenUri
            // leyendo las imagenes

            try {
                Glide.with(context)
                    .load(imagenUri)
                    .placeholder(R.drawable.item_imagen)
                    .into(holder.imagenItem)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // eliminar la imagen seleccionada

        holder.borrar_item.setOnClickListener{
            imagenesSelectArrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    inner class HolderImageSeleccionada(itemView: View) : ViewHolder(itemView) {
        var imagenItem = binding.imagenItem
        var borrar_item = binding.borrarItem
    }

}