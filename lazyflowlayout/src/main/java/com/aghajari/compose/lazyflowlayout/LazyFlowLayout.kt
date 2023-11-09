package com.aghajari.compose.lazyflowlayout

import androidx.annotation.IntRange
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

/**
 * A layout composable that places its children in a way that basic CSS Flexible Box
 * Layout Module does.
 *
 * You can use this Layout to form the required [FlowLayoutLine]s and use the combination
 * of [Row]/[Column] and [LazyFlowLayout] for more advanced use such as two dimensional layout
 * or flex grow. You can also create different arrangements such as RowReverse and
 * ColumnReverse by setting the desired [Arrangement]s.
 *
 * LazyFlowLayoutDirection:
 * * [FlowLayoutDirection.Row]: LazyFlowLayout places the children horizontally in the vertical lines.
 * * [FlowLayoutDirection.Column]: LazyFlowLayout places the children vertically in the horizontal lines.
 *
 * Arrangement:
 * * If [FlowLayoutDirection] is [FlowLayoutDirection.Row],
 * [horizontalArrangement] defines the arrangement of [Placeable]s on the line
 * and [verticalArrangement] defines the arrangement of [FlowLayoutLine]s.
 * * If [FlowLayoutDirection] is [FlowLayoutDirection.Column],
 * [verticalArrangement] defines the arrangement of [Placeable]s on the line
 * and [horizontalArrangement] defines the arrangement of [FlowLayoutLine]s.
 *
 * @param flowLayoutDirection The direction children items are placed, it determines the direction
 *  of the main axis.
 * @param itemInlineAlignment The default alignment of [Placeable]s inside a [FlowLayoutLine].
 * @param modifier [Modifier] to apply for the layout.
 * @param horizontalArrangement Used to specify the horizontal arrangement of [Placeable]s.
 * @param verticalArrangement Used to specify the vertical arrangement of [Placeable]s.
 * @param maxLines an optional maximum number of lines. It must be greater than zero.
 * @param maxItemsInEachLine The maximum number of items per line. It must be greater than zero.
 * @param animation The animation of item movements. A [spring] spec will be used for
 *  the animation by default. Pass null to disable the animation.
 * @param content a block which describes the content. Inside this block you can use methods
 *  like [LazyFlowLayoutScope.item] to add a single item or [LazyFlowLayoutScope.items] to add a list of items.
 *
 * @author Aghajari
 */
@Composable
internal fun LazyFlowLayout(
    flowLayoutDirection: FlowLayoutDirection,
    itemInlineAlignment: InlineAlignment,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    @IntRange(from = 1) maxLines: Int = Int.MAX_VALUE,
    @IntRange(from = 1) maxItemsInEachLine: Int = Int.MAX_VALUE,
    animation: LazyFlowLayoutAnimation? = DefaultLazyFlowLayoutAnimation(),
    content: LazyFlowLayoutScope.() -> Unit
) = with(flowLayoutDirection) {
    val itemProvider = rememberLazyFlowLayoutItemProvider(content)
    val (itemsPaddingPx, linesPaddingPx) = resolveSpacingsInPx(
        horizontalArrangement,
        verticalArrangement
    )

    val animationState = animation?.let {
        val coroutineScope = rememberCoroutineScope()
        remember(animation) {
            LazyFlowLayoutAnimationState(animation, coroutineScope)
        }
    }

    SubcomposeLayout(modifier) { constraints ->
        var availableLineSpace = constraints.flexLineSpace
        var currentLine = FlowLayoutLine()
        val lines = mutableSetOf<FlowLayoutLine>()
        animationState?.startComposition()

        itemProvider.forEach { key, content ->
            val itemContents = subcompose(key) {
                content.invoke()
            }.map { it.measure(Constraints()) }

            if (itemContents.isEmpty()) {
                return@forEach true
            }

            val contentSpace = itemContents.sumOf { it.flexContentSpace }
            val contentLineSpace = itemContents.sumOf { it.flexContentLineSpace }

            // We never can lay out this item!
            if (contentSpace > constraints.flexSpace ||
                contentLineSpace > availableLineSpace ||
                itemContents.size > maxItemsInEachLine
            ) {
                return@forEach true
            }

            val numItems = currentLine.sizeOfItems() + itemContents.size
            val added = if (numItems <= maxItemsInEachLine) {
                currentLine.addPlaceables(
                    itemContents,
                    constraints.flexSpace,
                    contentSpace,
                    itemsPaddingPx
                )
            } else {
                false
            }

            if (added.not()) {
                availableLineSpace -= getLineSpace(currentLine) + linesPaddingPx
                if (lines.size + 1 >= maxLines || availableLineSpace < contentLineSpace) {
                    false
                } else {
                    currentLine.close(itemsPaddingPx)
                    lines.add(currentLine)
                    currentLine = FlowLayoutLine()
                    currentLine.addPlaceables(
                        itemContents,
                        constraints.flexSpace,
                        contentSpace,
                        itemsPaddingPx
                    ).also {
                        if (it) {
                            animationState?.addPlaceables(itemContents)
                        }
                    }
                }
            } else {
                animationState?.addPlaceables(itemContents)
                true
            }
        }
        animationState?.endComposition()
        if (currentLine.sizeOfItems() > 0) {
            currentLine.close(itemsPaddingPx)
            lines.add(currentLine)
        }

        flowLayout(
            constraints,
            itemInlineAlignment,
            animationState,
            lines,
            horizontalArrangement,
            verticalArrangement
        )
    }
}