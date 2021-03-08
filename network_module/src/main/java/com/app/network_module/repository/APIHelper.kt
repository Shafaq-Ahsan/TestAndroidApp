package com.app.network_module.repository
 // api helper class
sealed class Result<out T : Any> // restrict hierarchy
class Success<out T : Any>(val data: T) : Result<T>()
class Error(val exception: Throwable, val message: String? = exception.localizedMessage) : Result<Nothing>()

inline fun <T : Any> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Success) action(data) // on api sucess
    return this
}
inline fun <T : Any> Result<T>.onError(action: (Error) -> Unit): Result<T> {
    if (this is Error) action(this) // on failure
    return this
}