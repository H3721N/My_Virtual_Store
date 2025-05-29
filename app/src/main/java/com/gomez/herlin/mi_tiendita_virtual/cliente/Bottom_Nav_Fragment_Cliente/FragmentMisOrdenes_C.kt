package com.gomez.herlin.mi_tiendita_virtual.cliente.Bottom_Nav_Fragment_Cliente

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorOrdenCompra
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloOrdenCompra
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentMisOrdenesCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentMisOrdenes_C : Fragment() {

    private lateinit var binding : FragmentMisOrdenesCBinding

    private lateinit var mContext: Context
    private lateinit var ordenesArrayList: ArrayList<ModeloOrdenCompra>
    private lateinit var ordenesAdaptador : AdaptadorOrdenCompra

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentMisOrdenesCBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated ( view: View, savedInstanceState: Bundle? ) {
        super.onViewCreated(view, savedInstanceState)
        verOrdenes()
    }

    private fun verOrdenes() {
        ordenesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        ref.orderByChild("ordenadoPor").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children ) {
                        val modelo = ds.getValue(ModeloOrdenCompra::class.java)
                        ordenesArrayList.add(modelo!!)
                    }
                    ordenesAdaptador = AdaptadorOrdenCompra(mContext, ordenesArrayList)
                    binding.ordenesRv.adapter = ordenesAdaptador
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}