package com.aghajari.compose.lazyflowlayout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LazyFlowColumn(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemInlineAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    maxLines: Int = Int.MAX_VALUE,
    animation: LazyFlowLayoutAnimation? = DefaultLazyFlowLayoutAnimation(),
    content: LazyFlowLayoutScope.() -> Unit
) {
    LazyFlowLayout(
        flowLayoutDirection = FlowLayoutDirection.Column,
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        itemInlineAlignment = HorizontalInlineAlignment(itemInlineAlignment),
        maxLines = maxLines,
        animation = animation,
        content = content
    )
}
