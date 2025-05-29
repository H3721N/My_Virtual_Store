package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloOrdenCompra
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemOrdenCompraBinding

class AdaptadorOrdenCompra : RecyclerView.Adapter<AdaptadorOrdenCompra.HolderOrdenCompra> {
    private lateinit var  binding : ItemOrdenCompraBinding

    private var mContext : Context
    var ordenesArrayList : ArrayList<ModeloOrdenCompra>

    constructor(mContext: Context, ordenesArrayList: ArrayList<ModeloOrdenCompra>) {
        this.mContext = mContext
        this.ordenesArrayList = ordenesArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrdenCompra {
        binding = ItemOrdenCompraBinding.inflate(
            LayoutInflater.from(mContext),parent, false
        )
        return HolderOrdenCompra(binding.root)
    }

    override fun getItemCount(): Int {
        return ordenesArrayList.size
    }

    override fun onBindViewHolder(holder: HolderOrdenCompra, position: Int) {
        val ordenCompra = ordenesArrayList[position]

        val idOrden = ordenCompra.idOrden
        val tiempoOrden = ordenCompra.tiempoOrden
        val costo = ordenCompra.costo
        val estadoOrden = ordenCompra.estadoOrden

        holder.idOrdenItem.text = idOrden
        holder.costoOrdenItem.text = costo
        holder.estadoOrdenItem.text = estadoOrden

        if (ordenCompra.equals(R.string.estado_orden_recibida)) {
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.azul_marino))
        } else if (ordenCompra.equals(R.string.estado_orden_preparacion)) {
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.naranja))
        } else if (ordenCompra.equals(R.string.estado_orden_entegado)) {
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.verde_oscuro2))
        } else if (ordenCompra.equals(R.string.estado_orden_cancelado)) {
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.rojo))
        }

        val fecha = Constantes().obtenerFecha(safeToLong(tiempoOrden))
        binding.fechaOrdenItem.text = fecha
    }

    fun safeToLong(str: String?): Long {
        return str?.toLongOrNull() ?: 0L
    }

    inner class HolderOrdenCompra (itemView : View) : RecyclerView.ViewHolder(itemView) {

        var idOrdenItem = binding.idOrdenItem
        var fechaOrdenItem = binding.fechaOrdenItem
        var estadoOrdenItem = binding.estadoOrdenItem
        var costoOrdenItem = binding.costoOrdenItem
        var ibSiguiente = binding.ibSiguiente
    }

}