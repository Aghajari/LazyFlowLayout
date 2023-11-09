package com.aghajari.compose.lazyflowlayout

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Creates and holds the state of [LazyFlowLayout] animation.
 * The animation will use the provided [LazyFlowLayoutAnimation] to animate the value towards the
 * target position.
 */
@Stable
class LazyFlowLayoutAnimationState(
    private val animation: LazyFlowLayoutAnimation,
    private val coroutineScope: CoroutineScope,
) {

    /**
     * [animator] holds the animation fraction value.
     * Whenever a position change detected in [findPlaceablePosition],
     * animator will automatically animate its value from 0.0f to 1.0f.
     *
     * __note__: Using only one [Animatable] for all [Placeable]s that are
     * moved is much more performant than using separate [Animatable]s
     * for each [Placeable] move.
     */
    private val animator = Animatable(
        initialValue = 0f,
        visibilityThreshold = animation.visibilityThreshold
    )

    /**
     * Stores [AnimationValues] of the previous composition [Placeable]s.
     *
     * __note__: Delete this field from memory at the end of composition.
     * Because some [Placeable]s may have been completely removed from [LazyFlowLayout]
     * and we should no longer keep them in memory.
     */
    private var previousPlaceableMap: Map<Placeable, AnimationValues>? = null

    /**
     * Stores [AnimationValues] of the latest composition [Placeable]s.
     *
     * __note__: At the beginning of each new composition, transfer
     * current values to [previousPlaceableMap] and clear this.
     */
    private var placeableMap = mutableMapOf<Placeable, AnimationValues>()

    /**
     * Notifies start of a new composition.
     * With the start of the new Composition, we transfer the data of
     * the previous Composition that are currently in the [placeableMap] to
     * the [previousPlaceableMap] and prepare the [placeableMap] for a new start.
     */
    internal fun startComposition() {
        previousPlaceableMap = placeableMap
        if (placeableMap.isNotEmpty()) {
            placeableMap = LinkedHashMap(placeableMap.size)
        }
    }

    /**
     * We add every [Placeable] that was added to the [LazyFlowLayout] during Composition to
     * the [placeableMap]. [AnimationValues] should be taken from [previousPlaceableMap].
     * If the [Placeable] is created for the very first time, we create a new initial
     * object of [AnimationValues] for it.
     */
    internal fun addPlaceables(
        placeables: List<Placeable>
    ) {
        placeables.forEach { placeable ->
            placeableMap[placeable] = previousPlaceableMap?.get(placeable) ?: AnimationValues()
        }
    }

    /**
     * Notifies end of the composition.
     * We should no longer keep [previousPlaceableMap] in memory.
     */
    internal fun endComposition() {
        previousPlaceableMap = null
    }

    /**
     * During the arrangement of the items, it inputs the expected final position
     * for each [Placeable] and takes a new position with the animation in mind.
     *
     * * If [AnimationValues] for the specified [Placeable] is empty, it means
     * that this item is added for the first time.
     * * if the specified position is not the same as the previous position of
     * [Placeable] that is kept in [AnimationValues.startValue], it means that it
     * has been moved and if the animation is not running, the animation should be
     * start and the output position should be calculated in relation to the current
     * fraction of the animation.
     */
    fun findPlaceablePosition(placeable: Placeable, position: IntOffset): IntOffset {
        placeableMap[placeable]?.apply {
            if (startValue == noValue) {
                startValue = position
                targetValue = startValue
            } else if (position != startValue) {
                val fraction = if (animator.isRunning.not()) {
                    coroutineScope.launch {
                        animator.snapTo(0f)
                        animator.animateTo(
                            targetValue = 1f,
                            animationSpec = animation.animationSpec
                        )
                        placeableMap.forEach { (_, values) ->
                            values.startValue = values.targetValue
                        }
                    }
                    0f
                } else {
                    animator.value
                }
                if (targetValue != position && targetValue != startValue) {
                    startValue = animation.evaluate(
                        fraction,
                        startValue,
                        targetValue
                    )
                }
                targetValue = position

                return animation.evaluate(
                    fraction,
                    startValue,
                    targetValue
                )
            }
        }
        return position
    }
}

/**
 * [AnimationValues] stores and holds the start position and
 * target position of each [Placeable].
 * * Whenever [startValue] is equal to [noValue], it means that
 * the associated [Placeable] has not been arranged even once.
 * * Whenever the [startValue] is equal to the [targetValue],
 * it means that there is no movement and there is no need
 * to apply animation.
 */
@Stable
internal data class AnimationValues(
    var startValue: IntOffset = noValue,
    var targetValue: IntOffset = noValue
)

private val noValue = IntOffset(Int.MIN_VALUE, Int.MIN_VALUE)

internal fun LazyFlowLayoutAnimationState?.findPlaceablePosition(
    placeable: Placeable,
    position: IntOffset
): IntOffset {
    return this?.let {
        findPlaceablePosition(placeable, position)
    } ?: position
}
