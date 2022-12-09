package pathfinder

@DslMarker
annotation class RouteDslMarker

@RouteDslMarker
operator fun String.div(other: String): RouteTemplate<Unit> =
    RouteTemplate.Fixed(this).and(RouteTemplate.Fixed(other))

@RouteDslMarker
fun <A> p(name: String? = null): RouteTemplate.Capture<A> =
    RouteTemplate.Capture(name = name)

@RouteDslMarker
fun <A> param(name: String? = null): RouteTemplate.Capture<A> = p(name)

@RouteDslMarker
fun <A> optional(name: String, default: A): RouteTemplate<A> =
    RouteTemplate.Query(name, default)

@RouteDslMarker
fun <A> o(name: String, default: A): RouteTemplate<A> =
    optional(name, default)

@RouteDslMarker
operator fun <A> String.div(other: RouteTemplate<A>): RouteTemplate<A> =
    RouteTemplate.Fixed(this).and(other)

@RouteDslMarker
operator fun <A> RouteTemplate<A>.div(other: String): RouteTemplate<A> =
    and(RouteTemplate.Fixed(other))

@RouteDslMarker
operator fun <A> RouteTemplate<Unit>.div(other: RouteTemplate<A>): RouteTemplate<A> =
    this.and(other)

@RouteDslMarker
@JvmName("div2")
operator fun <A, B> RouteTemplate<A>.div(other: RouteTemplate<B>): RouteTemplate<Pair<A, B>> =
    this and other

@RouteDslMarker
@JvmName("div3")
operator fun <A, B, C> RouteTemplate<Pair<A, B>>.div(other: RouteTemplate<C>): RouteTemplate<Triple<A, B, C>> =
    this and other

@RouteDslMarker
@JvmName("div3")
operator fun <A, B, C, D> RouteTemplate<Triple<A, B, C>>.div(
    other: RouteTemplate.Capture<D>
): RouteTemplate<Tuple4<A, B, C, D>> = this.and(other)

@RouteDslMarker
@JvmName("div4")
operator fun <A, B, C, D, E> RouteTemplate<Tuple4<A, B, C, D>>.div(
    other: RouteTemplate.Capture<E>
): RouteTemplate<Tuple5<A, B, C, D, E>> = this.and(other)

@RouteDslMarker
@JvmName("div5")
operator fun <A, B, C, D, E, F> RouteTemplate<Tuple5<A, B, C, D, E>>.div(
    other: RouteTemplate.Capture<F>
): RouteTemplate<Tuple6<A, B, C, D, E, F>> = this.and(other)

@RouteDslMarker
inline operator fun <reified A> RouteTemplate<A>.div(other: RouteTemplate.Fixed): RouteTemplate<A> =
    this.and(other)
