package com.aghajari.compose.lazyflowlayout

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.LayoutDirection

@Stable
internal fun interface InlineAlignment {

    fun align(size: Int, space: Int, layoutDirection: LayoutDirection): Int
}

@Immutable
internal class HorizontalInlineAlignment(
    private val alignment: Alignment.Horizontal
) : InlineAlignment {

    override fun align(
        size: Int,
        space: Int,
        layoutDirection: LayoutDirection
    ): Int {
        return alignment.align(size, space, layoutDirection)
    }
}

@Immutable
internal class VerticalInlineAlignment(
    private val alignment: Alignment.Vertical
) : InlineAlignment {

    override fun align(
        size: Int,
        space: Int,
        layoutDirection: LayoutDirection
    ): Int {
        return alignment.align(size, space)
    }
}