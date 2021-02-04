package com.blamejared.crafttweaker_annotation_processors.processors.document.conversion;

import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.DocumentConversionRegistry;
import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.element.KnownElementList;

import javax.lang.model.element.TypeElement;
import java.util.Collection;

public class ElementConverter {
    
    private final DocumentConversionRegistry conversionRegistry;
    
    public ElementConverter(DocumentConversionRegistry conversionRegistry) {
        this.conversionRegistry = conversionRegistry;
    }
    
    public void handleElements(TypeElement parentElement, KnownElementList knownElementList) {
        prepareElements(parentElement,knownElementList);
        convertElements(parentElement,knownElementList);
    }
    
    private void prepareElements(TypeElement parentElement, KnownElementList knownElementList) {
        final Collection<TypeElement> elementsForTypeDocumentation = knownElementList.getElementsForTypeDocumentation();
        final Collection<TypeElement> elementsForExpansionDocumentation = knownElementList.getElementsForExpansionDocumentation();
        
        prepareDocumentation(parentElement, elementsForTypeDocumentation);
        prepareDocumentation(parentElement, elementsForExpansionDocumentation);
    }
    
    private void convertElements(TypeElement parentElement, KnownElementList knownElementList) {
        final Collection<TypeElement> elementsForTypeDocumentation = knownElementList.getElementsForTypeDocumentation();
        final Collection<TypeElement> elementsForExpansionDocumentation = knownElementList.getElementsForExpansionDocumentation();
        
        handleDocumentation(parentElement,elementsForTypeDocumentation);
        handleDocumentation(parentElement,elementsForExpansionDocumentation);
    }
    
    private void prepareDocumentation(TypeElement parentElement, Collection<TypeElement> elementsToPrepare) {
        for(TypeElement typeElement : elementsToPrepare) {
            conversionRegistry.prepareTypePageFor(typeElement);
        }
        for(TypeElement typeElement : elementsToPrepare) {
            conversionRegistry.setCommentInfoFor(parentElement, typeElement);
        }
    }
    
    private void handleDocumentation(TypeElement parentElement, Collection<TypeElement> elementsForExpansionDocumentation) {
        for(TypeElement typeElement : elementsForExpansionDocumentation) {
            conversionRegistry.convert(parentElement,typeElement);
        }
    }
    
    
}
