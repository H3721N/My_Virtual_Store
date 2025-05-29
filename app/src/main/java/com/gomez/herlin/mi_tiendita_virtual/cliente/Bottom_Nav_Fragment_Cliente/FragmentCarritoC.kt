package com.gomez.herlin.mi_tiendita_virtual.cliente.Bottom_Nav_Fragment_Cliente

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorCarritoC
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProductoCarrito
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentCarritoCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentCarritoC : Fragment() {

    private lateinit var binding: FragmentCarritoCBinding
    private lateinit var mContext:  Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosArrayList: ArrayList<ModeloProductoCarrito>
    private lateinit var productoAdaptadorCarritoC: AdaptadorCarritoC

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentCarritoCBinding.inflate(inflater, container, false)
        binding.btnCrearOrden.setOnClickListener {
            if(productosArrayList.size ==0) {
                Toast.makeText(mContext, R.string.carrito_vacio, Toast.LENGTH_SHORT).show()
            } else {
                crearOrden()
            }
        }
        return binding.root
    }

    private fun crearOrden() {
        val tiempo = Constantes().obtenerTiempoD()
        val costo = binding.sumaProductos.text.toString().trim()
        val uid = firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["idOrden"] = "${keyId}"
        hashMap["tiempoorden"] = "${tiempo}"
        hashMap["estadoOrden"] = "Solicitud recibida"
        hashMap["costo"] = "${costo}"
        hashMap["ordenadoPor"] = "${uid}"

        ref.child(keyId!!).setValue(hashMap)
            .addOnSuccessListener {
                for (producto in productosArrayList) {
                    val idProducto = producto.idProducto
                    val nombre = producto.nombre
                    val precio = producto.precio
                    val precioFinal = producto.precioFinal
                    val precioDesc = producto.precioDesc
                    val cantidad = producto.cantidad

                    val hashMap2 = HashMap<String, Any>()
                    hashMap2["idProducto"] = idProducto
                    hashMap2["nombre"] = nombre
                    hashMap2["precio"] = precio
                    hashMap2["precioFinal"] = precioFinal
                    hashMap2["precioDesc"] = precioDesc
                    hashMap2["cantidad"] = cantidad

                    ref.child(keyId).child("Productos").child(idProducto).setValue(hashMap2)

                }
                Toast.makeText(mContext, R.string.order_created, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarCarrito()
        sumaProductos()

    }

    private fun sumaProductos() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var suma = 0.0
                    for (producto in snapshot.children) {
                        val precioFinal = producto.child("precioFinal").getValue(String::class.java)
                        if (precioFinal != null) {
                            suma += precioFinal.toDouble()
                        }

                        binding.sumaProductos.setText(suma.toString().plus(" USD"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun cargarCarrito() {
        productosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    productosArrayList.clear()
                    /*for (ds in snapshot.children) {
                        val modeloProductoCarrito = ds.getValue(ModeloProductoCarrito::class.java)


                        Log.d("FragmentCarritoC", "Producto Carrito: ${modeloProductoCarrito!!.nombre}")
                        productosArrayList.add(modeloProductoCarrito!!)


                    }*/
                    for (ds in snapshot.children) {
                        try {
                            val modeloProductoCarrito = ds.getValue(ModeloProductoCarrito::class.java)
                            Log.d("FragmentCarritoC", "Producto Carrito: ${modeloProductoCarrito?.nombre}")
                            productosArrayList.add(modeloProductoCarrito!!)

                        } catch (e: Exception) {
                            Log.e("FragmentCarritoC", "Error al convertir el producto: ${e.message}")
                            Log.e("FragmentCarritoC", "Datos crudos: ${ds.value}")
                        }
                    }

                    productoAdaptadorCarritoC = AdaptadorCarritoC(mContext, productosArrayList)
                    binding.carritoRv.adapter = productoAdaptadorCarritoC

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}