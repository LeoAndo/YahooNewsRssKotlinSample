package leo.yahoonewsrsskotlinsample

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T) : ApiResult<T>()
    object FetchFeedError : ApiResult<Nothing>()
    // data class FetchFeedError(val message: String) : ApiResult<Nothing>()
}
