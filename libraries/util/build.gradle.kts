/*
 * Copyright (c) 2018 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.library")
  kotlin("android")
  id("dev.zacsweers.redacted.redacted-gradle-plugin")
}

apply {
  plugin("com.google.protobuf")
}

android {
  compileSdkVersion(deps.android.build.compileSdkVersion)

  defaultConfig {
    minSdkVersion(deps.android.build.minSdkVersion)
    targetSdkVersion(deps.android.build.targetSdkVersion)
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  sourceSets {
    findByName("main")?.java?.srcDirs("src/main/kotlin")
    findByName("debug")?.java?.srcDirs("src/debug/kotlin")
    findByName("release")?.java?.srcDirs("src/release/kotlin")
    findByName("test")?.java?.srcDirs("src/test/kotlin")
  }
  buildFeatures {
    viewBinding = true
  }
  libraryVariants.all {
    generateBuildConfigProvider?.configure {
      enabled = false
    }
  }
}

redacted {
  redactedAnnotation = "io.sweers.catchup.util.network.Redacted"
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = build.standardFreeKotlinCompilerArgs
    jvmTarget = "1.8"
  }
}

dependencies {
  api(deps.android.androidx.annotations)

  implementation(deps.android.androidx.appCompat)
  implementation(deps.android.androidx.core)
  implementation(deps.android.androidx.design)
  implementation(deps.rx.android)
  implementation("com.google.protobuf:protobuf-javalite:3.11.3")
  implementation("com.google.protobuf:protobuf-java:3.11.3")

  api(deps.android.androidx.coreKtx)
  api(deps.dagger.runtime)
  api(deps.kotlin.stdlib.jdk7)
  api(deps.misc.lazythreeten)
  api(deps.moshi.core)
  api(deps.misc.timber)
  api(deps.okhttp.core)
  api(deps.rx.java)
  api(deps.misc.okio)

  implementation(deps.misc.unbescape)

  testImplementation(deps.test.junit)
  testImplementation(deps.test.truth)
}
