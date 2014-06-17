package org.tzi.use.kodkod.plugin;

import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelFactory;
import org.tzi.kodkod.model.impl.SimpleFactory;
import org.tzi.kodkod.model.type.PrimitiveTypeFactory;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLGroupRegistry;
import org.tzi.kodkod.ocl.operation.AnyOperationGroup;
import org.tzi.kodkod.ocl.operation.BooleanOperationGroup;
import org.tzi.kodkod.ocl.operation.ClassOperationGroup;
import org.tzi.kodkod.ocl.operation.CollectionConstructorGroup;
import org.tzi.kodkod.ocl.operation.ConditionalOperationGroup;
import org.tzi.kodkod.ocl.operation.IntegerOperationGroup;
import org.tzi.kodkod.ocl.operation.SetOperationGroup;
import org.tzi.kodkod.ocl.operation.VariableOperationGroup;
import org.tzi.use.kodkod.transform.ModelTransformator;
import org.tzi.use.main.ChangeEvent;
import org.tzi.use.main.ChangeListener;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MModel;

/**
 * Singleton to encapsulate the model transformation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum PluginModelFactory implements ChangeListener {

	INSTANCE;

	private Session session = null;
	private boolean reTransform = true;

	private IModel model;
	private TypeFactory typeFactory;
	private IModelFactory modelFactory;

	private PluginModelFactory() {
		setModelFactory(new SimpleFactory());
		setTypeFactory(new PrimitiveTypeFactory());
		registerDefaultOperationGroups();
	}

	/**
	 * Returns the representing model for the given use model.
	 * 
	 * @param mModel
	 * @return
	 */
	public IModel getModel(final MModel mModel) {
		if (reTransform) {
			reTransform = false;

			ModelTransformator transformator = new ModelTransformator(modelFactory, typeFactory);
			model = transformator.transform(mModel);
		}

		return model;
	}

	/**
	 * Sets the model factory.
	 * 
	 * @param modelFactory
	 */
	public void setModelFactory(IModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	/**
	 * Sets the type factory
	 * 
	 * @param typeFactory
	 */
	public void setTypeFactory(TypeFactory typeFactory) {
		this.typeFactory = typeFactory;
	}

	/**
	 * Registers the default operation groups with the different translation
	 * methods.
	 */
	public void registerDefaultOperationGroups() {
		OCLGroupRegistry registry = OCLGroupRegistry.INSTANCE;
		registry.registerOperationGroup(new VariableOperationGroup(typeFactory));
		registry.registerOperationGroup(new IntegerOperationGroup(typeFactory));
		registry.registerOperationGroup(new BooleanOperationGroup(typeFactory));
		registry.registerOperationGroup(new ClassOperationGroup(typeFactory));
		registry.registerOperationGroup(new AnyOperationGroup(typeFactory, true));
		registry.registerOperationGroup(new ConditionalOperationGroup(typeFactory));
		registry.registerOperationGroup(new SetOperationGroup(typeFactory));
		registry.registerOperationGroup(new CollectionConstructorGroup(typeFactory));
	}
	
	public void registerForSession(Session s){
		if(s == session){
			return;
		}
		s.addChangeListener(this);
		session = s;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		reTransform = true;
	}
}
