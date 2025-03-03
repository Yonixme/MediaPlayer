//package com.example.fullproject.screens.components
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.WindowManager
//import androidx.fragment.app.DialogFragment
//import com.example.fullproject.databinding.AddingSongInBdDataViewBinding
//
//class AddingSongInDbDialog : DialogFragment() {
//    private lateinit var binding: AddingSongInBdDataViewBinding
//    var onAddClickListener: ((String, String, String, Boolean) -> Unit)? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = AddingSongInBdDataViewBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val initialUri = arguments?.getString(ARG_URI) ?: ""
//        binding.uriItem.setText(initialUri)
//
//        binding.addMusicBtn.setOnClickListener {
//            val name = binding.nameItem.text.toString()
//            val author = binding.authorItem.text.toString()
//            val uri = binding.uriItem.text.toString()
//            val disEnableAutoPlay = binding.isAddToList.isChecked
//
//            if (validateInput(uri)) {
//                onAddClickListener?.invoke(name, author, uri, disEnableAutoPlay)
//                dismiss()
//            }
//        }
//    }
//
//    private fun validateInput(uri: String): Boolean {
//        return uri.isNotBlank()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.WRAP_CONTENT
//        )
//    }
//
//    companion object {
//        private const val ARG_URI = "arg_uri"
//
//        fun newInstance(uri: String): AddingSongInDbDialog {
//            val fragment = AddingSongInDbDialog()
//            val args = Bundle()
//            args.putString(ARG_URI, uri)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//}
package com.example.fullproject.screens.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.fullproject.databinding.AddingSongInBdDataViewBinding

class AddingSongInDbDialog : DialogFragment() {
    private lateinit var binding: AddingSongInBdDataViewBinding

    private var savedName: String = ""
    private var savedAuthor: String = ""
    private var savedUri: String = ""
    private var savedDisEnableAutoPlay: Boolean = false

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

        // Відновлення стану або ініціалізація з аргументів
        if (savedInstanceState != null) {
            savedName = savedInstanceState.getString(KEY_NAME, "")
            savedAuthor = savedInstanceState.getString(KEY_AUTHOR, "")
            savedUri = savedInstanceState.getString(KEY_URI, arguments?.getString(ARG_URI) ?: "")
            savedDisEnableAutoPlay = savedInstanceState.getBoolean(KEY_DIS_ENABLE_AUTO_PLAY, false)
        } else {
            savedUri = arguments?.getString(ARG_URI) ?: ""
        }

        // Заповнення полів
        binding.nameItem.setText(savedName)
        binding.authorItem.setText(savedAuthor)
        binding.uriItem.setText(savedUri)
        binding.isAddToList.isChecked = savedDisEnableAutoPlay

        // Слухачі змін
        binding.nameItem.addTextChangedListener { savedName = it.toString() }
        binding.authorItem.addTextChangedListener { savedAuthor = it.toString() }
        binding.uriItem.addTextChangedListener { savedUri = it.toString() }
        binding.isAddToList.setOnCheckedChangeListener { _, isChecked ->
            savedDisEnableAutoPlay = isChecked
        }

        // Обробка кліку
        binding.addMusicBtn.setOnClickListener {
            if (validateInput(savedUri)) {
                val result = Bundle().apply {
                    putString(KEY_NAME, savedName)
                    putString(KEY_AUTHOR, savedAuthor)
                    putString(KEY_URI, savedUri)
                    putBoolean(KEY_DIS_ENABLE_AUTO_PLAY, savedDisEnableAutoPlay)
                }

                parentFragmentManager.setFragmentResult(KEY_ADD_SONG, result)
                dismiss()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putString(KEY_NAME, savedName)
            putString(KEY_AUTHOR, savedAuthor)
            putString(KEY_URI, savedUri)
            putBoolean(KEY_DIS_ENABLE_AUTO_PLAY, savedDisEnableAutoPlay)
        }
    }

    private fun validateInput(uri: String): Boolean {
        return uri.isNotBlank()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val KEY_NAME = "song_name"
        const val KEY_AUTHOR = "song_author"
        const val KEY_URI = "song_uri"
        const val KEY_DIS_ENABLE_AUTO_PLAY = "song_dis_enable_auto_play"
        const val KEY_ADD_SONG = "add_song_key"
        private const val ARG_URI = "arg_uri"

        fun newInstance(uri: String): AddingSongInDbDialog {
            return AddingSongInDbDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, uri)
                }
            }
        }
    }
}