package com.aghajari.compose.flexbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

internal interface FlowLayoutItemProvider {

    /**
     * Performs the given [action] on each item.
     * @param [action] function that takes the key of an item and
     * the content of the item and performs the action on the item.
     */
    fun forEach(
        action: (
            key: Any,
            content: @Composable () -> Unit
        ) -> Boolean
    )
}

@Composable
internal fun rememberFlexboxItemProvider(
    content: LazyFlowLayoutScope.() -> Unit,
): FlowLayoutItemProvider {
    val latestContent = rememberUpdatedState(content)

    return remember {
        val listScope = LazyFlowLayoutScopeImpl().apply(latestContent.value)
        val itemProviderState = derivedStateOf {
            FlowLayoutItemProviderImpl(listScope.intervals)
        }
        delegatingFlexboxItemProvider(itemProviderState)
    }
}

internal class FlowLayoutItemProviderImpl(
    private val intervals: List<FlexboxIntervalContent>
) : FlowLayoutItemProvider {

    override fun forEach(
        action: (
            key: Any,
            content: @Composable () -> Unit
        ) -> Boolean
    ) {
        var index = 0
        for (interval in intervals) {
            for (localIndex in interval.indices) {
                val key = interval.key?.invoke(localIndex)
                    ?: getDefaultFlowLayoutKey(index)
                val keepGoing = action.invoke(
                    key,
                    interval.composable(localIndex)
                )
                if (keepGoing.not()) {
                    return
                }
                index++
            }
        }
    }
}

/**
 * Delegating version of [FlowLayoutItemProvider], abstracting internal [State] access.
 * This way, passing [FlowLayoutItemProvider] will not trigger recomposition unless
 * its methods are called within composable functions.
 *
 * @param delegate [State] to delegate [FlowLayoutItemProvider] functionality to.
 */
private fun delegatingFlexboxItemProvider(
    delegate: State<FlowLayoutItemProvider>
): FlowLayoutItemProvider = DefaultDelegatingFlowLayoutItemProvider(delegate)

private class DefaultDelegatingFlowLayoutItemProvider(
    private val delegate: State<FlowLayoutItemProvider>
) : FlowLayoutItemProvider {

    override fun forEach(action: (key: Any, content: @Composable () -> Unit) -> Boolean) =
        delegate.value.forEach(action)
}