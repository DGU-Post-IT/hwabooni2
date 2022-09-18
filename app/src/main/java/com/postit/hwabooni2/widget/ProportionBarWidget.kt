package com.postit.hwabooni2.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ProportionBar(
    data: List<Number>,
    colors: List<Color>,
    strokeWidth: Float,
    cornerRadius: CornerRadius = CornerRadius(strokeWidth),
    animate: Boolean,
    modifier: Modifier
) {
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        tween(2000)
    )
    val sumOfData = remember(data) { data.map { it.toFloat() }.sum() }
    Canvas(
        modifier = modifier
    ) {
        val lineStart = size.width * 0.05f
        val lineEnd = size.width * 0.95f
        val lineLength = (lineEnd - lineStart) * animationProgress
        val lineHeightOffset = (size.height - strokeWidth) * 0.5f
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(
                        offset = Offset(lineStart, lineHeightOffset),
                        size = Size(lineLength, strokeWidth)
                    ),
                    cornerRadius
                )
            )
        }
        val dataAndColor = data.zip(colors)
        clipPath(
            path
        ) {
            var dataStart = lineStart
            dataAndColor.forEach { (number, color) ->
                val dataEnd =
                    dataStart + ((number.toFloat() / sumOfData) * lineLength)
                drawRect(
                    color = color,
                    topLeft = Offset(dataStart, lineHeightOffset),
                    size = Size(dataEnd - dataStart, strokeWidth)
                )
                dataStart = dataEnd
            }
        }

    }
}

@Composable
@Preview(widthDp = 300, heightDp = 200, showBackground = true)
fun ProportionBarPreview() {
    ProportionBar(
        data = listOf(1, 2, 0, 3),
        colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue),
        strokeWidth = 180f,
        animate = true,
        modifier = Modifier.fillMaxSize()
    )

}

class ProportionBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    var data by mutableStateOf<List<Number>>(listOf())
    var colors by mutableStateOf<List<Color>>(listOf())
    var strokeWidth by mutableStateOf<Float>(0f)
    var animate by mutableStateOf<Boolean>(false)
    var modifier by mutableStateOf<Modifier>(Modifier)

    @Composable
    override fun Content() {
        ProportionBar(
            data = data,
            colors = colors,
            strokeWidth = strokeWidth,
            animate = animate,
            modifier = modifier
        )
    }
}

@Preview(widthDp = 300, heightDp = 200, showBackground = true)
@Composable
fun PreviewTest() {
    val colors = listOf(Color.Black, Color.Red, Color.Blue, Color.Green)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(
                        offset = Offset(30f, 80f),
                        size = Size(290f, 150f)
                    ),
                    CornerRadius(45f)
                )
            )
        }
        clipPath(path) {
            var start = 30f
            var end = 0f
            for (data in 1..3) {
                end = start + data * 50
                drawRect(
                    color = colors[data],
                    topLeft = Offset(start, 80f),
                    size = Size(end - start, 150f)
                )
                start = end
            }
        }
    }
}