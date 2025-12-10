package com.example.wellfit.ui.recetas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellfit.R
import com.example.wellfit.data.remote.RecetaRemota

class RecetasAdapter(
    private var items: List<RecetaRemota>,
    private val clickListener: (RecetaRemota) -> Unit, // Para abrir el detalle
    private val toggleFavoriteListener: (Long) -> Unit, // Para marcar/desmarcar
    private val isFavoriteChecker: (Long) -> Boolean // Para pintar el icono
) : RecyclerView.Adapter<RecetasAdapter.RecetaViewHolder>() {

    class RecetaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgReceta: ImageView = view.findViewById(R.id.imgReceta)
        val tvNombreReceta: TextView = view.findViewById(R.id.tvNombreReceta)
        val tvDatosNutricionales: TextView = view.findViewById(R.id.tvDatosNutricionales)
        // La línea del botón de favorito (R.id.btnFavorite) sigue comentada para evitar el crash.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta_card, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = items[position]

        // 1. Mostrar el nombre real de la receta.
        holder.tvNombreReceta.text = receta.nombreReceta ?: "Receta Desconocida"

        // 2. Mostrar la dificultad o descripción.
        val dificultadTexto = when (receta.idDificultad) {
            1 -> "Fácil"
            2 -> "Intermedio"
            3 -> "Avanzado"
            else -> "Desconocida"
        }

        // Muestra la dificultad real, sobreescribiendo el placeholder "0 Kcal..."
        holder.tvDatosNutricionales.text = "Dificultad: $dificultadTexto"

        // Cargar imagen:
        val imageUrl = receta.recetaImageId
        if (!imageUrl.isNullOrEmpty()) {
            // Aquí debería estar la implementación de Glide/Picasso.
        } else {
            // Mostrar un placeholder si no hay URL de imagen
            holder.imgReceta.setImageResource(R.drawable.ic_recetas)
        }


        // Click Listener para abrir el detalle
        holder.itemView.setOnClickListener { clickListener(receta) }

        // La gestión del favorito está comentada para evitar el error.
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<RecetaRemota>) {
        items = newList
        notifyDataSetChanged()
    }
}