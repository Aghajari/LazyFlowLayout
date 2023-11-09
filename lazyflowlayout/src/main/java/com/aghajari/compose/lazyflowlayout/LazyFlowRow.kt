package com.aghajari.compose.lazyflowlayout

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LazyFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemInlineAlignment: Alignment.Vertical = Alignment.CenterVertically,
    @IntRange(from = 1) maxLines: Int = Int.MAX_VALUE,
    @IntRange(from = 1) maxItemsInEachLine: Int = Int.MAX_VALUE,
    animation: LazyFlowLayoutAnimation? = DefaultLazyFlowLayoutAnimation(),
    content: LazyFlowLayoutScope.() -> Unit
) {
    LazyFlowLayout(
        flowLayoutDirection = FlowLayoutDirection.Row,
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        itemInlineAlignment = VerticalInlineAlignment(itemInlineAlignment),
        maxLines = maxLines,
        maxItemsInEachLine = maxItemsInEachLine,
        animation = animation,
        content = content
    )
}
