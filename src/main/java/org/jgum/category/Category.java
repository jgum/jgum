package org.jgum.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * A hierarchical category associated with named properties.
 * @author sergioc
 *
 */
public class Category {

	private final Map<Object, Object> properties; //properties associated with this category are backed up in this map.
	private Categorization categorization; //the categorization where this category exists.
	private final List<? extends Category> parents; //default placeholder for the parents of this category. Subclasses may choose to store parents in a different structure.
	private final List<? extends Category> children; //default placeholder for the children of this category. Subclasses may choose to store children in a different structure.
	private List<? extends Category> bottomUpLinearization; //lazily initialized bottom-up linearization
	
	/**
	 * @param categorization the categorization where this category exists.
	 */
	public Category(Categorization<?> categorization) {
		this(new ArrayList());
		setCategorization(categorization);
	}
	
	/**
	 * @parem parents the parents of this category.
	 */
	public Category(List<? extends Category> parents) {
		this.parents = parents;
		children = new ArrayList<>();
		properties = new HashMap<>();
	}
	
	
	private void setCategorization(Categorization categorization) {
		this.categorization = categorization;
		categorization.setRoot(this);
	}

	/**
	 * 
	 * @return the categorization where this category exists.
	 */
	public Categorization<?> getCategorization() {
		if(categorization == null) //implies that this is not the root category
			categorization = getParents().get(0).getCategorization(); //there is at least one parent 
		return categorization;
	}

	/**
	 * @param property a property name.
	 * @return a category property.
	 */
	public CategoryProperty<?> getProperty(Object property) {
		return new CategoryProperty<>(this, property);
	}
	
	/**
	 * 
	 * @param property a property name.
	 * @return true if the property is defined in the category. false otherwise. It attempts to find it in ancestor categories if the property is not locally present.
	 */
	public boolean containsProperty(Object property) {
		return getProperty(property).isPresent();
	}
	
	/**
	 * @param property a property name.
	 * @return an optional with the property value in the current category (if any). It does not query ancestor categories if the property is not locally present.
	 */
	public Optional<?> getLocalProperty(Object property) {
		return Optional.fromNullable(properties.get(property));
	}
	
	/**
	 * 
	 * @param property a property name.
	 * @return true if the property exists in the current category. false otherwise. It does not query ancestor categories if the property is not locally present.
	 */
	public boolean containsLocalProperty(Object property) {
		return properties.containsKey(property);
	}
	
	/**
	 * Set a property to a given value.
	 * @param property the property name to set.
	 * @param value the value of the property.
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
		Object currentPropertyValue = getLocalProperty(property);
		if(currentPropertyValue!=null && !canOverride)
			throw new RuntimeException("The node already has a label for the property \"" + property + "\":" + currentPropertyValue +
				". Attempting to override this property with: " + value + ".");
		else
			setProperty(property, value);
	}
	
	/**
	 * 
	 * @param linearizationFunction is a linearization function.
	 * @return A list of categories, according to the given linearization function.
	 */
	public <U extends Category> List<U> linearize(Function<U,List<U>> linearizationFunction) {
		return linearizationFunction.apply((U)this);
	}
	
	/**
	 * 
	 * @return an optional with the super category.
	 */
	public <U extends Category> Optional<U> getSuper() {
		List bottomUpLinearization = bottomUpLinearization();
		if(bottomUpLinearization.size() == 1) //there are no super categories (according to the default linearization function)
			return Optional.absent();
		else
			return Optional.of((U)bottomUpLinearization.get(1));
	}
	
	/**
	 * 
	 * @return a linearization using the default bottom-up linearization function.
	 */
	public <U extends Category> List<U> bottomUpLinearization() {
		if(bottomUpLinearization == null) {
			bottomUpLinearization = linearize(getCategorization().getBottomUpLinearizationFunction());
		}
		return (List<U>)bottomUpLinearization;
	}

	/**
	 * 
	 * @return a linearization using the default top-down linearization function.
	 */
	public <U extends Category> List<U> topDownLinearization() {
		return (List<U>)linearize(getCategorization().getTopDownLinearizationFunction());
	}
	
	/**
	 * 
	 * @return the parents of this category. The ordering in which parents are returned is determined by subclasses.
	 */
	public <U extends Category> List<U> getParents() {
		return (List)parents;
	}

	/**
	 * 
	 * @return the children of this category. The ordering in which children are returned is determined by subclasses.
	 */
	public <U extends Category> List<U> getChildren() {
		return (List)children;
	}

//	protected void onAddChild(Category category) {
//		categorization.notifyCategorizationListeners(category);
//	}
	
	/**
	 * 
	 * @return true if the current category corresponds to the root category. false otherwise.
	 */
	public boolean isRoot() {
		return getParents().isEmpty();
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
