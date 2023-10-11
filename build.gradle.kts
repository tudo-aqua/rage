/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2023 The RAGe Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.diffplug.spotless.npm.PrettierFormatterStep.DEFAULT_VERSION as PRETTIER_DEFAULT_VERSION
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gradle.node.variant.computeNodeDir
import com.github.gradle.node.variant.computeNodeExec
import org.gradle.api.plugins.JavaBasePlugin.DOCUMENTATION_GROUP
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem

plugins {
  `java-library`
  `maven-publish`
  signing

  alias(libs.plugins.detekt)
  alias(libs.plugins.gitVersioning)
  alias(libs.plugins.kotlin.dokka)
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.kover)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.nexus.publish)
  alias(libs.plugins.node)
  alias(libs.plugins.runtime)
  alias(libs.plugins.spotless)
  alias(libs.plugins.taskTree)
  alias(libs.plugins.versions)
}

group = "tools.aqua"

version = "0.0.0-SNAPSHOT"

gitVersioning.apply {
  describeTagFirstParent = false
  refs {
    considerTagsOnBranches = true
    tag("(?<version>.*)") {
      // on a tag: use the tag name as is
      version = "\${ref.version}"
    }
    branch("main") {
      // on the main branch: use <last.tag.version>-<distance>-<commit>-SNAPSHOT
      version = "\${describe.tag.version}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
    branch(".+") {
      // on other branches: use <last.tag.version>-<branch.name>-<distance>-<commit>-SNAPSHOT
      version =
          "\${describe.tag.version}-\${ref.slug}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
  }

  // optional fallback configuration in case of no matching ref configuration
  rev {
    // in case of missing git data: use 0.0.0-unknown-0-<commit>-SNAPSHOT
    version = "0.0.0-unknown-0-\${commit.short}-SNAPSHOT"
  }
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(libs.bundles.niwen)
  implementation(libs.clikt)
  implementation(libs.jline)
  implementation(libs.xmlutil)

  testImplementation(libs.assertj)
  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.xmlunit)
}

node {
  download = true
  version = libs.versions.nodejs.get()
  workDir = layout.buildDirectory.dir("nodejs")
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
  gradleReleaseChannel = "current"
  rejectVersionIf { isNonStable(candidate.version) && !isNonStable(currentVersion) }
}

spotless {
  kotlin {
    licenseHeaderFile(project.file("config/license/Apache-2.0-cstyle"))
    ktfmt("0.46")
  }
  kotlinGradle {
    licenseHeaderFile(project.file("config/license/Apache-2.0-cstyle"), "(plugins|import )")
    ktfmt("0.46")
  }
  format("markdown") {
    target(".github/**/*.md", "*.md")
    licenseHeaderFile(project.file("config/license/CC-BY-4.0-xmlstyle"), "#+")
    prettier()
        .npmInstallCache()
        .nodeExecutable(computeNodeExec(node, computeNodeDir(node)).get())
        .config(mapOf("parser" to "markdown", "printWidth" to 100, "proseWrap" to "always"))
  }
  yaml {
    target("config/**/*.yml", ".github/**/*.yml", "CITATION.cff")
    licenseHeaderFile(project.file("config/license/Apache-2.0-hashmark"), "[A-Za-z-]+:")
    prettier()
        .npmInstallCache()
        .nodeExecutable(computeNodeExec(node, computeNodeDir(node)).get())
        .config(mapOf("parser" to "yaml", "printWidth" to 100))
  }
  format("toml") {
    target("gradle/libs.versions.toml")
    licenseHeaderFile(project.file("config/license/Apache-2.0-hashmark"), """\[[A-Za-z-]+]""")
    prettier(
            mapOf(
                "prettier" to PRETTIER_DEFAULT_VERSION,
                "prettier-plugin-toml" to libs.versions.prettier.toml.get()))
        .npmInstallCache()
        .nodeExecutable(computeNodeExec(node, computeNodeDir(node)).get())
        .config(mapOf("parser" to "toml", "printWidth" to 100))
  }
  format("xml") {
    target("src/*/resources/**/*.register.xml")
    licenseHeaderFile(project.file("config/license/CC-BY-4.0-xmlstyle"), "<[^!]")
        .skipLinesMatching("""<\?.*\?>""")
    prettier(
            mapOf(
                "prettier" to PRETTIER_DEFAULT_VERSION,
                "@prettier/plugin-xml" to libs.versions.prettier.xml.get()))
        .npmInstallCache()
        .nodeExecutable(computeNodeExec(node, computeNodeDir(node)).get())
        .config(
            mapOf("parser" to "xml", "printWidth" to 100, "xmlWhitespaceSensitivity" to "ignore"))
  }
}

tasks.named("spotlessMarkdown") { dependsOn(tasks.npmSetup) }

tasks.named("spotlessToml") { dependsOn(tasks.npmSetup) }

tasks.named("spotlessYaml") { dependsOn(tasks.npmSetup) }

tasks.named("spotlessXml") { dependsOn(tasks.npmSetup) }

val kdocJar: TaskProvider<Jar> by
    tasks.registering(Jar::class) {
      group = DOCUMENTATION_GROUP
      archiveClassifier = "kdoc"
      from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    }

val kdoc: Configuration by
    configurations.creating {
      isCanBeConsumed = true
      isCanBeResolved = false
    }

artifacts { add(kdoc.name, kdocJar) }

val javadocJar: TaskProvider<Jar> by
    tasks.registering(Jar::class) {
      group = DOCUMENTATION_GROUP
      archiveClassifier = "javadoc"
      from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    }

java {
  withJavadocJar()
  withSourcesJar()
}

kotlin { jvmToolchain(libs.versions.java.jdk.get().toInt()) }

application {
  mainClass = "tools.aqua.rage.CliKt"
  applicationDefaultJvmArgs += listOf("-Xmx16g")
}

runtime {
  modules = listOf("java.base", "java.logging", "java.management", "java.xml")
  jpackage {
    val versionFragments =
        project.version.toString().split('-', limit = 2).first().split('.').map { it.toInt() }
    val appVersionFragments =
        listOf(versionFragments.first().coerceAtLeast(1)) +
            versionFragments.subList(1, versionFragments.size).take(2)
    appVersion = appVersionFragments.joinToString(".")
    if (getCurrentOperatingSystem().isWindows) installerOptions = listOf("--win-console")
  }
}

tasks.test {
  useJUnitPlatform()
  testLogging { events(FAILED, SKIPPED, PASSED) }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name = "RAGe"
        description = "A generator for Register Automata"
        url = "https://github.com/tudo-aqua/konstraints"

        licenses {
          license {
            name = "Apache-2.0"
            url = "https://www.apache.org/licenses/LICENSE-2.0"
          }
          license {
            name = "CC-BY-4.0"
            url = "https://creativecommons.org/licenses/by/4.0/"
          }
        }

        developers {
          developer {
            name = "Simon Dierl"
            email = "simon.dierl@tu-dortmund.de"
            organization = "TU Dortmund University"
          }
          developer {
            name = "Falk Howar"
            email = "falk.howar@tu-dortmund.de"
            organization = "TU Dortmund University"
          }
        }

        scm {
          url = "https://github.com/tudo-aqua/rage/tree/main"
          connection = "scm:git:git://github.com:tudo-aqua/rage.git"
          developerConnection = "scm:git:ssh://git@github.com:tudo-aqua/rage.git"
        }
      }
    }
  }
}

signing {
  setRequired { gradle.taskGraph.allTasks.any { it.group == PUBLISH_TASK_GROUP } }
  useGpgCmd()
  sign(publishing.publications["maven"])
}

nexusPublishing { this.repositories { sonatype() } }
