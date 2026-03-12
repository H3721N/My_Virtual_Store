package com.gomez.herlin.mi_tiendita_virtual.cliente.Orden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityDetalleOrdenCactivityBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleOrdenCActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleOrdenCactivityBinding
    private var idOrden = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleOrdenCactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idOrden = intent.getStringExtra("idOrden") ?: ""

        datosOrden()
    }

    private fun datosOrden() {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val idOrden = "${snapshot.child("idOrden").value}"
                val costo = "${snapshot.child("costo").value}"
                val tiempoOrden = "${snapshot.child("tiempoOrden").value}"
                val estadoOrden = "${snapshot.child("estadoOrden").value}"

                val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())

                binding.idOrdenD.text = idOrden
                binding.fechaOrdenD.text = fecha
                binding.estadoOrdenD.text = estadoOrden
                binding.costoOrdenD.text = costo

                if (estadoOrden == this@DetalleOrdenCActivity.getString(R.string.estado_orden_recibida)) {
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.azul_marino))
                } else if (estadoOrden == this@DetalleOrdenCActivity.getString(R.string.estado_orden_preparacion)) {
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.naranja))
                } else if (estadoOrden == this@DetalleOrdenCActivity.getString(R.string.estado_orden_entegado)) {
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.verde_oscuro2))
                } else if (estadoOrden == this@DetalleOrdenCActivity.getString(R.string.estado_orden_cancelado)) {
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.rojo))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}