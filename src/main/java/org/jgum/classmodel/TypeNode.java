package org.jgum.classmodel;

import static org.jgum.JGum.DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY;

import java.util.Collections;
import java.util.List;

import org.jgum.JGum;
import org.jgum.graph.Node;
import org.jgum.graph.PropertyIterable;

import com.google.common.collect.FluentIterable;

public abstract class TypeNode<T> extends Node {
	
	protected Class<T> wrappedClazz;
	private List<InterfaceNode<? super T>> superInterfaceNodes;

	public TypeNode(JGum context, Class<T> wrappedClazz) {
		this(context, wrappedClazz, Collections.<InterfaceNode<? super T>>emptyList());
	}
	
	public TypeNode(JGum context, Class<T> wrappedClazz, List<InterfaceNode<? super T>> superInterfaceNodes) {
		super(context);
		this.wrappedClazz = wrappedClazz;
		setSuperInterfaceNodes(superInterfaceNodes);
	}
	
	public Class<T> getWrappedClass() {
		return wrappedClazz;
	}
	
	public List<InterfaceNode<? super T>> getSuperInterfaceNodes() {
		return superInterfaceNodes;
	}
	
	protected void setSuperInterfaceNodes(List<InterfaceNode<? super T>> superInterfaceNodes) {
		this.superInterfaceNodes = superInterfaceNodes;
	}

	public FluentIterable<ClassNode<? super T>> getAncestorClasses() {
		return (FluentIterable)path(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY).skip(1).filter(ClassNode.class);
	}
	
	public FluentIterable<InterfaceNode<? super T>> getAncestorInterfaces() {
		return (FluentIterable)path(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY).skip(1).filter(InterfaceNode.class);
	}
	
	public <U extends TypeNode<?>> FluentIterable<U> bottomUpPath() {
		return path((BottomUpTypeTraversalPolicy)getContext().getBottomUpTypeTraversalPolicy());
	}
	
	public <U extends TypeNode<?>> FluentIterable<U> topDownPath() {
		return path((TopDownTypeTraversalPolicy)getContext().getTopDownTypeTraversalPolicy());
	}
	
	@Override
	public <U> FluentIterable<U> propertyInHierarchy(Object key) {
		return PropertyIterable.<U>properties(bottomUpPath(), key);
	}
	
	@Override
	public String toString() {
		return wrappedClazz.getName() + super.toString();
	}
	
	protected abstract <U extends TypeNode<? super T>> List<U> getParents(Priority priority, InterfaceOrder interfaceOrder);

	protected abstract <U extends TypeNode<? extends T>> List<U> getChildren(Priority priority);
	
}
