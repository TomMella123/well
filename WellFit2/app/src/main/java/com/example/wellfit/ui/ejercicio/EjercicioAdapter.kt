package com.example.wellfit.ui.ejercicio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.EjercicioRemoto

class EjercicioAdapter(
    private var lista: List<EjercicioRemoto> = emptyList()
) : RecyclerView.Adapter<EjercicioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreEjercicio)
        // CORRECCIÓN: Usamos el ID que sí existe en el XML
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionEjercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ejercicio_recomendado, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.nombreEjercicio ?: "Ejercicio"

        // Concatenamos la descripción o las series en el campo de descripción
        val detalles = if (item.series != null && item.repeticiones != null) {
            "${item.series} series x ${item.repeticiones} reps"
        } else {
            item.descripcionEjercicio ?: "Sin detalles"
        }
        holder.tvDescripcion.text = detalles
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<EjercicioRemoto>) {
        lista = newList
        notifyDataSetChanged()
    }
}