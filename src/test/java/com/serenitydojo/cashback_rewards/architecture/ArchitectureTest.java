package com.serenitydojo.cashback_rewards.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.serenitydojo.cashback_rewards",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainHasNoSpringDependency = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .because("the domain must remain free of Spring framework dependencies")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainHasNoJpaDependency = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
            .because("the domain must remain free of persistence framework dependencies")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule layeredArchitectureIsRespected = Architectures.layeredArchitecture()
            .consideringAllDependencies()
            .withOptionalLayers(true)
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Adapter").definedBy("..adapter..")
            .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter")
            .because("dependencies must flow inward: adapter -> application -> domain");
}