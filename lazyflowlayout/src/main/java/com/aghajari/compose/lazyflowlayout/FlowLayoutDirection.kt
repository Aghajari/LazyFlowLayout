package com.aghajari.compose.lazyflowlayout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.max

/**
 * The direction children items are placed inside the [LazyFlowLayout],
 * it determines the direction of the main axis.
 */
@Stable
internal interface FlowLayoutDirection {

    /**
     * The maximum available space that can be occupied by
     * contents of each [FlowLayoutLine], in pixels.
     */
    val Constraints.flexSpace: Int

    /**
     * The maximum available space that can be occupied by
     * [FlowLayoutLine]s, in pixels.
     */
    val Constraints.flexLineSpace: Int

    /**
     * The space that this [Placeable] occupies in a [FlowLayoutLine]
     * in the direction of the contents in the line, in pixels.
     */
    val Placeable.flexContentSpace: Int

    /**
     * The space that this [Placeable] occupies in a [FlowLayoutLine]
     * in the direction of the lines, in pixels.
     */
    val Placeable.flexContentLineSpace: Int

    /**
     * Places the layout [Placeable]s.
     *
     * @param constraints The [Constraints] of the [LazyFlowLayout]
     * @param itemInlineAlignment The default alignment of [Placeable]s inside a [FlowLayoutLine].
     * @param animationState The animation state to calculate the position of each [Placeable]
     *  with [LazyFlowLayoutAnimation] in mind.
     * @param lines The list of all [FlowLayoutLine]s that includes the [Placeable]s of each line.
     * @param horizontalArrangement Used to specify the horizontal arrangement of the layout's [Placeable]s.
     * @param verticalArrangement Used to specify the vertical arrangement of the layout's [Placeable]s.
     */
    fun MeasureScope.flowLayout(
        constraints: Constraints,
        itemInlineAlignment: InlineAlignment,
        animationState: LazyFlowLayoutAnimationState?,
        lines: Set<FlowLayoutLine>,
        horizontalArrangement: Arrangement.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ): MeasureResult

    /**
     * An interface to calculate the position of [Placeable]s where
     * [Placeable]s on each [FlowLayoutLine] are arranged horizontally
     * in a row and the [FlowLayoutLine]s are arranged vertically.
     */
    @Stable
    interface Horizontal : FlowLayoutDirection {
        override val Constraints.flexSpace: Int
            get() = maxWidth
        override val Constraints.flexLineSpace: Int
            get() = maxHeight
        override val Placeable.flexContentSpace: Int
            get() = width
        override val Placeable.flexContentLineSpace: Int
            get() = height

        override fun MeasureScope.flowLayout(
            constraints: Constraints,
            itemInlineAlignment: InlineAlignment,
            animationState: LazyFlowLayoutAnimationState?,
            lines: Set<FlowLayoutLine>,
            horizontalArrangement: Arrangement.Horizontal,
            verticalArrangement: Arrangement.Vertical
        ): MeasureResult {
            val width = constraints.constrainWidth(lines.maxContentSpace)

            val (lineSizes, totalSize) = getLinesSizes(
                lines,
                verticalArrangement.spacing.roundToPx()
            )
            val height = constraints.constrainHeight(totalSize)
            val outLinesPositions = IntArray(lineSizes.size)
            verticalArrangement.apply {
                arrange(height, lineSizes, outLinesPositions)
            }

            return layout(width, height) {
                lines.forEachIndexed { lineIndex, line ->
                    val placeableSizes = IntArray(line.placeables.size)
                    val placeablePositions = IntArray(line.placeables.size)
                    line.placeables.forEachIndexed { placeableIndex, placeable ->
                        placeableSizes[placeableIndex] = placeable.width
                    }
                    horizontalArrangement.apply {
                        arrange(width, placeableSizes, layoutDirection, placeablePositions)
                    }

                    line.placeables.forEachIndexed { placeableIndex, placeable ->
                        val position = animationState.findPlaceablePosition(
                            placeable,
                            IntOffset(
                                x = placeablePositions[placeableIndex],
                                y = outLinesPositions[lineIndex] +
                                    itemInlineAlignment.align(
                                        placeable.height,
                                        getLineSpace(line),
                                        layoutDirection
                                    )
                            )
                        )
                        placeable.place(position)
                    }
                }
            }
        }
    }

    /**
     * An interface to calculate the position of [Placeable]s where
     * [Placeable]s on each [FlowLayoutLine] are arranged vertically
     * in a column and the [FlowLayoutLine]s are arranged horizontally.
     */
    @Stable
    interface Vertical : FlowLayoutDirection {
        override val Constraints.flexSpace: Int
            get() = maxHeight
        override val Constraints.flexLineSpace: Int
            get() = maxWidth
        override val Placeable.flexContentSpace: Int
            get() = height
        override val Placeable.flexContentLineSpace: Int
            get() = width

