package com.gomez.herlin.mi_tiendita_virtual.DetalleProducto

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorImgSlider
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloImgSlider
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityDetalleProductoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var idProducto = ""

    private lateinit var imagenSlider : ArrayList<ModeloImgSlider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Obtener el id del producto desde el adaptador

        idProducto = intent.getStringExtra("idProducto").toString()

        cargarImagenesProd()

        cargarInfoProd()
    }

    private fun cargarInfoProd() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val modeloProducto = snapshot.getValue(ModeloImgSlider::class.java)

                    val nombre = modeloProducto?.nombre
                    val descripcion = modeloProducto?.descripcion
                    val precio = modeloProducto?.precio
                    val precioDesc = modeloProducto?.precioDesc
                    val notaDesc = modeloProducto?.notaDesc

                    binding.nombrePD.text = nombre
                    binding.descripcionPD.text = descripcion
                    binding.precioPD.text = precio.plus(" USD")

                    if (!precioDesc.equals("") && !notaDesc.equals("")) {
                        // el producto tiene descuento
                        binding.precioDescPD.text = precioDesc.plus(" USD")
                        binding.notaDescPD.text = notaDesc

                        binding.precioDescPD.paintFlags = binding.precioDescPD.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        // el producto no tiene descuento
                        binding.precioDescPD.visibility = View.GONE
                        binding.notaDescPD.visibility = View.GONE

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarImagenesProd() {

        imagenSlider = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    imagenSlider.clear()
                    for (ds in snapshot.children) {
                        try {
                            val modeloImgSlider = ds.getValue(ModeloImgSlider::class.java)
                            imagenSlider.add(modeloImgSlider!!)
                        } catch (e: Exception) {
                            // hi

                        }
                    }

                    val adaptadorImgSlider = AdaptadorImgSlider(this@DetalleProductoActivity, imagenSlider)
                    binding.imagenVP.adapter = adaptadorImgSlider

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}