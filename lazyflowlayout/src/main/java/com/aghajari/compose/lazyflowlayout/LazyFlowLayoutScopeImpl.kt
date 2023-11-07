package com.aghajari.compose.lazyflowlayout

import androidx.compose.runtime.Composable

internal class LazyFlowLayoutScopeImpl : LazyFlowLayoutScope {

    private val _intervals = mutableListOf<LazyFlowlayoutIntervalContent>()
    val intervals: List<LazyFlowlayoutIntervalContent> = _intervals

    override fun item(
        key: Any?,
        itemContent: @Composable () -> Unit
    ) {
        _intervals.add(
            FixedLazyFlowlayoutIntervalContent(
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
        _intervals.add(FixedLazyFlowlayoutIntervalContent(count, key, itemContent))
    }

    override fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (index: Int, item: T) -> Unit
    ) {
        _intervals.add(
            ListLazyFlowlayoutIntervalContent(items, key) { index, item ->
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
            ListLazyFlowlayoutIntervalContent(items, key) { _, item ->
                itemContent(item)
            }
        )
    }
}

internal interface LazyFlowlayoutIntervalContent {
    val itemCount: Int
    val key: ((index: Int) -> Any)?

    fun composable(index: Int): @Composable () -> Unit
}

internal class FixedLazyFlowlayoutIntervalContent(
    override val itemCount: Int,
    override val key: ((index: Int) -> Any)?,
    private val item: @Composable (index: Int) -> Unit
) : LazyFlowlayoutIntervalContent {

    override fun composable(index: Int): @Composable () -> Unit = {
        item.invoke(index)
    }
}

internal class ListLazyFlowlayoutIntervalContent<T>(
    private val list: List<T>,
    override val key: ((index: Int) -> Any)?,
    private val item: @Composable (index: Int, value: T) -> Unit
) : LazyFlowlayoutIntervalContent {

    override val itemCount: Int
        get() = list.size

    override fun composable(index: Int): @Composable () -> Unit {
        val value = list[index]
        return {
            item.invoke(index, value)
        }
    }
}

internal val LazyFlowlayoutIntervalContent.indices: IntRange
    get() = 0 until itemCount