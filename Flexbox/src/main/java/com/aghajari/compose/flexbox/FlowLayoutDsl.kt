package com.aghajari.compose.flexbox

import androidx.compose.runtime.Composable

/**
 * Receiver scope which is used by [LazyFlowLayout].
 */
interface LazyFlowLayoutScope {

    /**
     * Adds a single item.
     *
     * @param key a stable and unique key representing the item. Using the same key
     * for multiple items in the list is not allowed. If null is passed the position in the list
     * will represent the key.
     * @param itemContent the content of the item
     */
    fun item(
        key: Any? = null,
        itemContent: @Composable () -> Unit
    )

    /**
     * Adds a [count] of items.
     *
     * @param count the items count
     * @param key a factory of stable and unique keys representing the item. Using the same key
     * for multiple items in the list is not allowed. If null is passed the position in the list
     * will represent the key.
     * @param itemContent the content displayed by a single item
     */
    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable (index: Int) -> Unit
    )

    /**
     * Adds a list of items where the content of an item is aware of its index.
     *
     * @param items the data list
     * @param key a factory of stable and unique keys representing the item. Using the same key
     * for multiple items in the list is not allowed. If null is passed the position in the list
     * will represent the key.
     * @param itemContent the content displayed by a single item
     */
    fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable (index: Int, item: T) -> Unit
    )

    /**
     * Adds a list of items.
     *
     * @param items the data list
     * @param key a factory of stable and unique keys representing the item. Using the same key
     * for multiple items in the list is not allowed. If null is passed the position in the list
     * will represent the key.
     * @param itemContent the content displayed by a single item
     */
    fun <T> items(
        items: List<T>,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable (item: T) -> Unit
    )
}