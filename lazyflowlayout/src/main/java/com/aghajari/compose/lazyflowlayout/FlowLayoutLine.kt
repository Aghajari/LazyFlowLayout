package com.aghajari.compose.lazyflowlayout

import androidx.compose.runtime.Stable
import androidx.compose.ui.layout.Placeable
import kotlin.math.max

/**
 * [FlowLayoutLine] holds all [Placeable]s that fit on a line,
 * relative to the [LazyFlowLayout] constraints.
 */
@Stable
internal class FlowLayoutLine {

    /**
     * List of all [Placeable]s on this line.
     */
    val placeables: MutableList<Placeable> = mutableListOf()

    /**
     * The maximum space that the [Placeable]s on this line occupy.
     */
    var usedSpace: Int = 0
        private set

    /**
     * Adds new [Placeable]s to this line.
     *
     * @return True if all of the [placeables] can fit on this line.
     *  False otherwise.
     */
    fun addPlaceables(
        placeables: List<Placeable>,
        flexSpace: Int,
        contentSpace: Int,
        padding: Int
    ): Boolean {
        return if (flexSpace >= usedSpace + contentSpace) {
            usedSpace += contentSpace + padding
            this.placeables.addAll(placeables)
            true
        } else {
            false
        }
    }

    /**
     * Notifies that no more [Placeable] can fit on this line.
     */
    fun close(padding: Int) {
        usedSpace = max(usedSpace - padding, 0)
    }

    fun sizeOfItems() = placeables.size
}