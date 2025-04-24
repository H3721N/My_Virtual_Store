package com.gomez.herlin.mi_tiendita_virtual.Filtro

import android.widget.Filter
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorProductoC
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import java.util.Locale

class FiltroProducto (
    private val adaptador: AdaptadorProductoC,
    private val filtroLista : ArrayList<ModeloProducto>,
) : Filter(){
    override fun performFiltering(filtro: CharSequence?): FilterResults {
        var filtro = filtro
        var resultados = FilterResults()

        if (!filtro.isNullOrEmpty()) {
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroProducto = ArrayList<ModeloProducto>()

            for (i in filtroLista.indices) {
                if (filtroLista[i].nombre.uppercase(Locale.getDefault()).contains(filtro)) {
                    filtroProducto.add(filtroLista[i])
                }
            }
            resultados.count = filtroProducto.size
            resultados.values = filtroProducto
        } else {
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(filtro: CharSequence?, results: FilterResults) {
        adaptador.productosArrayList = results.values as ArrayList<ModeloProducto>
        adaptador.notifyDataSetChanged()
    }
}