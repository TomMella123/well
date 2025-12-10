package com.example.wellfit.ui.salud

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import java.text.SimpleDateFormat
import java.util.*

// ÚNICA DEFINICIÓN DE PresionItem (Resuelve Redeclaration en PresionActivity.kt)
data class PresionItem(val sistolica: Int, val diastolica: Int, val timestamp: Long)

class PresionAdapter(
    private val items: MutableList<PresionItem>
) : RecyclerView.Adapter<PresionAdapter.PresionViewHolder>() {

    class PresionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvValor: TextView = view.findViewById(R.id.tvValorPresion)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaPresion)
        val tvHora: TextView = view.findViewById(R.id.tvHoraPresion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_presion, parent, false)
        return PresionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresionViewHolder, position: Int) {
        val item = items[position]

        holder.tvValor.text = "${item.sistolica}/${item.diastolica}"

        val date = Date(item.timestamp)
        val formatFecha = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault())
        val formatHora = SimpleDateFormat("HH:mm 'hrs'", Locale.getDefault())

        holder.tvFecha.text = formatFecha.format(date)
        holder.tvHora.text = formatHora.format(date)
    }

    override fun getItemCount() = items.size

    fun agregarItem(nuevo: PresionItem) {
        items.add(0, nuevo)
        notifyItemInserted(0)
    }

    // Función para resolver Unresolved reference 'actualizarLista'
    fun actualizarLista(nuevosItems: List<PresionItem>) {
        items.clear()
        items.addAll(nuevosItems)
        notifyDataSetChanged()
    }
}