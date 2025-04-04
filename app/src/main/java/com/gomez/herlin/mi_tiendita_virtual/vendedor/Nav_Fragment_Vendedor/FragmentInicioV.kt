package com.gomez.herlin.mi_tiendita_virtual.vendedor.Nav_Fragment_Vendedor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gomez.herlin.mi_tiendita_virtual.R


class FragmentInicioV : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_inicio_v, container, false)
    }


}