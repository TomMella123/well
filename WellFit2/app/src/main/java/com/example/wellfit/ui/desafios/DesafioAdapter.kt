package com.example.wellfit.ui.desafios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.DesafioRemoto

class DesafioAdapter(private var list: List<DesafioRemoto> = emptyList()) : RecyclerView.Adapter<DesafioAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val titulo: TextView = v.findViewById(R.id.tvTituloDesafio)
        val desc: TextView = v.findViewById(R.id.tvDescripcionDesafio)
        val puntos: TextView = v.findViewById(R.id.tvPuntosDesafio)
        val dif: TextView = v.findViewById(R.id.tvDificultadDesafio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_desafio, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = list[pos]
        h.titulo.text = item.nombreDesafio ?: "Desafío"
        h.desc.text = item.descripcionDesafio ?: "Sin descripción"
        h.puntos.text = "🏆 ${item.puntaje ?: 0} pts"
        h.dif.text = "Nivel: ${item.idDificultad ?: 1}"
    }

    override fun getItemCount() = list.size

    fun update(l: List<DesafioRemoto>) { list = l; notifyDataSetChanged() }
}