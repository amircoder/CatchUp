/*
 * Copyright (c) 2017 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sweers.catchup.data

import io.sweers.catchup.util.e
import io.sweers.inspector.Inspector
import io.sweers.inspector.ValidationException
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type


/**
 * A converter factory that uses Inspector to validate responses and fails fast if the response
 * models are invalid.
 */
class InspectorConverterFactory private constructor(
    private val inspector: Inspector) : Converter.Factory() {
  override fun responseBodyConverter(
      type: Type,
      annotations: Array<Annotation>,
      retrofit: Retrofit): Converter<ResponseBody, *> {
    val delegateConverter = retrofit.nextResponseBodyConverter<Converter<ResponseBody, *>>(
        this,
        type,
        annotations)
    return InspectorResponseConverter(type, inspector, delegateConverter)
  }

  companion object {

    fun create(inspector: Inspector): InspectorConverterFactory {
      return InspectorConverterFactory(inspector)
    }
  }
}

private class InspectorResponseConverter internal constructor(
    private val type: Type,
    private val inspector: Inspector,
    private val delegateConverter: Converter<ResponseBody, *>) : Converter<ResponseBody, Any> {

  @Throws(IOException::class)
  override fun convert(value: ResponseBody): Any {
    val convert = delegateConverter.convert(value)
    try {
      inspector.validator<Any>(type).validate(convert)
    } catch (validationException: ValidationException) {
      // This response didn't pass validation, throw the exception.
      e(validationException) { "Validation exception: $type" }
      throw IOException(validationException)
    }

    return convert
  }
}