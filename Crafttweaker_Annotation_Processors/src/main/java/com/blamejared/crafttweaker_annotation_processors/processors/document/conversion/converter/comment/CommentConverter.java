package com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.comment;

import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.comment.example.ExampleDataConverter;
import com.blamejared.crafttweaker_annotation_processors.processors.document.conversion.converter.member.header.ParameterDescriptionConverter;
import com.blamejared.crafttweaker_annotation_processors.processors.document.page.comment.DocumentationComment;
import com.blamejared.crafttweaker_annotation_processors.processors.document.page.info.DocumentationPageInfo;
import com.blamejared.crafttweaker_annotation_processors.processors.document.page.member.header.examples.ExampleData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

public class CommentConverter {
    
    private final ProcessingEnvironment processingEnv;
    private final CommentMerger commentMerger;
    private final ExampleDataConverter exampleDataConverter;
    private final DescriptionConverter descriptionConverter;
    private final ParameterDescriptionConverter parameterDescriptionConverter;
    
    public CommentConverter(ProcessingEnvironment processingEnv, CommentMerger commentMerger, ExampleDataConverter exampleDataConverter, DescriptionConverter descriptionConverter, ParameterDescriptionConverter parameterDescriptionConverter) {
        
        this.processingEnv = processingEnv;
        this.commentMerger = commentMerger;
        this.exampleDataConverter = exampleDataConverter;
        this.descriptionConverter = descriptionConverter;
        this.parameterDescriptionConverter = parameterDescriptionConverter;
    }
    
    public DocumentationComment convertForType(TypeElement parentElement, TypeElement typeElement) {
        
        return convertElement(parentElement, typeElement);
    }
    
    public DocumentationComment convertForConstructor(TypeElement parentElement, ExecutableElement constructor, DocumentationPageInfo pageInfo) {
        
        final DocumentationComment comment = convertElement(parentElement, constructor);
        fillExampleForThisParameterFromPageInfo(comment, pageInfo);
        return comment;
    }
    
    public DocumentationComment convertForMethod(TypeElement parentElement, ExecutableElement method, DocumentationPageInfo pageInfo) {
        
        final DocumentationComment comment = convertElement(parentElement, method);
        return fillExampleForThisParameterFromPageInfo(comment, pageInfo);
    }
    
    public DocumentationComment convertForParameter(TypeElement parentElement, VariableElement variableElement) {
        
        final DocumentationComment parameterDescription = convertParameterDescription(variableElement);
        final DocumentationComment comment = convertElement(parentElement, variableElement.getEnclosingElement());
        return mergeComments(parameterDescription, comment);
    }
    
    private DocumentationComment convertParameterDescription(Element element) {
        
        return parameterDescriptionConverter.convertDescriptionOf(element);
    }
    
    public DocumentationComment convertForTypeParameter(TypeElement parentElement, TypeParameterElement typeParameterElement) {
        
        final DocumentationComment parameterDescription = convertParameterDescription(typeParameterElement);
        final DocumentationComment comment = convertElement(parentElement, typeParameterElement.getEnclosingElement());
        return mergeComments(parameterDescription, comment);
    }
    
    private DocumentationComment convertElement(TypeElement parentElement, Element element) {
        
        final DocumentationComment comment = getCommentForElement(element);
        final DocumentationComment enclosingElementComment = getCommentFromEnclosingElement(parentElement, parentElement); // TODO
        return mergeComments(comment, enclosingElementComment);
    }
    
    @Nonnull
    private DocumentationComment getCommentForElement(Element element) {
        
        final String docComment = processingEnv.getElementUtils().getDocComment(element);
        final String description = extractDescriptionFrom(docComment, element);
        final ExampleData exampleData = extractExampleDataFrom(docComment, element);
        
        return new DocumentationComment(description, exampleData);
    }
    
    @Nullable
    private String extractDescriptionFrom(@Nullable String docComment, Element element) {
        
        return descriptionConverter.convertFromCommentString(docComment, element);
    }
    
    private ExampleData extractExampleDataFrom(String docComment, Element element) {
        
        return exampleDataConverter.convertFromCommentString(docComment, element);
    }
    
    private DocumentationComment getCommentFromEnclosingElement(TypeElement parentElement, Element element) {
    
        if(element == null) {
            return DocumentationComment.empty();
        }
        
        Element enclosingElement = element.getEnclosingElement();
        
        if(parentElement != null) {
            enclosingElement = parentElement.getEnclosingElement();
        }
        
        if(enclosingElement == null) {
            return DocumentationComment.empty();
        }
        
        return convertElement(null, enclosingElement);
    }
    
    private DocumentationComment mergeComments(DocumentationComment comment, DocumentationComment enclosingElementComment) {
        
        return commentMerger.merge(comment, enclosingElementComment);
    }
    
    private DocumentationComment fillExampleForThisParameterFromPageInfo(DocumentationComment comment, DocumentationPageInfo pageInfo) {
        
        final DocumentationComment classComment = pageInfo.getClassComment();
        return mergeComments(comment, classComment);
    }
    
}
