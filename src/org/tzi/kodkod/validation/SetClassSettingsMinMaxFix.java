package org.tzi.kodkod.validation;

import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;

/**
 * Fix that is provided for an invalidity in a configuration.
 * 
 * This sets the lower and upper bound values for a class to selected values.
 * 
 * @author Jan Prien
 *
 */
final class SetClassSettingsMinMaxFix extends AbstractFix {

	/**
	 * The new lower bound value.
	 */
	private final int newMin;

	/**
	 * The new upper bound value.
	 */
	private final int newMax;

	/**
	 * The configuration aspect.
	 */

	private final ClassSettings classSettings;

	/**
	 * Constructs an object.
	 * 
	 * @param config
	 *            The configuration that gets modified when applying the fix.
	 * @param model
	 *            The model for which the configuration is.
	 * @param explenation
	 *            The textual explanation of the fix.
	 * @param resolvesProblemCompletely
	 *            Whether the problem is resolved completely or only partially.
	 * @param classSettings
	 *            The configuration aspect.
	 * @param newMin
	 *            The new lower bound value.
	 * @param newMax
	 *            The new upper bound value.
	 */
	protected SetClassSettingsMinMaxFix(SettingsConfiguration config, IModel model, String explenation,
			boolean resolvesProblemCompletely, final ClassSettings classSettings, final int newMin, final int newMax) {
		super(config, model, explenation, resolvesProblemCompletely);
		if (classSettings == null) {
			throw new IllegalArgumentException();
		}
		this.classSettings = classSettings;
		this.newMin = newMin;
		this.newMax = newMax;
	}

	@Override
	public void apply() {
		classSettings.setLowerBound(newMin);
		classSettings.setUpperBound(newMax);
	}

}
