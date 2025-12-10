package com.example.wellfit.ui.ejercicio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.EjercicioRemoto

class EjercicioAdapter(private var list: List<EjercicioRemoto> = emptyList()) : RecyclerView.Adapter<EjercicioAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val titulo: TextView = v.findViewById(R.id.tvNombreEjercicio)
        val desc: TextView = v.findViewById(R.id.tvDescripcionEjercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ejercicio_recomendado, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = list[pos]
        h.titulo.text = item.nombreEjercicio ?: "Ejercicio"
        h.desc.text = "${item.series ?: 0} series x ${item.repeticiones ?: 0} reps"
    }

    override fun getItemCount() = list.size

    fun update(l: List<EjercicioRemoto>) { list = l; notifyDataSetChanged() }
}