package com.gomez.herlin.mi_tiendita_virtual.Adaptadores


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloCategoria
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemCategoriaVBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorCategoriaV : RecyclerView.Adapter<AdaptadorCategoriaV.HolderCategoriaV> {

    private lateinit var binding : ItemCategoriaVBinding

    private val mContext : Context
    private val categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(mContext: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.mContext = mContext
        this.categoriaArrayList = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaV {
        binding = ItemCategoriaVBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderCategoriaV(binding.root)

    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaV, position: Int) {
        val modelo = categoriaArrayList[position]

        val id = modelo.id
        val categoria = modelo.categoria

        holder.iten_nombre_c_v.text = categoria

        holder.item_eliminar_c_v.setOnClickListener {
            Toast.makeText(mContext, mContext.getString(R.string.item_delete_nombre_c_v), Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle(mContext.getString(R.string.item_delete_nombre_c_v))
            builder.setMessage(mContext.getString(R.string.item_delete_nombre_c_c))
                .setPositiveButton("Confirmar") { a,d ->
                    eliminarCategoria(id)
                }
                .setNegativeButton("Cancelar") { a,d ->
                    a.dismiss()
                }
            builder.show()
        }
    }

    private fun eliminarCategoria(modelo: ModeloCategoria, holder: AdaptadorCategoriaV.HolderCategoriaV) {
        val idCat = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child(idCat).removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext, mContext.getString(R.string.item_deleted_category), Toast.LENGTH_SHORT).show()
                eliminarImg(idCat)
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, "${mContext.getString(R.string.item_n_delete_nombre_c_v)} debido a: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun eliminarImg(idCat: String) {
        val nombreImg = idCat
        val rutaImagen = "Categorias/$nombreImg"
        val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
        storageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(mContext, mContext.getString(R.string.img_deleted_category), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


    inner class HolderCategoriaV (itemView: View) : RecyclerView.ViewHolder(itemView) {

        var iten_nombre_c_v = binding.itemNombreCV
        var item_eliminar_c_v = binding.itemEliminarC

    }


}