package com.github.alekseygett.canvasapp.feature.canvas.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.alekseygett.canvasapp.R
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem
import com.github.alekseygett.canvasapp.utils.setAdapterAndCleanupOnDetachFromWindow
import com.github.alekseygett.canvasapp.utils.setData
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ToolsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr){

    private var onClick: (Int) -> Unit = {}

    private val adapterDelegate = ListDelegationAdapter(colorAdapterDelegate {
        onClick(it)
    })

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        findViewById<RecyclerView>(R.id.toolsRecyclerView).apply {
            setAdapterAndCleanupOnDetachFromWindow(adapterDelegate)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun render(toolsItems: List<ToolsItem>) {
        adapterDelegate.setData(toolsItems)
    }

    fun setOnClickListener(onClick: (Int) -> Unit) {
        this.onClick = onClick
    }

}
