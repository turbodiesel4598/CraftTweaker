package com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.member;

import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.element.KnownElementList;
import com.blamejared.crafttweaker_annotation_processors.processors.document.page.info.DocumentationPageInfo;
import com.sun.tools.javac.code.Type;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public abstract class MemberConverter<T> {
    
    private final EnumMap<ElementKind, List<AbstractEnclosedElementConverter<T>>> elementConverters = new EnumMap<>(ElementKind.class);
    
    protected abstract boolean isCandidate(Element enclosedElement);
    
    protected abstract T createResultObject(DocumentationPageInfo pageInfo);
    
    public T convertFor(KnownElementList knownElements, TypeElement typeElement, DocumentationPageInfo pageInfo) {
        
        final T result = createResultObject(pageInfo);
        for(Element enclosedElement : typeElement.getEnclosedElements()) {
            convertMemberFor(null,enclosedElement, result, pageInfo);
        }
        ArrayList<TypeElement> interfaces = new ArrayList<>();
        traverseInterfaces(knownElements, typeElement, interfaces);
        for(TypeElement element : interfaces) {
            for(Element enclosedElement : element.getEnclosedElements()) {
    
                convertMemberFor(typeElement, enclosedElement, result, pageInfo);
            }
        }
        
        return result;
    }
    
    private void traverseInterfaces(KnownElementList knownElements, TypeElement element, List<TypeElement> result) {
        
        for(TypeMirror anInterface : element.getInterfaces()) {
            Type.ClassType classType = (Type.ClassType) anInterface;
            String name = classType.tsym.getQualifiedName().toString();
            knownElements.getElement(name).ifPresent(typeElement1 -> {
                result.add(typeElement1);
                traverseInterfaces(knownElements, typeElement1, result);
            });
            
        }
    }
    
    private void convertMemberFor(TypeElement parentElement, Element enclosedElement, T result, DocumentationPageInfo pageInfo) {
        
        if(!isCandidate(enclosedElement)) {
            return;
        }
        
        final ElementKind kind = enclosedElement.getKind();
        final List<AbstractEnclosedElementConverter<T>> converters = getConvertersFor(kind);
        for(AbstractEnclosedElementConverter<T> converter : converters) {
            if(converter.canConvert(enclosedElement)) {
                converter.convertAndAddTo(parentElement, enclosedElement, result, pageInfo);
            }
        }
    }
    
    
    private List<AbstractEnclosedElementConverter<T>> getConvertersFor(ElementKind kind) {
        
        return elementConverters.getOrDefault(kind, Collections.emptyList());
    }
    
    protected void addElementConverter(ElementKind kind, AbstractEnclosedElementConverter<T> expansionMethodConverter) {
        
        elementConverters.computeIfAbsent(kind, ignored -> new ArrayList<>())
                .add(expansionMethodConverter);
    }
    
}
