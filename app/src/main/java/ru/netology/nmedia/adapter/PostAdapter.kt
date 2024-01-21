package ru.netology.nmedia.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.listener.OnInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.extensions.load
import ru.netology.nmedia.extensions.loadCircle
import ru.netology.nmedia.utils.FormatNumber.formatNumber

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}
class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            avatar.loadCircle("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            attachment.load("${BuildConfig.BASE_URL}/images/${post.attachment?.url}")
            attachment.isVisible = !post.attachment?.url.isNullOrBlank()
            author.text = post.author
            published.text = post.published
            postText.text = post.content
            views.text = formatNumber(post.views)
            share.text = formatNumber(post.shares)
            share.isChecked = post.sharedByMe
            like.text = formatNumber(post.likes)
            like.isChecked = post.likedByMe
            videoGroup.isVisible = !post.video.isNullOrEmpty()
            videoPreview.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }
            playVideo.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            root.setOnClickListener {
                onInteractionListener.onOpenPost(post)
            }
        }
    }
}
class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}