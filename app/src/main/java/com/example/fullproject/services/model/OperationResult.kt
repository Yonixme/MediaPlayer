package com.example.fullproject.services.model



sealed class OperationResult<T>{

}

class PendingOperationResult<T> : OperationResult<T>()

class Success<T>(
    val data: T
): OperationResult<T>()

class ErrorOperationResult<T>(
    exception: Exception
): OperationResult<T>()