package com.github.alekseygett.canvasapp.feature.canvas.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.github.alekseygett.canvasapp.R
import com.github.alekseygett.canvasapp.databinding.FragmentCanvasBinding
import com.github.alekseygett.canvasapp.feature.canvas.ui.views.ToolsLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream

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
            saveImageToFile(bitmap)
        }
    }

    private fun getCanvasBitmap(): Bitmap? = binding.drawView.getBitmap()

    private fun showErrorMessage(@StringRes stringRes: Int) {
        val errorMessage = resources.getString(stringRes)
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun saveImageToFile(bitmap: Bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "masterpieces")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, requireContext().contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                requireContext().contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + "masterpieces")
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            val values = contentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            // .DATA is deprecated in API 29
            requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}