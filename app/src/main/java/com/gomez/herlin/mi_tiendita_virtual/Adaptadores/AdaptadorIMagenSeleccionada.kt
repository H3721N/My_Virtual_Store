package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloImagenSeleccionada
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityAgregarProductoBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemImagenesSeleccionadasBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.log

class AdaptadorIMagenSeleccionada(
    private val context: Context,
    private val imagenesSelectArrayList: ArrayList<ModeloImagenSeleccionada>,
    private val idProducto : String
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
            if (modelo.deInternet ) {
                eliminarImagenFirebase(modelo,  position)
            }
            imagenesSelectArrayList.remove(modelo)
            notifyDataSetChanged()
        }
    }

    private fun eliminarImagenFirebase(
        modelo: ModeloImagenSeleccionada,
        position: Int) {

        val idImagen = modelo.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes").child(idImagen)
            .removeValue()
            .addOnSuccessListener {
                try {
                    Log.d("AdaptadorIMagenSeleccionada", idProducto)
                    Log.d("IMagenSeleccionada", idImagen)
                    imagenesSelectArrayList.remove(modelo)
                    notifyItemRemoved(position)
                    eliminarImagenStorage(modelo)
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun eliminarImagenStorage(modelo: ModeloImagenSeleccionada) {
        val rutaImagen = "Productos/" + idProducto + "/Imagenes/"+ modelo.id

        //Log.d("ModeloCompleto", modelo)

        Log.d("URL", modelo.imagenUrl.toString())
        Log.d("URI", modelo.imagenUri.toString())
        Log.d("ID", modelo.id)
        Log.d("In", modelo.deInternet.toString())

        Log.d("AdaptadorIMagenSeleccionada", rutaImagen)

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.delete()
            .addOnSuccessListener {
                Toast.makeText(context, (R.string.delete_img), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderImageSeleccionada(itemView: View) : ViewHolder(itemView) {
        var imagenItem = binding.imagenItem
        var borrar_item = binding.borrarItem
    }

}