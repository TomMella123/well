package com.example.wellfit.ui.hidratacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.local.entities.AguaRegistroEntity
import java.text.SimpleDateFormat
import java.util.*

class HidratacionAdapter(
    private var items: List<AguaRegistroEntity>
) : RecyclerView.Adapter<HidratacionAdapter.AguaViewHolder>() {

    inner class AguaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fecha: TextView = view.findViewById(R.id.itemFecha)
        val cantidad: TextView = view.findViewById(R.id.itemCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AguaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agua_registro, parent, false)
        return AguaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AguaViewHolder, position: Int) {
        val item = items[position]

        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val fechaFormateada = sdf.format(Date(item.timestamp))

        holder.fecha.text = fechaFormateada
        holder.cantidad.text = "${item.ml} ml"
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newList: List<AguaRegistroEntity>) {
        items = newList
        notifyDataSetChanged()
    }
}
