package com.example.wellfit.ui.hidratacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import java.text.SimpleDateFormat
import java.util.*

// ESTA ES LA ÚNICA DEFINICIÓN DE AguaItem (Resuelve Redeclaration: data class AguaItem : Any)
data class AguaItem(val cantidad: String, val timestamp: Long)

class HidratacionAdapter(
    private val items: MutableList<AguaItem>
) : RecyclerView.Adapter<HidratacionAdapter.AguaViewHolder>() {

    class AguaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCantidad: TextView = view.findViewById(R.id.itemCantidad)
        val tvFecha: TextView = view.findViewById(R.id.itemFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AguaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agua_registro, parent, false)
        return AguaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AguaViewHolder, position: Int) {
        val item = items[position]

        val sdf = SimpleDateFormat("HH:mm 'hrs'", Locale.getDefault())
        val horaFormateada = sdf.format(Date(item.timestamp))

        holder.tvCantidad.text = item.cantidad
        holder.tvFecha.text = horaFormateada
    }

    override fun getItemCount(): Int = items.size

    fun agregarItem(nuevo: AguaItem) {
        items.add(0, nuevo)
        notifyItemInserted(0)
    }

    fun eliminarUltimo() {
        if (items.isNotEmpty()) {
            items.removeAt(0)
            notifyItemRemoved(0)
        }
    }

    // Función para resolver Unresolved reference 'actualizarLista'
    fun actualizarLista(nuevosItems: List<AguaItem>) {
        items.clear()
        items.addAll(nuevosItems)
        notifyDataSetChanged()
    }
}