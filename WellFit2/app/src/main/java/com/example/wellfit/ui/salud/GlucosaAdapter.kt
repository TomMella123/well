package com.example.wellfit.ui.salud

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R

// Modelo de datos simple (puedes ajustarlo a tu modelo real)
data class RegistroGlucosa(val valor: Int, val fecha: String)

class GlucosaAdapter(private val lista: MutableList<RegistroGlucosa>) :
    RecyclerView.Adapter<GlucosaAdapter.GlucosaViewHolder>() {

    class GlucosaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvValor: TextView = view.findViewById(R.id.tvValorItem)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlucosaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_glucosa_card, parent, false)
        return GlucosaViewHolder(view)
    }

    override fun onBindViewHolder(holder: GlucosaViewHolder, position: Int) {
        val item = lista[position]
        holder.tvValor.text = "${item.valor} mg/dl"
        holder.tvFecha.text = item.fecha
    }

    override fun getItemCount() = lista.size

    fun agregarRegistro(nuevo: RegistroGlucosa) {
        lista.add(0, nuevo) // Agrega al principio
        notifyItemInserted(0)
    }
}