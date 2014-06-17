package org.tzi.use.kodkod.plugin;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodModelValidatorConfiguration;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.gen.model.GFlaggedInvariant;
import org.tzi.use.gen.model.GModel;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.UseLogAppender;
import org.tzi.use.kodkod.transform.InvariantTransformator;
import org.tzi.use.kodkod.transform.enrich.ModelEnricher;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MSystem;

/**
 * Abstract base class for the new commands.
 * 
 * @author Hendrik Reitmann
 * 
 */
public abstract class AbstractPlugin {

	protected static final Logger LOG = Logger.getLogger(AbstractPlugin.class);

	protected MModel mModel;
	protected MSystem mSystem;

	protected void initialize(Session session) {
		mSystem = session.system();
		mModel = mSystem.model();
		PluginModelFactory.INSTANCE.registerForSession(session);
	}

	protected void initialize(Session session, MainWindow mainWindow) {
		initialize(session);
		UseLogAppender.initialize(mainWindow.logWriter());
	}

	/**
	 * Enriches the model with a given object diagram (automatic diagram extraction).
	 */
	protected void enrichModel() {
		ModelEnricher enricher = KodkodModelValidatorConfiguration.INSTANCE.getModelEnricher();
		enricher.enrichModel(mSystem, model());
	}

	protected IModel model() {
		return PluginModelFactory.INSTANCE.getModel(mModel);
	}

	/**
	 * Transforms the invariants of the generator and enrich the model.
	 */
	protected void enrichModelWithLoadedInvariants() {
		try {
			ModelConfigurator configurator = (ModelConfigurator) model().getConfigurator();
			configurator.clear();
			
			GModel gModel=null;
			try {
				gModel = mSystem.generator().gModel();
			} catch (NoSuchMethodError e) {
				LOG.error(LogMessages.noSuchMethodError, e);
			}
			
			Collection<GFlaggedInvariant> loadedClassInvariants = gModel.loadedFlaggedInvariants();

			if (loadedClassInvariants.size() > 0) {
				LOG.info(LogMessages.enrichWithLoadedInvariants);

				InvariantTransformator invariantTransformator = new InvariantTransformator(model().modelFactory(), model().typeFactory());

				for (GFlaggedInvariant loadedInvariant : loadedClassInvariants) {
					if (!configurator.getInvariants().containsValue(loadedInvariant.classInvariant().name())) {

						IInvariant invariant = invariantTransformator.transform(model(), loadedInvariant.classInvariant());
						if(loadedInvariant.negated()){
							invariant.negate();
						}
						if(loadedInvariant.disabled()){
							invariant.deactivate();
						}
						configurator.addInvariant(invariant);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(LogMessages.errorWithLoadedInvariants);
			if (LOG.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}
}
