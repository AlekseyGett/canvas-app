package com.github.alekseygett.canvasapp.feature.canvas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.github.alekseygett.canvasapp.R
import com.github.alekseygett.canvasapp.databinding.FragmentCanvasBinding
import com.github.alekseygett.canvasapp.feature.canvas.ui.views.ToolsLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class CanvasFragment : Fragment() {

    companion object {
        private const val PALETTE = 0

        fun getInstance() = CanvasFragment()
    }

    private val viewModel: CanvasViewModel by viewModel()

    private var _binding: FragmentCanvasBinding? = null

    private val binding: FragmentCanvasBinding
        get() = _binding!!

    private val toolsLayouts: List<ToolsLayout> by lazy {
        listOf(requireActivity().findViewById(R.id.palette))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanvasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolsLayouts[PALETTE].setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnColorClick(it))
        }

        binding.appBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.tools) {
                viewModel.processUiEvent(UiEvent.OnToolsClick)
            }

            return@setOnMenuItemClickListener true
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
    }

    private fun render(viewState: ViewState) {
        toolsLayouts[PALETTE].let { paletteLayout ->
            paletteLayout.isGone = !viewState.isPaletteVisible
            paletteLayout.render(viewState.colors)
        }

        binding.drawView.render(viewState.canvasViewState)
    }

}