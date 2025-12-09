package com.example.wellfit.ui.ejercicio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.local.entities.EjercicioEntity

class EjercicioAdapter(
    private val onIniciarClick: (EjercicioEntity) -> Unit
) : RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder>() {

    private val items = mutableListOf<EjercicioEntity>()

    fun submitList(nuevaLista: List<EjercicioEntity>) {
        items.clear()
        items.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ejercicio_recomendado, parent, false)
        return EjercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class EjercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val icono: ImageView = itemView.findViewById(R.id.imgIconoEjercicio)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreEjercicio)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionEjercicio)
        private val btnIniciar: Button = itemView.findViewById(R.id.btnIniciarEjercicio)

        fun bind(item: EjercicioEntity) {
            tvNombre.text = item.nombreEjercicio
            tvDescripcion.text = item.descripcionEjercicio ?: "Ejercicio recomendado"

            // Si quieres cambiar icono según dificultad, aquí sería.
            // Por ahora solo dejamos el mismo.

            btnIniciar.setOnClickListener {
                onIniciarClick(item)
            }
        }
    }
}
