package com.aghajari.compose.flexbox

import androidx.compose.runtime.Composable

internal class FlexboxScopeImpl : FlexboxScope {

    private val _intervals = mutableListOf<FlexboxIntervalContent>()
    val intervals: List<FlexboxIntervalContent> = _intervals

    override fun item(
        key: Any?,
        itemContent: @Composable () -> Unit
    ) {
        _intervals.add(
            FixedFlexboxIntervalContent(
                itemCount = 1,
                key = key?.let { { key } }
            ) {
                itemContent()
            }
        )
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        _intervals.add(FixedFlexboxIntervalContent(count, key, itemContent))
    }

    override fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (index: Int, item: T) -> Unit
    ) {
        _intervals.add(
            ListFlexboxIntervalContent(items, key) { index, item ->
                itemContent(index, item)
            }
        )
    }

    override fun <T> items(
        items: List<T>,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (item: T) -> Unit
    ) {
        _intervals.add(
            ListFlexboxIntervalContent(items, key) { _, item ->
                itemContent(item)
            }
        )
    }
}

internal interface FlexboxIntervalContent {
    val itemCount: Int
    val key: ((index: Int) -> Any)?

    fun composable(index: Int): @Composable () -> Unit
}

internal class FixedFlexboxIntervalContent(
    override val itemCount: Int,
    override val key: ((index: Int) -> Any)?,
    private val item: @Composable (index: Int) -> Unit
) : FlexboxIntervalContent {

    override fun composable(index: Int): @Composable () -> Unit = {
        item.invoke(index)
    }
}

internal class ListFlexboxIntervalContent<T>(
    private val list: List<T>,
    override val key: ((index: Int) -> Any)?,
    private val item: @Composable (index: Int, value: T) -> Unit
) : FlexboxIntervalContent {

    override val itemCount: Int
        get() = list.size

    override fun composable(index: Int): @Composable () -> Unit {
        val value = list[index]
        return {
            item.invoke(index, value)
        }
    }
}

internal val FlexboxIntervalContent.indices: IntRange
    get() = 0 until itemCount