package com.example.wellfit.ui.desafios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.DesafioRemoto

class DesafioAdapter :
    ListAdapter<DesafioRemoto, DesafioAdapter.DesafioViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<DesafioRemoto>() {
        override fun areItemsTheSame(oldItem: DesafioRemoto, newItem: DesafioRemoto) =
            oldItem.idDesafio == newItem.idDesafio

        override fun areContentsTheSame(oldItem: DesafioRemoto, newItem: DesafioRemoto) =
            oldItem == newItem
    }

    inner class DesafioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.imgIconoDesafio)
        private val titulo: TextView = view.findViewById(R.id.tvTituloDesafio)
        private val descripcion: TextView = view.findViewById(R.id.tvDescripcionDesafio)
        private val puntos: TextView = view.findViewById(R.id.tvPuntosDesafio)
        private val dificultad: TextView = view.findViewById(R.id.tvDificultadDesafio)

        fun bind(item: DesafioRemoto) {
            // Icono fijo tipo "fuerza"
            icon.setImageResource(R.drawable.ic_trofeo)

            titulo.text = item.nombreDesafio ?: "Desafío"
            descripcion.text = item.descripcionDesafio ?: "Sin descripción"

            val pts = item.puntaje ?: 0
            puntos.text = "$pts puntos"

            dificultad.text = when (item.idDificultad ?: 0) {
                1 -> "Básico"
                2 -> "Intermedio"
                3 -> "Avanzado"
                else -> "Avanzado"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesafioViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_desafio, parent, false)
        return DesafioViewHolder(v)
    }

    override fun onBindViewHolder(holder: DesafioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
