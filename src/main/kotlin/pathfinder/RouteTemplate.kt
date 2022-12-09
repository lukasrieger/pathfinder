package pathfinder


private val defaultRenderPathParam: (Int, RouteTemplate.Capture<*>) -> String = { index, pc ->
    pc.name?.let { name -> "{$name}" } ?: "{param$index}"
}

private val defaultRenderQueryParam: ((Int, RouteTemplate.Query<*>) -> String) = { _, q -> "${q.name}={${q.name}}" }

sealed interface RouteTemplate<A> {

    data class Query<A>(val name: String, val defaultValue: A) : RouteTemplate<A>

    data class Fixed(val s: String) : RouteTemplate<Unit>

    data class Capture<A>(val name: String?) : RouteTemplate<A>

    data class Sequence<A, B, C> internal constructor(
        internal val first: RouteTemplate<A>,
        internal val second: RouteTemplate<B>
    ) : RouteTemplate<C>

    fun renderPath(
        renderPathParam: (Int, Capture<*>) -> String = defaultRenderPathParam,
        renderQueryParam: ((Int, Query<*>) -> String)? = defaultRenderQueryParam
    ): String {
        val inputs = toList()
        val (pathComponents, pathParamCount) = renderedPathComponents(inputs, renderPathParam)
        val queryComponents = renderQueryParam
            ?.let { renderedQueryComponents(inputs, it, pathParamCount) }
            ?.joinToString("&")
            ?: ""

        return buildString {
            append("/")
            append(pathComponents.joinToString("/"))
            if (queryComponents.isNotEmpty()) append("?$queryComponents")
        }
    }

    private fun toList(): List<RouteTemplate<*>> = when (this) {
        is Sequence<*, *, *> -> this.first.toList() + this.second.toList()
        is Fixed -> listOf(this)
        is Capture -> listOf(this)
        is Query -> listOf(this)
    }

    private fun renderedPathComponents(
        inputs: List<RouteTemplate<*>>,
        pathParamRendering: (Int, Capture<*>) -> String
    ): Pair<List<String>, Int> =
        inputs.fold(Pair(emptyList(), 1)) { (acc, index), component ->
            when (component) {
                is Capture<*> ->
                    Pair(acc + pathParamRendering(index, component), index + 1)

                is Fixed -> Pair(acc + component.s, index)
                else -> Pair(acc, index)
            }
        }

    private fun renderedQueryComponents(
        inputs: List<RouteTemplate<*>>,
        queryParamRendering: (Int, Query<*>) -> String,
        pathParamCount: Int
    ): List<String> =
        inputs.fold(Pair(emptyList<String>(), pathParamCount)) { (acc, index), component ->
            when (component) {
                is Query<*> -> Pair(acc + queryParamRendering(index, component), index + 1)
                else -> Pair(acc, index)
            }
        }.first
}

@JvmName("and")
infix fun <A, B> RouteTemplate<A>.and(other: RouteTemplate<B>): RouteTemplate<Pair<A, B>> =
    RouteTemplate.Sequence(
        this,
        other
    )

@JvmName("andLeftUnit")
fun <A> RouteTemplate<Unit>.and(
    other: RouteTemplate<A>,
    @Suppress("UNUSED_PARAMETER") dummy: Unit = Unit
): RouteTemplate<A> = RouteTemplate.Sequence(this, other)

@JvmName("andRightUnit")
fun <A> RouteTemplate<A>.and(
    other: RouteTemplate<Unit>,
    @Suppress("UNUSED_PARAMETER") dummy: Unit = Unit
): RouteTemplate<A> = RouteTemplate.Sequence(this, other)

@JvmName("andLeftRightUnit")
fun RouteTemplate<Unit>.and(
    other: RouteTemplate<Unit>,
    @Suppress("UNUSED_PARAMETER") dummy: Unit = Unit
): RouteTemplate<Unit> = RouteTemplate.Sequence(this, other)

@JvmName("and2")
infix fun <A, B, C> RouteTemplate<Pair<A, B>>.and(
    other: RouteTemplate<C>
): RouteTemplate<Triple<A, B, C>> = RouteTemplate.Sequence(this, other)

@JvmName("and2Pair")
infix fun <A, B, C, D> RouteTemplate<Pair<A, B>>.and(
    other: RouteTemplate<Pair<C, D>>
): RouteTemplate<Tuple4<A, B, C, D>> = RouteTemplate.Sequence(this, other)

@JvmName("and2Unit")
infix fun <A, B> RouteTemplate<Pair<A, B>>.and(
    other: RouteTemplate<Unit>
): RouteTemplate<Pair<A, B>> = RouteTemplate.Sequence(this, other)

@JvmName("and3")
infix fun <A, B, C, D> RouteTemplate<Triple<A, B, C>>.and(
    other: RouteTemplate<D>
): RouteTemplate<Tuple4<A, B, C, D>> = RouteTemplate.Sequence(this, other)

@JvmName("and4")
infix fun <A, B, C, D, E> RouteTemplate<Tuple4<A, B, C, D>>.and(
    other: RouteTemplate<E>
): RouteTemplate<Tuple5<A, B, C, D, E>> = RouteTemplate.Sequence(this, other)

@JvmName("and5")
infix fun <A, B, C, D, E, F> RouteTemplate<Tuple5<A, B, C, D, E>>.and(
    other: RouteTemplate<F>
): RouteTemplate<Tuple6<A, B, C, D, E, F>> = RouteTemplate.Sequence(this, other)


infix fun <A> RouteTemplate<A>.handle1(handler: (A) -> Unit): Route.Route1<A> =
    Route.Route1(this, handler)

infix fun <A, B> RouteTemplate<Pair<A, B>>.handle(handler: (A, B) -> Unit): Route.Route2<A, B> =
    Route.Route2(this, handler)

infix fun <A, B, C> RouteTemplate<Triple<A, B, C>>.handle(handler: (A, B, C) -> Unit): Route.Route3<A, B, C> =
    Route.Route3(this, handler)

infix fun <A, B, C, D> RouteTemplate<Tuple4<A, B, C, D>>.handle(
    handler: (A, B, C, D) -> Unit
): Route.Route4<A, B, C, D> = Route.Route4(this, handler)

infix fun <A, B, C, D, E> RouteTemplate<Tuple5<A, B, C, D, E>>.handle(
    handler: (A, B, C, D, E) -> Unit
): Route.Route5<A, B, C, D, E> = Route.Route5(this, handler)
