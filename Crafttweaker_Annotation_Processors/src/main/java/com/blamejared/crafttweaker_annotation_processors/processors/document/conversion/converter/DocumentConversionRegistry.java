package com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter;

import com.blamejared.crafttweaker_annotation_processors.processors.document.DocumentRegistry;
import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.expansion.ExpansionConverter;
import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.named_type.NamedTypeConverter;
import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.native_registration.NativeRegistrationConverter;
import com.blamejared.crafttweaker_annotation_processors.processors.util.dependencies.DependencyContainer;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DocumentConversionRegistry {
    
    private final List<DocumentConverter> converters = new ArrayList<>();
    private final DocumentRegistry documentRegistry;
    
    public DocumentConversionRegistry(DocumentRegistry documentRegistry, DependencyContainer dependencyContainer) {
        this.documentRegistry = documentRegistry;
        //
        // TODO: Add converters
        converters.add(dependencyContainer.getInstanceOfClass(NativeRegistrationConverter.class));
        converters.add(dependencyContainer.getInstanceOfClass(ExpansionConverter.class));
        converters.add(dependencyContainer.getInstanceOfClass(NamedTypeConverter.class));
    }
    
    public void prepareTypePageFor(TypeElement typeElement) {
        setPageInfo(typeElement);
    }
    
    public void setCommentInfoFor(TypeElement parentElement, TypeElement typeElement) {
        setCommentData(parentElement, typeElement);
    }
    
    public void convert(TypeElement parentElement, TypeElement typeElement) {
        convertAndAddToRegistry(parentElement,typeElement);
    }
    
    private void convertAndAddToRegistry(TypeElement parentElement, TypeElement typeElement) {
        converters.stream()
                .filter(converter -> documentRegistry.hasPageInfoFor(typeElement))
                .filter(converter -> converter.canConvert(typeElement))
                .map(converter -> converter.convert(parentElement,typeElement, documentRegistry.getPageInfoFor(typeElement)))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(documentRegistry::addDocumentationPage);
    }
    
    private void setCommentData(TypeElement parentElement, TypeElement typeElement) {
        converters.stream()
                .filter(converter -> documentRegistry.hasPageInfoFor(typeElement))
                .filter(converter -> converter.canConvert(typeElement))
                .forEach(setCommentInfo(parentElement, typeElement));
        
    }
    
    @Nonnull
    private Consumer<DocumentConverter> setCommentInfo(TypeElement parentElement, TypeElement typeElement) {
        return converter -> converter.setDocumentationCommentTo(parentElement, typeElement, documentRegistry.getPageInfoFor(typeElement));
    }
    
    private void setPageInfo(TypeElement typeElement) {
        converters.stream()
                .filter(converter -> converter.canConvert(typeElement))
                .map(converter -> converter.prepareConversion(typeElement))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(pageInfo -> documentRegistry.addInfo(typeElement, pageInfo));
    }
}
