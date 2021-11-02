package com.github.alekseygett.canvasapp.feature.canvas.ui.views

import android.graphics.PorterDuff
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem
import com.github.alekseygett.canvasapp.base.Item
import com.github.alekseygett.canvasapp.databinding.ItemPaletteBinding
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun colorAdapterDelegate(onClick: (Int) -> Unit) : AdapterDelegate<List<Item>> =
    adapterDelegateViewBinding<ToolsItem.ColorModel, Item, ItemPaletteBinding>(
        { layoutInflater, parent -> ItemPaletteBinding.inflate(layoutInflater, parent, false) }
    ) {
        bind {
            binding.colorItem.setColorFilter(
                context.resources.getColor(item.colorRes, null),
                PorterDuff.Mode.SRC_IN
            )

            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }
    }
