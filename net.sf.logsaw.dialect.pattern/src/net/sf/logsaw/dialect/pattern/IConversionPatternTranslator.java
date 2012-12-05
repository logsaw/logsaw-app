/*******************************************************************************
 * Copyright (c) 2010, 2011 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.dialect.pattern;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.LogEntry;

import org.eclipse.core.runtime.CoreException;

/**
 * Classes implementing this interface can extract conversion rules from a 
 * conversion pattern.
 * 
 * @author Philipp Nanz
 */
public interface IConversionPatternTranslator {

	/**
	 * Returns the number of lines to be expected per log entry.
	 * @return the number of lines
	 */
	int getLinesPerEntry();

	/**
	 * Applies the specified locale to the given rules, as provided by 
	 * the log resource.
	 * @param loc the locale
	 * @param rules the rules
	 */
	void applyLocale(Locale loc, List<ConversionRule> rules);

	/**
	 * Applies the specified timezone to the given rules, as provided by 
	 * the log resource.
	 * @param tw the timezone
	 * @param rules the rules
	 */
	void applyTimeZone(TimeZone tz, List<ConversionRule> rules);

	/**
	 * Prepares the given external pattern, if necessary.
	 * @param externalPattern the external pattern to modify
	 * @return the (un)modified external pattern
	 * @throws CoreException if an error occurred
	 */
	String prepare(String externalPattern) throws CoreException;

	/**
	 * Extracts conversion rules from the given external pattern.
	 * @param externalPattern the external pattern
	 * @return the conversion rules
	 * @throws CoreException if an error occurred
	 */
	List<ConversionRule> extractRules(String externalPattern) throws CoreException;

	/**
	 * Applies defaults to the given rule.
	 * @param rule the conversion rule
	 * @param dialect the log dialect
	 * @throws CoreException if an error occurred
	 */
	void applyDefaults(ConversionRule rule, APatternDialect dialect) throws CoreException;

	/**
	 * Rewrites the given rule
	 * @param rule the conversion rule
	 * @param dialect the log dialect
	 * @throws CoreException if an error occurred
	 */
	void rewrite(ConversionRule rule, APatternDialect dialect) throws CoreException;

	/**
	 * Returns the Regex pattern matching the given rule.
	 * @param rule the conversion rule
	 * @return the Regex pattern matching the rule
	 * @throws CoreException if an error occurred
	 */
	String getRegexPatternForRule(ConversionRule rule) throws CoreException;

	/**
	 * Extracts the specified field.
	 * @param entry the entry to add the field to
	 * @param rule the rule to apply
	 * @param val the value
	 * @throws CoreException if an error occurred
	 */
	void extractField(LogEntry entry, ConversionRule rule, String val) throws CoreException;

	/**
	 * Returns the log entry field representing the given rule.
	 * @param rule the conversion rule
	 * @return the log entry field representing the rule
	 * @throws CoreException if an error occurred
	 */
	ALogEntryField<?, ?> getFieldForRule(ConversionRule rule) throws CoreException;
}
