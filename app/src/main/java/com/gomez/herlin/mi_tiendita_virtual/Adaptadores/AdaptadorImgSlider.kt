package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloImgSlider
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemImagenSliderBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class AdaptadorImgSlider : RecyclerView.Adapter<AdaptadorImgSlider.HolderImagenSlider> {
    private lateinit var binding : ItemImagenSliderBinding
    private var context : Context
    private var imagenArrayList : ArrayList<ModeloImgSlider>

    constructor(context: Context, imagenArrayList: ArrayList<ModeloImgSlider>) {
        this.context = context
        this.imagenArrayList = imagenArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSlider {
        binding = ItemImagenSliderBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagenSlider(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSlider, position: Int) {
        val modeloImagenSlider = imagenArrayList[position]

        val imagenUrl = modeloImagenSlider.imagenUrl
        val imagenContador = "${position + 1}/${imagenArrayList.size}"
        holder.imagenContadorTV.text = imagenContador

        try {
            Glide.with(context)
                .load(imagenUrl)
                .placeholder(R.drawable.item_img_producto)
                .into(holder.imagenSIV)
        } catch (e: Exception) {
            //holder.imagenSIV.setImageResource(R.drawable.item_img_producto)
        }

        holder.itemView.setOnClickListener{
            zoomImg(imagenUrl)
        }
    }

    inner class HolderImagenSlider (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imagenSIV : ShapeableImageView = binding.imagenSIV
        var imagenContadorTV : TextView = binding.imagenContadorTv


    }

    private fun zoomImg (imagen : String) {
        val pv : PhotoView
        val btnCerrar : MaterialButton
        val dialog = Dialog(context)

        dialog.setContentView(R.layout.zoom_imagen)

        pv = dialog.findViewById(R.id.zoomimg)
        btnCerrar = dialog.findViewById(R.id.cerrarZoom)

        try {
            Glide.with(context)
                .load(imagen)
                .placeholder(R.drawable.item_img_producto)
                .into(pv)
        } catch (e: Exception) {

        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }


}