package org.jgum.category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A hierarchical category associated with some arbitrary named properties.
 * @author sergioc
 *
 */
public abstract class Category {

	private final Map<Object, Object> properties; //properties associated with this category are backed up in this map.
	private final Categorization<?> categorization; //the hierarchy where this category exists.
	
	
	/**
	 * @param categorization the hierarchy where this category exists.
	 * @param label a label identifying this category.
	 */
	public Category(Categorization<?> categorization) {
		this.categorization = categorization;
		properties = new HashMap<>();
	}

	public Categorization<?> getCategoryHierarchy() {
		return categorization;
	}

	
	/**
	 * @param property a property name.
	 * @return the label of the property in the current node (if any).
	 * @see Map#get(Object)
	 */
	public Object getProperty(Object property) {
		return properties.get(property);
	}
	
	/**
	 * 
	 * @param property a property.
	 * @return true if the property exists. false otherwise.
	 */
	public boolean containsProperty(Object property) {
		return properties.containsKey(property);
	}
	
	/**
	 * Set a property to a given value.
	 * @param property the property to set.
	 * @param value the label of the property.
	 */
	public void setProperty(Object property, Object value) {
		properties.put(property, value);
	}
	
	/**
	 * Set a property to a given value. Rises an exception if the property is already set and the canOverride parameter is false.
	 * @param property the property to set.
	 * @param value the label of the property.
	 * @param canOverride a boolean indicating if the property can be overridden or not. 
	 * @throws RuntimeException if the property exists and it cannot be overridden.
	 */
	public void setProperty(Object property, Object value, boolean canOverride) {
		Object currentPropertyValue = getProperty(property);
		if(currentPropertyValue!=null && !canOverride)
			throw new RuntimeException("The node already has a label for the property \"" + property + "\":" + currentPropertyValue +
				". Attempting to override this property with: " + value + ".");
		else
			setProperty(property, value);
	}
	
	/**
	 * 
	 * @param linearizationFunction is a linearization function.
	 * @return An iterable of nodes, according to the given linearization function.
	 */
	public <U extends Category> FluentIterable<U> linearize(Function<U,FluentIterable<U>> linearizationFunction) {
		return linearizationFunction.apply((U)this);
	}
	
	/**
	 * 
	 * @return a linearization using the default bottom up linearization function.
	 */
	public <U extends Category> FluentIterable<U> bottomUpLinearization() {
		return (FluentIterable<U>)linearize(categorization.getBottomUpLinearizationFunction());
	}

	public abstract <U extends Category> List<U> getParents();

	public abstract <U extends Category> List<U> getChildren();
	
	/**
	 * 
	 * @return a linearization using the default top down linearization function.
	 */
	public <U extends Category> FluentIterable<U> topDownLinearization() {
		return (FluentIterable<U>)linearize(categorization.getTopDownLinearizationFunction());
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
