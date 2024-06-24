package com.in2bliss.ui.activity.home.myAffirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.databinding.ItemMyRecordingsBinding
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import com.in2bliss.utils.extension.convertMilliseconds
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.visibility

class RecordingsAdapter(
    private val player: BackgroundMusicPlayerInterface?
) : PagingDataAdapter<SeeAllResponse.Data, RecordingsAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<SeeAllResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: SeeAllResponse.Data,
            newItem: SeeAllResponse.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SeeAllResponse.Data,
            newItem: SeeAllResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var selectedPosition = -1

    private var previousPosition = -1

    var listener: ((position: Int, affirmationId: Int, data: SeeAllResponse.Data?) -> Unit)? =
        null

    class ViewHolder(val view: ItemMyRecordingsBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemMyRecordingsBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            tvTitle.text = model?.title

            val date = if (model?.updatedAt != null) {
                model.updatedAt
            } else {
                model?.createdAt
            }
            tvDateAndTime.text = date?.let {
                formatDate(
                    date = it,
                    inputFormat = "yyyy-MM-dd HH:mm:ss",
                    isUtc = true,
                    outPutFormat = "EEEE, dd MMM hh:mm:aa"
                )
            }

            convertMilliseconds(if (model?.duration == null) 0L else model.duration.toLong() * 1000) { hour, minute, second ->
                val time = "${
                    if (hour > 0) "${String.format("%02d", hour)}:" else ""
                }${String.format("%02d", minute)}:${String.format("%02d", second)}"
                tvRecordEndTime.text = time
            }

            musicPlayerSlider.max =
                if (model?.duration == null) 0 else model.duration.toInt() * 1000

            if (selectedPosition == holder.absoluteAdapterPosition) {

                if (player?.musicPLayingState()?.value == false) {
                    player.addBackgroundMedia(
                        mediaUri = BuildConfig.AFFIRMATION_BASE_URL.plus(model?.audio.orEmpty())
                            .toUri(),
                        playWhenReady = true
                    )
                }

                player?.isPlayOrPause = { isPlaying ->
                    ivPlayAndPause.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            if (isPlaying) R.drawable.ic_music_pause else R.drawable.ic_music_play
                        )
                    )
                }

                player?.playerProgress = { mediaProgress ->
                    musicPlayerSlider.progress = mediaProgress.seekBarProgress.toInt()
                    tvRecordStartTime.text = mediaProgress.musicProgress
                }

                player?.playerBuffering = { isVisible ->
                    buffering.visibility(
                        isVisible = isVisible && selectedPosition == holder.absoluteAdapterPosition
                    )
                }

                musicPlayerSlider.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        if (p2) {
                            player?.changePlayerProgress(
                                progress = p1.toLong()
                            )
                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })

                ivForward10.setOnClickListener {
                    player?.changePlayerProgress(
                        progress = (musicPlayerSlider.progress + 10000).toLong()
                    )
                }

                ivBack10.setOnClickListener {
                    player?.changePlayerProgress(
                        progress = (musicPlayerSlider.progress - 10000).toLong()
                    )
                }

            } else {
                ivPlayAndPause.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        R.drawable.ic_music_play
                    )
                )
                buffering.visibility(
                    isVisible = false
                )
            }

            ivPlayAndPause.setOnClickListener {
                if (selectedPosition == holder.absoluteAdapterPosition) {
                    if ((player?.musicProgress()?.value?.seekBarProgress
                            ?: 0) >= (player?.musicEndTime()?.value?.seekBarProgress ?: 0)
                    ) {
                        player?.changePlayerProgress(
                            progress = 0
                        )
                    }

                    model?.isPlaying = (model?.isPlaying ?: false).not()
                    player?.playOrPausePlayer(model?.isPlaying ?: false)

                    return@setOnClickListener
                }

                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousPosition)
                previousPosition = selectedPosition
                player?.playOrPausePlayer(
                    isPlaying = false
                )
            }

            ivMenu.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition, model?.id ?: 0, model)
            }
        }
    }
}