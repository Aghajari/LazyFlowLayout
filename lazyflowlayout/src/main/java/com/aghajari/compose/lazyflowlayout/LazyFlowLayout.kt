package com.aghajari.compose.lazyflowlayout

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
 * @param modifier [Modifier] to apply for the layout.
 * @param horizontalArrangement Used to specify the horizontal arrangement of [Placeable]s.
 * @param verticalArrangement Used to specify the vertical arrangement of [Placeable]s.
 * @param itemInlineAlignment The default alignment of [Placeable]s inside a [FlowLayoutLine].
 * @param maxLines an optional maximum number of lines. It must be greater than zero.
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
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemInlineAlignment: Alignment = Alignment.Center,
    maxLines: Int = Int.MAX_VALUE,
    animation: LazyFlowLayoutAnimation? = DefaultLazyFlowLayoutAnimation(),
    content: LazyFlowLayoutScope.() -> Unit
) = with(flowLayoutDirection) {
    require(maxLines > 0) {
        "maxLines must be greater than zero."
    }

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

            val contentSpace = itemContents
                .map { it.flexContentSpace }
                .reduce(Int::plus)
            val contentLineSpace = itemContents
                .map { it.flexContentLineSpace }
                .reduce(Int::plus)

            // We never can lay out this item!
            if (contentSpace > constraints.flexSpace ||
                contentLineSpace > availableLineSpace
            ) {
                return@forEach true
            }

            val added = currentLine.addPlaceables(
                itemContents,
                constraints.flexSpace,
                contentSpace,
                itemsPaddingPx
            )
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
        currentLine.close(itemsPaddingPx)
        lines.add(currentLine)

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