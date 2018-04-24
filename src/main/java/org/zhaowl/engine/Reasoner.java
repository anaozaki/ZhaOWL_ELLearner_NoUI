package org.zhaowl.engine;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.zhaowl.tree.ClassHierarchyT;
import org.zhaowl.tree.ObjectPropertyHierarchyT;
 

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public    class Reasoner  {
	
	public static final NumberFormat numberFormat = NumberFormat.getInstance();
	 
	public boolean useInstanceChecks = false;

	// statistical data for particular reasoning operations
	private final long instanceCheckReasoningTimeNs = 0;
	private final int nrOfInstanceChecks = 0;
	public int nrOfMultiInstanceChecks = 0;
	private final long retrievalReasoningTimeNs = 0;
	private final int nrOfRetrievals = 0;
	private long subsumptionReasoningTimeNs = 0;
	private int nrOfSubsumptionChecks = 0;
	public int nrOfMultiSubsumptionChecks = 0;
	private final int nrOfSubsumptionHierarchyQueries = 0;

	// rest of reasoning time
	public long otherReasoningTimeNs = 0;

	// time for all reasoning requests (usually longer than the sum of all
	// above)
	private long overallReasoningTimeNs = 0;

	// temporary variables (moved here for performance reasons)
	private long reasoningStartTimeTmp;
	private long reasoningDurationTmp;

	// list view
	public List<OWLClass> atomicConceptsList;
	public List<OWLObjectProperty> atomicRolesList;

	// hierarchies (they are computed the first time they are needed)
	 
	private OWLReasoner reasoner;
	private ClassHierarchyT subsumptionHierarchy = null;
	private ObjectPropertyHierarchyT roleHierarchy = null;
	 
 
	private final boolean precomputeClassHierarchy = true;
	public boolean precomputeObjectPropertyHierarchy = true; 
	public boolean precomputeDataPropertyHierarchy = true;
	
	private OWLDataFactory df = new OWLDataFactoryImpl();
	private OWLOntology ontology;
	//public Multimap<OWLDatatype, OWLDataProperty> datatype2Properties = HashMultimap.create();
	public Map<OWLDataProperty, OWLDatatype> dataproperty2datatype = new HashMap<>();

	public boolean precomputePropertyDomains = true;
	public Map<OWLProperty, OWLClassExpression> propertyDomains = new HashMap<>();

	 
	public boolean precomputeObjectPropertyRanges = true;
	public Map<OWLObjectProperty, OWLClassExpression> objectPropertyRanges = new HashMap<>();

	 

    public Reasoner(){

    }
    public Reasoner(OWLDataFactory df, OWLOntology onto, OWLReasoner reasoner){
    	this.df = df;  
    	this.ontology = onto;
    	this.reasoner = reasoner;
    }
 

	/**
	 * Gets the type of the underlying reasoner. Although rarely necessary,
	 * applications can use this to adapt their behaviour to the reasoner.
	 * 
	 * @return The reasoner type.
	 */
	 
 

	/**
	 * Notify the reasoner component that the underlying knowledge base has
	 * changed and all caches (for named classes, subsumption hierarchies, etc.)
	 * should be invalidaded. TODO Currently, nothing is done to behave
	 * correctly after updates.
	 */
	 public void setUpdated() {
		// TODO currently, nothing is done to behave correctly after updates
	}

	/**
	 * Call this method to release the knowledge base. Not calling the method
	 * may (depending on the underlying reasoner) result in resources for this
	 * knowledge base not being freed, which can cause memory leaks.
	 */ 

	// we cannot expect callers of reasoning methods to reliably recover if
	// certain reasoning methods are not implemented by the backend; we also
	// should not require callers to build catch clauses each time they make
	// a reasoner request => for this reasoner, we throw a runtime exception
	// here
	private void handleExceptions(ReasoningMethodUnsupportedException e) {
		e.printStackTrace();
		throw new RuntimeException("Reasoning method not supported.", e);
	}

 

	public final boolean isSuperClassOf(OWLClassExpression superClass, OWLClassExpression subClass) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result = false;
		if(precomputeClassHierarchy) {
			if(superClass.isAnonymous() || subClass.isAnonymous()) {
				try {
					result = isSuperClassOfImpl(superClass, subClass);
				} catch (ReasoningMethodUnsupportedException e) {
					e.printStackTrace();
				}
			} else {
				return getClassHierarchy().isSubclassOf(subClass, superClass);
			}
		} else {
			try {
				result = isSuperClassOfImpl(superClass, subClass);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		nrOfSubsumptionChecks++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		subsumptionReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		
		return result;
	}

	private boolean isSuperClassOfImpl(OWLClassExpression superConcept, OWLClassExpression subConcept)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	public final boolean isEquivalentClass(OWLClassExpression class1, OWLClassExpression class2) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result = false;
		try {
			result = isEquivalentClassImpl(class1, class2);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		nrOfSubsumptionChecks+=2;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		subsumptionReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		
		return result;
	}

	private boolean isEquivalentClassImpl(OWLClassExpression class1, OWLClassExpression class2) throws ReasoningMethodUnsupportedException {
		return isSuperClassOfImpl(class1,class2) && isSuperClassOfImpl(class2,class1);
	}	
	
 
	public boolean isDisjointImpl(OWLClass superConcept, OWLClass subConcept)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
 
	
	public Set<OWLClassExpression> getAssertedDefinitionsImpl(OWLClass namedClass)
		throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
 
	public SortedSet<OWLIndividual> getIndividualsImpl(OWLClassExpression concept)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	 

 

	public boolean hasTypeImpl(OWLClassExpression concept, OWLIndividual individual)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

 

	public Set<OWLClass> getInconsistentClassesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

 

	public boolean remainsSatisfiableImpl(OWLAxiom axiom) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
 
	
	public Map<OWLObjectProperty,Set<OWLIndividual>> getObjectPropertyRelationshipsImpl(OWLIndividual individual) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
 
 
			
 
 
 

 
 

 

	public Map<OWLIndividual, SortedSet<OWLIndividual>> getPropertyMembersImpl(
			OWLObjectProperty atomicRole) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

 
 
	
	public final Set<OWLObjectProperty> getObjectProperties() {
		try {
			return getObjectPropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	private Set<OWLObjectProperty> getObjectPropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	public final Set<OWLDataProperty> getDatatypeProperties() {
		try {
			return getDatatypePropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	private Set<OWLDataProperty> getDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

 

	// TODO Even if there is a small performance penalty, we could implement
	// the method right here by iterating over all data properties and
	// querying their ranges. At least, this should be done once we have a
	// reasoner independent of OWL API with datatype support.
	public Set<OWLDataProperty> getBooleanDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
 
 
	public  NodeSet<OWLClass> getSuperClasses(OWLClassExpression concept) { 
			return reasoner.getSuperClasses(concept, true);
		 
	}

 
	
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression concept) {
		return reasoner.getSubClasses(concept, true);
		
	}
 
	private <T extends OWLProperty<?, ?>> SortedSet<T> getSuperPropertiesImpl(T role) throws ReasoningMethodUnsupportedException {
		if(OWLObjectProperty.class.isInstance(role)) {
			// WARNING: unchecked cast!
			return (SortedSet<T>) getSuperPropertiesImpl((OWLObjectProperty) role);
		} 
		//else 
		//	if(OWLDataProperty.class.isInstance(role)) {
	//		return (SortedSet<T>) getSuperPropertiesImpl((OWLDataProperty) role);
	//	}
		throw new ReasoningMethodUnsupportedException();
	}
 
	 
	private <T extends OWLProperty> SortedSet<T> getSubPropertiesImpl(T role) throws ReasoningMethodUnsupportedException {
		if(OWLObjectProperty.class.isInstance(role)) {
			// WARNING: unchecked cast!
			return (SortedSet<T>) getSubPropertiesImpl((OWLObjectProperty) role);
		}  
		throw new ReasoningMethodUnsupportedException();
	}
 
	/**
	 * Creates the class hierarchy. Invoking this method is optional (if not
	 * called explicitly, it is called the first time, it is needed).
	 * 
	 * @return The class hierarchy.
	 * @throws ReasoningMethodUnsupportedException If any method needed to
	 * create the hierarchy is not supported by the underlying reasoner.
	 */
	private ClassHierarchyT prepareSubsumptionHierarchy() {
		TreeMap<OWLClassExpression, SortedSet<OWLClassExpression>> subsumptionHierarchyUp = new TreeMap<>(
		);
		TreeMap<OWLClassExpression, SortedSet<OWLClassExpression>> subsumptionHierarchyDown = new TreeMap<>(
		);

		// parents/children of top ...
		Set<OWLClass> tmp = reasoner.getSubClasses(df.getOWLThing(),true).getFlattened();
		System.out.println("The subs are: " + tmp);
		for (OWLClass aTmp : tmp) System.out.println("Inside loop: " + aTmp);
			
		SortedSet<OWLClassExpression> tmp2 = null;
		tmp2.addAll(tmp);
		
		System.out.println("Sorted: " + tmp2);
		
		subsumptionHierarchyUp.put(df.getOWLThing(), new TreeSet<>());
		subsumptionHierarchyDown.put(df.getOWLThing(), tmp2);
		
		
		// ... bottom ...
		tmp = reasoner.getSuperClasses(df.getOWLThing(),true).getFlattened();
		tmp2 = null;
		tmp2.addAll(tmp);
		subsumptionHierarchyUp.put(df.getOWLNothing(), tmp2);
		subsumptionHierarchyDown.put(df.getOWLNothing(), new TreeSet<>());
		
		// ... and named classes
		Set<OWLClass> atomicConcepts = ontology.getClassesInSignature();
		for (OWLClass atom : atomicConcepts) {
			tmp = reasoner.getSubClasses(atom, true).getFlattened();
			// quality control: we explicitly check that no reasoner implementation returns null here
			tmp2 = null;
			tmp2.addAll(tmp);
			
			if(tmp == null) {
				System.out.println("Class hierarchy: getSubClasses returned null instead of empty set."); 
			}			
			subsumptionHierarchyDown.put(atom, tmp2);

			tmp = reasoner.getSuperClasses(atom, true).getFlattened();
			// quality control: we explicitly check that no reasoner implementation returns null here
			if(tmp == null) {
				System.out.println("Class hierarchy: getSuperClasses returned null instead of empty set."); 
			}	
			tmp2 = null;
			tmp2.addAll(tmp);
			subsumptionHierarchyUp.put(atom, tmp2);
		}		

		 return new ClassHierarchyT(subsumptionHierarchyUp, subsumptionHierarchyDown);
	}
 
	public   ClassHierarchyT getClassHierarchy() {
		// class hierarchy is created on first invocation
		if (subsumptionHierarchy == null) {
				subsumptionHierarchy = prepareSubsumptionHierarchy();
		}
		return subsumptionHierarchy;
	}

	/**
	 * Creates the object property hierarchy. Invoking this method is optional
	 * (if not called explicitly, it is called the first time, it is needed).
	 * 
	 * @return The object property hierarchy.
	 * @throws ReasoningMethodUnsupportedException
	 *             Thrown if a reasoning method for object property 
	 *             hierarchy creation is not supported by the reasoner.
	 */
	private ObjectPropertyHierarchyT prepareObjectPropertyHierarchy()
			throws ReasoningMethodUnsupportedException {
		
		TreeMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> roleHierarchyUp = new TreeMap<>(
		);
		TreeMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> roleHierarchyDown = new TreeMap<>(
		);
 
		Set<OWLObjectProperty> atomicRoles = ontology.getObjectPropertiesInSignature();
		for (OWLObjectProperty role : atomicRoles) {
			roleHierarchyDown.put(role, getSubPropertiesImpl(role));
			roleHierarchyUp.put(role, getSuperPropertiesImpl(role));
		}
		roleHierarchy = new ObjectPropertyHierarchyT(roleHierarchyUp, roleHierarchyDown);
		return roleHierarchy;		
	}

	private ObjectPropertyHierarchyT getObjectPropertyHierarchy() {
		try {
			if (roleHierarchy == null) {
				roleHierarchy = prepareObjectPropertyHierarchy();
			}
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}

		return roleHierarchy;
	}
	
	public boolean isSubPropertyOf(OWLProperty subProperty, OWLProperty superProperty){
		if(subProperty.isOWLObjectProperty() && superProperty.isOWLObjectProperty()){
			return getObjectPropertyHierarchy().isSubpropertyOf((OWLObjectProperty)subProperty, (OWLObjectProperty)superProperty);
		} 
		//else if(subProperty.isOWLDataProperty() && superProperty.isOWLDataProperty()){
		//	return getDatatypePropertyHierarchy().isSubpropertyOf((OWLDataProperty)subProperty, (OWLDataProperty)superProperty);
		//}
		return false;
	}

 

 
	public void setSubsumptionHierarchy(ClassHierarchyT subsumptionHierarchy) {
		this.subsumptionHierarchy = subsumptionHierarchy;
	}

 

	public long getInstanceCheckReasoningTimeNs() {
		return instanceCheckReasoningTimeNs;
	}

	public long getRetrievalReasoningTimeNs() {
		return retrievalReasoningTimeNs;
	}

	public int getNrOfInstanceChecks() {
		return nrOfInstanceChecks;
	}

	public int getNrOfRetrievals() {
		return nrOfRetrievals;
	}

	public int getNrOfSubsumptionChecks() {
		return nrOfSubsumptionChecks;
	}

	public long getSubsumptionReasoningTimeNs() {
		return subsumptionReasoningTimeNs;
	}

	public int getNrOfSubsumptionHierarchyQueries() {
		return nrOfSubsumptionHierarchyQueries;
	}

	public long getOverallReasoningTimeNs() {
		return overallReasoningTimeNs;
	}

	public long getTimePerRetrievalNs() {
		return retrievalReasoningTimeNs / nrOfRetrievals;
	}

	public long getTimePerInstanceCheckNs() {
		return instanceCheckReasoningTimeNs / nrOfInstanceChecks;
	}

	public long getTimePerSubsumptionCheckNs() {
		return subsumptionReasoningTimeNs / nrOfSubsumptionChecks;
	}
 
 
}
