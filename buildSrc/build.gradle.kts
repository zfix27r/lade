import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Сборка вспомогательного модуля buildSrc.
// Здесь компилируется ArchGuard.kt, а его классы становятся доступны
// во всех build-скриптах проекта (root, app, feed, database).
plugins {
	kotlin("jvm") version "2.2.10"
}

// Фиксируем единый JVM-таргет: от запускающего JDK (JBR 25, JRE 8 и т.д.)
// не должны зависеть совместимость Java/Kotlin задач buildSrc.
java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
	}
}