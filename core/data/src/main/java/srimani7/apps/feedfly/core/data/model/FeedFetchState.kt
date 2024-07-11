package srimani7.apps.feedfly.core.data.model

sealed class FeedFetchState(val message: String?) {
    data object Loading : FeedFetchState(null)
    data object Completed : FeedFetchState(null)
    data object LastBuild : FeedFetchState("You are up to date")
    class Failure(message: String?) : FeedFetchState(message)
}