        override fun MeasureScope.flowLayout(
            constraints: Constraints,
            itemInlineAlignment: InlineAlignment,
            animationState: LazyFlowLayoutAnimationState?,
            lines: Set<FlowLayoutLine>,
            horizontalArrangement: Arrangement.Horizontal,
            verticalArrangement: Arrangement.Vertical
        ): MeasureResult {
            val (lineSizes, totalSize) = getLinesSizes(
                lines,
                horizontalArrangement.spacing.roundToPx()
            )
            val width = constraints.constrainWidth(totalSize)
            val outLinesPositions = IntArray(lineSizes.size)
            horizontalArrangement.apply {
                arrange(width, lineSizes, layoutDirection, outLinesPositions)
            }

            val height = constraints.constrainHeight(lines.maxContentSpace)

            return layout(width, height) {
                lines.forEachIndexed { lineIndex, line ->
                    val placeableSizes = IntArray(line.placeables.size)
                    val placeablePositions = IntArray(line.placeables.size)
                    line.placeables.forEachIndexed { placeableIndex, placeable ->
                        placeableSizes[placeableIndex] = placeable.height
                    }
                    verticalArrangement.apply {
                        arrange(height, placeableSizes, placeablePositions)
                    }

                    line.placeables.forEachIndexed { placeableIndex, placeable ->
                        val position = animationState.findPlaceablePosition(
                            placeable,
                            IntOffset(
                                x = outLinesPositions[lineIndex] +
                                    itemInlineAlignment.align(
                                        placeable.width,
                                        getLineSpace(line),
                                        layoutDirection
                                    ),
                                y = placeablePositions[placeableIndex]
                            )
                        )
                        placeable.place(position)
                    }
                }
            }
        }
    }

    @Immutable
    companion object {

        /**
         * Main axis direction -> horizontal. Main start to
         * main end -> Left to right (in LTR languages).
         */
        @Stable
        val Row = object : Horizontal {
            override fun toString() = "FlowLayoutDirection#Row"
        }

        /**
         * Main axis direction -> vertical. Main start
         * to main end -> Top to bottom.
         */
        @Stable
        val Column = object : Vertical {
            override fun toString() = "FlowLayoutDirection#Column"
        }
    }
}

/**
 * @return True if [Placeable]s on each [FlowLayoutLine] are
 * arranged horizontally in a row.
 */
internal fun FlowLayoutDirection.isHorizontal(): Boolean {
    return this is FlowLayoutDirection.Horizontal
}

/**
 * Calculates the space between each two [Placeable]s
 * and each two [FlowLayoutLine]s based on the specified
 * [Arrangement]s.
 *
 * * If [FlowLayoutDirection] is [FlowLayoutDirection.Horizontal],
 * [horizontalArrangement] defines the distance between
 * [Placeable]s and [verticalArrangement] determines the
 * distance between [FlowLayoutLine]s.
 * * If [FlowLayoutDirection] is [FlowLayoutDirection.Vertical],
 * [verticalArrangement] defines the distance between
 * [Placeable]s and [horizontalArrangement] determines the
 * distance between [FlowLayoutLine]s.
 */
@Composable
internal fun FlowLayoutDirection.resolveSpacingsInPx(
    horizontalArrangement: Arrangement.Horizontal,
    verticalArrangement: Arrangement.Vertical
): Pair<Int, Int> {
    return with(LocalDensity.current) {
        if (isHorizontal()) {
            Pair(
                horizontalArrangement.spacing.roundToPx(),
                verticalArrangement.spacing.roundToPx()
            )
        } else {
            Pair(
                verticalArrangement.spacing.roundToPx(),
                horizontalArrangement.spacing.roundToPx()
            )
        }
    }
}

/**
 * Returns the space that the specified [FlowLayoutLine] occupies
 * in the direction of the lines, in pixels.
 */
internal fun FlowLayoutDirection.getLineSpace(line: FlowLayoutLine): Int {
    return line.placeables.maxOfOrNull { it.flexContentLineSpace } ?: 0
}

/**
 * Returns list of sizes that each [FlowLayoutLine] occupies
 * in the direction of the lines, in pixels.
 */
private fun FlowLayoutDirection.getLinesSizes(
    lines: Set<FlowLayoutLine>,
    linesPadding: Int
): Pair<IntArray, Int> {
    val sizes = IntArray(lines.size)
    var total = 0
    lines.forEachIndexed { index, line ->
        val lineSpace = getLineSpace(line)
        total += lineSpace + linesPadding
        sizes[index] = lineSpace
    }
    return sizes to max(0, total - linesPadding)
}

/**
 * Returns the maximum space that the [FlowLayoutLine]s occupies
 * in the direction of the contents in the line, in pixels.
 */
private val Set<FlowLayoutLine>.maxContentSpace: Int
    get() = maxOfOrNull { it.usedSpace } ?: 0