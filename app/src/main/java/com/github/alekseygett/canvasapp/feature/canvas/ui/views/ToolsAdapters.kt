package com.github.alekseygett.canvasapp.feature.canvas.ui.views

import android.graphics.PorterDuff
import com.github.alekseygett.canvasapp.base.Item
import com.github.alekseygett.canvasapp.databinding.ItemColorBinding
import com.github.alekseygett.canvasapp.databinding.ItemLineWeightBinding
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun colorsAdapterDelegate(onClick: (Int) -> Unit) : AdapterDelegate<List<Item>> =
    adapterDelegateViewBinding<ToolsItem.ColorModel, Item, ItemColorBinding>(
        { layoutInflater, parent -> ItemColorBinding.inflate(layoutInflater, parent, false) }
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

fun lineWeightsAdapterDelegate(onClick: (Int) -> Unit) : AdapterDelegate<List<Item>> =
    adapterDelegateViewBinding<ToolsItem.LineWeightModel, Item, ItemLineWeightBinding>(
        { layoutInflater, parent -> ItemLineWeightBinding.inflate(layoutInflater, parent, false) }
    ) {
        bind {
            val size = context.resources.getDimension(item.dimenRes).toInt()
            binding.lineWeightItem.layoutParams.width = size
            binding.lineWeightItem.layoutParams.height = size

            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }
    }