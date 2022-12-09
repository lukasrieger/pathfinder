package pathfinder

sealed interface Route {

    val template: RouteTemplate<*>

    data class Route1<A>(
        override val template: RouteTemplate<A>,
        val handler: (A) -> Unit
    ) : Route

    data class Route2<A, B>(
        override val template: RouteTemplate<Pair<A, B>>,
        val handler: (A, B) -> Unit
    ) : Route

    data class Route3<A, B, C>(
        override val template: RouteTemplate<Triple<A, B, C>>,
        val handler: (A, B, C) -> Unit
    ) : Route

    data class Route4<A, B, C, D>(
        override val template: RouteTemplate<Tuple4<A, B, C, D>>,
        val handler: (A, B, C, D) -> Unit
    ) : Route

    data class Route5<A, B, C, D, E>(
        override val template: RouteTemplate<Tuple5<A, B, C, D, E>>,
        val handler: (A, B, C, D, E) -> Unit
    ) : Route
}