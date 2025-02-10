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

package com.company.vansales.app.datamodel.services.core.networkresponsefactory;

import com.company.vansales.app.datamodel.models.base.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class ApiResponseCallAdapter<S : Any , E : Any>(
        private val successType: Type ,
        private val errorBodyConverter: Converter<ResponseBody, E>
) : CallAdapter<S, Call<ApiResponse<S, E>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<ApiResponse<S,E>> {
        return NetworkResponseCall(call , errorBodyConverter)
    }
}