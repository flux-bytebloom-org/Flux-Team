package architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withOperatorModifier
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

class CleanArchitectureTest {

    @Test
    fun `domain layer should remain independent from data layer`() {
        Konsist.scopeFromProject().assertArchitecture {
            val domain = Layer("Domain", "..domain..")
            val data = Layer("Data", "..data..")
            domain.dependsOnNothing()
            data.dependsOn(domain)
        }
    }

    @Test
    fun `domain layer should not import data layer or external libraries`() {
        Konsist.scopeFromProduction().files.withPackage("..domain..")
            .assertFalse { file ->
                file.imports.any { import ->isForbiddenImport(import.name) }
            }
    }

    @Test
    fun`classes in usecase package should end with UseCase suffix`(){
        Konsist.scopeFromProduction().classes()
            .withPackage("..domain.usecase..")
            .assertTrue { it.hasNameEndingWith("UseCase") }

    }

    @Test
    fun `all classes in domain validator package should end with Validator`(){
        Konsist.scopeFromProduction().classes()
            .withPackage("..domain.validator..")
            .assertTrue { it.hasNameEndingWith("Validator") }

    }

    @Test
    fun `every UseCase class must define a public operator invoke function`(){
        Konsist.scopeFromProduction().classes().withPackage("..domain.usecase..")
            .assertTrue { useCaseClass ->
                useCaseClass.functions().withOperatorModifier().any { it.name == "invoke" }
            }

    }

    @Test
    fun `all DTO classes in data remote dto package should end with Dto and be annotated with Serializable`(){
        Konsist.scopeFromProduction()
            .classes()
            .withPackage("..data.remote.dto..")
            .assertTrue { dtoClass ->
                dtoClass.hasNameEndingWith("Dto") && dtoClass.hasAnnotationOf(Serializable::class)
            }
    }


    private fun isForbiddenImport(importName: String): Boolean{
        val allowedPrefixes = listOf(
            "kotlin.",
            "org.byte_bloom.flux.domain."
        )
        return allowedPrefixes.none{allowed -> importName.startsWith(allowed)}

    }
}