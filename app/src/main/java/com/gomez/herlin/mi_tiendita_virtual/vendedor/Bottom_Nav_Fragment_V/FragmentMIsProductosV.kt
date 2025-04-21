package com.gomez.herlin.mi_tiendita_virtual.vendedor.Bottom_Nav_Fragment_V

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorProducto
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentMisProductosVBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemProductoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentMIsProductosV : Fragment() {

    private lateinit var binding: FragmentMisProductosVBinding
    private lateinit var mContext : Context

    private lateinit var productosArrayList: ArrayList<ModeloProducto>
    private lateinit var adaptadorProductos: AdaptadorProducto

    override fun onAttach (context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = FragmentMisProductosVBinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listarProductos()
    }

    private fun listarProductos() {
        productosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productosArrayList.clear()
                for (ds in snapshot.children) {
                    val modeloProducto = ds.getValue(ModeloProducto::class.java)
                    productosArrayList.add(modeloProducto!!)
                }
                adaptadorProductos = AdaptadorProducto(mContext, productosArrayList)
                binding.productosRV.adapter = adaptadorProductos
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}