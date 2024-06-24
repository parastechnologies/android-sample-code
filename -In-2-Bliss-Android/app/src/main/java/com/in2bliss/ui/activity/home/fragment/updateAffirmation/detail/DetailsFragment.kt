package com.in2bliss.ui.activity.home.fragment.updateAffirmation.detail

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.in2bliss.base.BaseFragment
import com.in2bliss.databinding.FragmentDetailsBinding
import com.in2bliss.utils.extension.visibility

class DetailsFragment : BaseFragment<FragmentDetailsBinding>(
    layoutInflater = FragmentDetailsBinding::inflate
) {

    private val viewModel: DetailsVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.data=viewModel
        onClick()

    }

    private fun onClick(){
        binding.edtTranscript.doAfterTextChanged { text ->
            viewModel.transcriptCount.set("${text?.length}/1000")
        }
        binding.etTranscriptTitle.doAfterTextChanged { text ->
            binding.ivTitleCancel.visibility((text?.length ?: "".length) > 0)
        }
        binding.ivTitleCancel.setOnClickListener {
            viewModel.title.set("")
        }
    }

}

