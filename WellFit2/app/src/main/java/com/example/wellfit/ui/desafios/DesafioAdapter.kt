package com.example.wellfit.ui.desafios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.DesafioRemoto

class DesafioAdapter(
    private var desafios: List<DesafioRemoto> = emptyList()
) : RecyclerView.Adapter<DesafioAdapter.DesafioViewHolder>() {

    class DesafioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // IDs corregidos según item_desafio.xml
        val tvNombre: TextView = view.findViewById(R.id.tvTituloDesafio)
        val tvDesc: TextView = view.findViewById(R.id.tvDescripcionDesafio)
        val tvPuntos: TextView = view.findViewById(R.id.tvPuntosDesafio)
        // Agregamos dificultad ya que existe en el XML
        val tvDificultad: TextView = view.findViewById(R.id.tvDificultadDesafio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesafioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_desafio, parent, false)
        return DesafioViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesafioViewHolder, position: Int) {
        val item = desafios[position]

        holder.tvNombre.text = item.nombreDesafio ?: "Desafío"
        holder.tvDesc.text = item.descripcionDesafio ?: "Sin descripción"
        holder.tvPuntos.text = "🏆 ${item.puntaje ?: 0} puntos"

        // Mapeo simple de ID dificultad a texto (ajusta según tu lógica)
        val dificultadTexto = when(item.idDificultad) {
            1 -> "Fácil"
            2 -> "Medio"
            3 -> "Avanzado"
            else -> "General"
        }
        holder.tvDificultad.text = dificultadTexto
    }

    override fun getItemCount() = desafios.size

    fun actualizarLista(nuevaLista: List<DesafioRemoto>) {
        desafios = nuevaLista
        notifyDataSetChanged()
    }
}