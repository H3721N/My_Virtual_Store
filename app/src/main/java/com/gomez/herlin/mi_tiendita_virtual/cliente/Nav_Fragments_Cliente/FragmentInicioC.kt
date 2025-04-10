package com.gomez.herlin.mi_tiendita_virtual.cliente.Nav_Fragments_Cliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.cliente.Bottom_Nav_Fragment_Cliente.FragmentMisOrdenes_C
import com.gomez.herlin.mi_tiendita_virtual.cliente.Bottom_Nav_Fragment_Cliente.FragmentTiendaC
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentInicioCBinding

class FragmentInicioC : Fragment() {


    private lateinit var binding: FragmentInicioCBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioCBinding.inflate(inflater, container, false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.op_mi_tienda_c -> {
                    replaceceFragment(FragmentTiendaC())
                }
                R.id.op_mis_ordenes_c -> {
                    replaceceFragment(FragmentMisOrdenes_C())
                }
            }
            true
        }

        replaceceFragment(FragmentTiendaC())
        binding.bottomNavigation.selectedItemId = R.id.op_mi_tienda_c

        return binding.root
    }

    private fun replaceceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }

}