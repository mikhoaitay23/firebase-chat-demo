package com.example.firebase_chat_demo.data.response

import com.example.firebase_chat_demo.data.model.LoadDataStatus


sealed class DataResponse<T> constructor(val loadDataStatus: LoadDataStatus) {
    class DataEmptyResponse<T> : DataResponse<T>(LoadDataStatus.IDLE)
    class DataErrorResponse<T> : DataResponse<T>(LoadDataStatus.ERROR)
    data class DataSuccessResponse<T>(val body: T) : DataResponse<T>(LoadDataStatus.SUCCESS)
    class DataLoadingResponse<T> : DataResponse<T>(LoadDataStatus.LOADING)
}