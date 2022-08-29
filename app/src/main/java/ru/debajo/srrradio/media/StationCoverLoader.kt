package ru.debajo.srrradio.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.debajo.srrradio.R

class StationCoverLoader(private val context: Context) {

    private val emptyBitmapMutable: MutableStateFlow<Bitmap> = MutableStateFlow(createEmptyBitmap(context))
    val emptyBitmap: StateFlow<Bitmap> = emptyBitmapMutable.asStateFlow()

    private var bgColor: Int = Color.parseColor("#6c586b")
    private var iconColor: Int = Color.parseColor("#907A88")

    fun setColors(@ColorInt bgColor: Int, @ColorInt iconColor: Int) {
        this.bgColor = bgColor
        this.iconColor = iconColor
        emptyBitmapMutable.value = createEmptyBitmap(context)
    }

    suspend fun loadImage(url: String?): Bitmap? {
        url ?: return null
        return suspendCancellableCoroutine {
            val task = Glide
                .with(context)
                .asBitmap()
                .load(url)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        it.resume(null, null)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        it.resume(resource, null)
                        return false
                    }

                })
                .submit()

            it.invokeOnCancellation { task.cancel(true) }
        }
    }

    private fun createEmptyBitmap(context: Context): Bitmap {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(bgColor)
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_radio)!!
        val drawableWidth = bitmap.width * 0.45f
        val drawableHeight = bitmap.height * 0.45f
        val drawableLeft = (bitmap.width - drawableWidth) / 2f
        val drawableTop = (bitmap.height - drawableHeight) / 2f
        drawable.setBounds(
            drawableLeft.roundToInt(),
            drawableTop.roundToInt(),
            (drawableLeft + drawableWidth).roundToInt(),
            (drawableTop + drawableHeight).roundToInt()
        )
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, iconColor)
        wrappedDrawable.draw(canvas)
        return bitmap
    }
}
