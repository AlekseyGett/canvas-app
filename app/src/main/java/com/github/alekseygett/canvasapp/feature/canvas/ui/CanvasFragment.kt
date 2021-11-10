package com.github.alekseygett.canvasapp.feature.canvas.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.github.alekseygett.canvasapp.FileService
import com.github.alekseygett.canvasapp.R
import com.github.alekseygett.canvasapp.databinding.FragmentCanvasBinding
import com.github.alekseygett.canvasapp.feature.canvas.ui.views.ToolsLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

class CanvasFragment : Fragment() {

    companion object {
        private const val COLOR_TOOLBAR_INDEX = 0
        private const val SIZE_TOOLBAR_INDEX = 1

        fun getInstance() = CanvasFragment()
    }

    private val viewModel: CanvasViewModel by viewModel()

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            saveCanvas()
        } else {
            showErrorMessage(R.string.storage_permission_error)
        }
    }

    private var _binding: FragmentCanvasBinding? = null

    private val binding: FragmentCanvasBinding
        get() = _binding!!

    private val toolsLayouts: List<ToolsLayout> by lazy {
        listOf(
            requireActivity().findViewById(R.id.colorsToolbar),
            requireActivity().findViewById(R.id.sizesToolbar)
        )
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

        toolsLayouts[COLOR_TOOLBAR_INDEX].setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnColorClick(it))
        }

        toolsLayouts[SIZE_TOOLBAR_INDEX].setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnLineWeightClick(it))
        }

        binding.appBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.colors -> {
                    viewModel.processUiEvent(UiEvent.OnColorsButtonClick)
                }
                R.id.lineWeights -> {
                    viewModel.processUiEvent(UiEvent.OnLineWeightsButtonClick)
                }
                R.id.clear -> {
                    binding.drawView.clear()
                }
                R.id.save -> {
                    requestPermission {
                        saveCanvas()
                    }
                }
            }

            return@setOnMenuItemClickListener true
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
    }

    private fun render(viewState: ViewState) {
        toolsLayouts[COLOR_TOOLBAR_INDEX].let { paletteLayout ->
            paletteLayout.isGone = !viewState.isColorsToolbarVisible
            paletteLayout.render(viewState.colors)
        }

        toolsLayouts[SIZE_TOOLBAR_INDEX].let { strokeWidthLayout ->
            strokeWidthLayout.isGone = !viewState.isLineWeightsToolbarVisible
            strokeWidthLayout.render(viewState.lineWeights)
        }

        binding.drawView.render(viewState.canvasViewState)
    }

    private fun requestPermission(proceed: () -> Unit) {
        val permissionState = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            proceed()
        } else {
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun saveCanvas() {
        getCanvasBitmap()?.let { bitmap ->
            saveBitmapToFile(bitmap)
        }
    }

    private fun getCanvasBitmap(): Bitmap? = binding.drawView.getBitmap()

    private fun saveBitmapToFile(bitmap: Bitmap) {
        val data = bitmapToBytes(bitmap)
        val context = requireContext()

        Intent(context, FileService::class.java).let { intent ->
            intent.putExtra(FileService.BITMAP_KEY, data)
            context.startService(intent)
        }
    }

    private fun bitmapToBytes(bitmap: Bitmap): ByteArray =
        ByteArrayOutputStream().let { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }

    private fun showErrorMessage(@StringRes stringRes: Int) {
        val errorMessage = resources.getString(stringRes)
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

}