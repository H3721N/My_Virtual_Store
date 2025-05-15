package com.gomez.herlin.mi_tiendita_virtual

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

class Constantes {
    fun obtenerTiempoD() : Long {
        return System.currentTimeMillis()
    }

    fun agregarProductoFav (context : Context, idProducto : String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val tiempo = Constantes().obtenerTiempoD()

        val hashMap = HashMap<String, Any>()
        hashMap["idProducto"] = idProducto
        hashMap["idFav"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idProducto)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.fav), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun eliminarProductoFav (context : Context, idProducto : String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idProducto)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.no_fav), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun obtenerFecha(tiempo: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = tiempo

        return java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendar.time)
    }
}