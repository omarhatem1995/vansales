/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.company.vansales.app.datamodel.models.base;

import java.io.IOException

/**
 * Common class used by API responses.
 * @param <R> the type of the response object
 * @param <E> the type of the api error object
</T> */
sealed class ApiResponse<out R: Any, out E : Any > {

    data class Success<R : Any>(val body: R) : ApiResponse<R, Nothing>()

    /**
     * Failure response with body
     */
    data class ApiError< E : Any>(val body:E?, val code: Int) : ApiResponse<Nothing, E>()

    /**
     * Network error
     */

    data class NetworkError(val error: IOException) : ApiResponse<Nothing, Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable?) : ApiResponse<Nothing, Nothing>()

    object EmptyResponse: ApiResponse<Nothing, Nothing>()
}