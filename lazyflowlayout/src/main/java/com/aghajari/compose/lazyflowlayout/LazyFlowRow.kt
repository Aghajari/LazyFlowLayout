package com.aghajari.compose.lazyflowlayout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LazyFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemInlineAlignment: Alignment = Alignment.Center,
    maxLines: Int = Int.MAX_VALUE,
    animation: LazyFlowLayoutAnimation? = DefaultLazyFlowLayoutAnimation(),
    content: LazyFlowLayoutScope.() -> Unit
) {
    LazyFlowLayout(
        flowLayoutDirection = FlowLayoutDirection.Row,
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        itemInlineAlignment = itemInlineAlignment,
        maxLines = maxLines,
        animation = animation,
        content = content
    )
}
