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
    static final ArchRule domain_must_not_depend_on_spring =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                    .because("Domain must be free of Spring framework dependencies")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_must_not_depend_on_jpa =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
                    .because("Domain must be free of persistence framework dependencies")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule layered_architecture_is_respected =
            Architectures.layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Adapter").definedBy("..adapter..")
                    .layer("Application").definedBy("..application..")
                    .layer("Domain").definedBy("..domain..")
                    .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Adapter", "Application")
                    .withOptionalLayers(true);
}