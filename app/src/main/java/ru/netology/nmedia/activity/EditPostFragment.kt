package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class EditPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(layoutInflater, container, false)

        arguments?.textArg?.let{
            binding.editPost.setText(it)
        }
        val viewModel: PostViewModel by viewModels(ownerProducer = :: requireParentFragment)

        binding.editPost.requestFocus()
        binding.ok.setOnClickListener {
            val content = binding.editPost.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContent(content)
                viewModel.save()
            }
            findNavController().navigateUp()
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().navigateUp()

        }
        return binding.root
    }
}
