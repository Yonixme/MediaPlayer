package com.example.fullproject.screens.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.fullproject.databinding.AddingSongInBdDataViewBinding

class AddingSongInDbDialog:  DialogFragment() {
    private lateinit var binding: AddingSongInBdDataViewBinding
    var onAddClickListener: ((String, String, String, Boolean) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddingSongInBdDataViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addMusicBtn.setOnClickListener {
            val name = binding.nameItem.text.toString()
            val author = binding.authorItem.text.toString()
            val uri = binding.uriItem.text.toString()
            val isChecked = binding.isAddToList.isChecked

            if (validateInput(name, author, uri)) {
                onAddClickListener?.invoke(name, author, uri, isChecked)
                dismiss()
            }
        }
    }

    private fun validateInput(name: String, author: String, uri: String): Boolean {
        // Додайте валідацію за потребою
        return uri.isNotBlank()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}