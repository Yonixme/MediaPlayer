package com.example.fullproject.screens.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.fullproject.R
import com.example.fullproject.databinding.AddingDirInBdDataViewBinding

class AddingDirInDbDialog : DialogFragment() {
    private lateinit var binding: AddingDirInBdDataViewBinding

    private var savedName: String = ""
    private var savedUri: String = ""
    private var savedDisEnableForReading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddingDirInBdDataViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootPath = getString(R.string.root_path)

        if (savedInstanceState != null) {
            savedName = savedInstanceState.getString(KEY_NAME, "")
            savedUri = savedInstanceState.getString(KEY_URI, rootPath)
            savedDisEnableForReading = savedInstanceState.getBoolean(KEY_DIS_ENABLED_FOR_READING, false)
        } else {
            savedUri = rootPath
        }

        binding.nameItem.setText(savedName)
        binding.uriItem.setText(savedUri)
        binding.isAddToList.isChecked = savedDisEnableForReading

        binding.nameItem.addTextChangedListener { savedName = it.toString() }
        binding.uriItem.addTextChangedListener { savedUri = it.toString() }
        binding.isAddToList.setOnCheckedChangeListener { _, isChecked -> savedDisEnableForReading = isChecked }

        binding.addDirBtn.setOnClickListener {
            val result = Bundle().apply {
                putString(KEY_NAME, savedName)
                putString(KEY_URI, savedUri)
                putBoolean(KEY_DIS_ENABLED_FOR_READING, savedDisEnableForReading)
            }

            parentFragmentManager.setFragmentResult(KEY_ADD_DIRECTORY, result)
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, savedName)
        outState.putString(KEY_URI, savedUri)
        outState.putBoolean(KEY_DIS_ENABLED_FOR_READING, savedDisEnableForReading)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    companion object{
        const val KEY_NAME = "name"
        const val KEY_URI = "uri"
        const val KEY_DIS_ENABLED_FOR_READING = "disEnableForReading"
        const val KEY_ADD_DIRECTORY = "addDirKey"
    }
}
