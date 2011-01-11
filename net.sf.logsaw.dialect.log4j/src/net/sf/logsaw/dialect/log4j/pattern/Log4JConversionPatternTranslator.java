/*******************************************************************************
 * Copyright (c) 2010 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.dialect.log4j.pattern;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.logsaw.core.dialect.ILogLevelProvider;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.log4j.Log4JFieldProvider;
import net.sf.logsaw.dialect.log4j.internal.Messages;
import net.sf.logsaw.dialect.pattern.ConversionRule;
import net.sf.logsaw.dialect.pattern.IConversionPatternTranslator;
import net.sf.logsaw.dialect.pattern.RegexUtils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * This class can extract conversion rules out of a Log4J pattern.
 * 
 * @author Philipp Nanz
 */
public final class Log4JConversionPatternTranslator implements IConversionPatternTranslator {

	private static final Pattern EXTRACTION_PATTERN = 
		Pattern.compile("%(-?(\\d+))?(\\.(\\d+))?([a-zA-Z])(\\{([^\\}]+)\\})?"); //$NON-NLS-1$

	private static final String PROP_DATEFORMAT = "dateFormat"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.IConversionPatternTranslator#prepare(java.lang.String)
	 */
	@Override
	public String prepare(String externalPattern) throws CoreException {
		if (!externalPattern.endsWith("%n")) { //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
					Messages.Log4JConversionRuleExtractor_error_mustEndWithNewLine));
		}
		// Pattern without %n
		externalPattern = externalPattern.substring(0, externalPattern.length() - 2);
		if (externalPattern.contains("%n")) { //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
					Messages.Log4JConversionRuleExtractor_error_moreThanOneNewLine));
		}
		return externalPattern;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.IConversionPatternTranslator#applyLocale(java.util.Locale, java.util.List)
	 */
	@Override
	public void applyLocale(Locale loc, List<ConversionRule> rules) {
		for (ConversionRule rule : rules) {
			if (rule.getPlaceholderName().equals("d")) { //$NON-NLS-1$
				SimpleDateFormat df = rule.getProperty(PROP_DATEFORMAT, SimpleDateFormat.class);
				Assert.isNotNull(df, "dateFormat"); //$NON-NLS-1$
				df.setDateFormatSymbols(DateFormatSymbols.getInstance(loc));
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.IConversionPatternTranslator#applyTimeZone(java.util.TimeZone, java.util.List)
	 */
	@Override
	public void applyTimeZone(TimeZone tz, List<ConversionRule> rules) {
		for (ConversionRule rule : rules) {
			if (rule.getPlaceholderName().equals("d")) { //$NON-NLS-1$
				SimpleDateFormat df = rule.getProperty(PROP_DATEFORMAT, SimpleDateFormat.class);
				Assert.isNotNull(df, "dateFormat"); //$NON-NLS-1$
				df.setTimeZone(tz);
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.log4j.pattern.IConversionRuleExtractor#extractRules(java.lang.String)
	 */
	@Override
	public List<ConversionRule> extractRules(String externalPattern) throws CoreException {
		// Find supported conversion characters
		Matcher m = EXTRACTION_PATTERN.matcher(externalPattern);
		List<ConversionRule> ret = new ArrayList<ConversionRule>();
		
		while (m.find()) {
			String minWidthModifier = m.group(2);
			String maxWidthModifier = m.group(4);
			String conversionName = m.group(5);
			String conversionModifier = m.group(7);
			
			int minWidth = -1; // not specified
			if ((minWidthModifier != null) && (minWidthModifier.length() > 0)) {
				minWidth = Integer.parseInt(minWidthModifier);
			}
			int maxWidth = -1; // not specified
			if ((maxWidthModifier != null) && (maxWidthModifier.length() > 0)) {
				maxWidth = Integer.parseInt(maxWidthModifier);
			}
			ConversionRule rule = new ConversionRule();
			rule.setBeginIndex(m.start());
			rule.setLength(m.end() - m.start());
			rule.setMaxWidth(maxWidth);
			rule.setMinWidth(minWidth);
			rule.setPlaceholderName(conversionName);
			rule.setModifier(conversionModifier);
			ret.add(rule);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.IConversionPatternTranslator#applyDefaults(net.sf.logsaw.dialect.pattern.ConversionRule)
	 */
	@Override
	public void applyDefaults(ConversionRule rule) throws CoreException {
		if (rule.getPlaceholderName().equals("d") && (rule.getModifier() == null)) { //$NON-NLS-1$
			// ISO8601 is the default
			rule.setModifier("ISO8601"); //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.IConversionPatternTranslator#rewrite(net.sf.logsaw.dialect.pattern.ConversionRule)
	 */
	@Override
	public void rewrite(ConversionRule rule) throws CoreException {
		if (rule.getPlaceholderName().equals("d")) { //$NON-NLS-1$
			if (rule.getModifier().equals("ABSOLUTE")) { //$NON-NLS-1$
				rule.setModifier("HH:mm:ss,SSS"); //$NON-NLS-1$
			} else if (rule.getModifier().equals("DATE")) { //$NON-NLS-1$
				rule.setModifier("dd MMM yyyy HH:mm:ss,SSS"); //$NON-NLS-1$
			} else if (rule.getModifier().equals("ISO8601")) { //$NON-NLS-1$
				rule.setModifier("yyyy-MM-dd HH:mm:ss,SSS"); //$NON-NLS-1$
			}
			try {
				// Cache date format
				rule.putProperty(PROP_DATEFORMAT, new SimpleDateFormat(rule.getModifier()));
			} catch (IllegalArgumentException e) {
				throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
						NLS.bind(Messages.Log4JConversionRuleTranslator_error_dataFormatNotSupported, rule.getModifier())));
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.log4j.pattern.IConversionRuleTranslator#convertToRegexPattern(net.sf.logsaw.dialect.log4j.pattern.ConversionRule)
	 */
	@Override
	public String getRegexPatternForRule(ConversionRule rule) throws CoreException {
		if (rule.getPlaceholderName().equals("d")) { //$NON-NLS-1$
			// Pattern is dynamic
			return "(" + RegexUtils.getRegexForSimpleDateFormat(rule.getModifier()) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("p")) { //$NON-NLS-1$
			String lnHint = RegexUtils.getLengthHint(rule);
			if (lnHint.length() > 0) {
				return "([ A-Z]" + lnHint + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			// Default: Length is limited by the levels available
			return "([A-Z]{4,5})"; //$NON-NLS-1$
		} else if (rule.getPlaceholderName().equals("c")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("t")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("m")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("F")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("C")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("M")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("L")) { //$NON-NLS-1$
			return "([0-9]*" + RegexUtils.getLengthHint(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getPlaceholderName().equals("x")) { //$NON-NLS-1$
			return "(.*" + RegexUtils.getLengthHint(rule) + RegexUtils.getLazySuffix(rule) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
				NLS.bind(Messages.Log4JConversionRuleTranslator_error_unsupportedConversionCharacter, rule.getPlaceholderName())));
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.log4j.pattern.IConversionRuleTranslator#getFieldForRule(net.sf.logsaw.dialect.log4j.pattern.ConversionRule)
	 */
	@Override
	public ALogEntryField<?, ?> getFieldForRule(ConversionRule rule) throws CoreException {
		if (rule.getPlaceholderName().equals("d")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_TIMESTAMP;
		} else if (rule.getPlaceholderName().equals("p")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_LEVEL;
		} else if (rule.getPlaceholderName().equals("c")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_LOGGER;
		} else if (rule.getPlaceholderName().equals("t")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_THREAD;
		} else if (rule.getPlaceholderName().equals("m")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_MESSAGE;
		} else if (rule.getPlaceholderName().equals("F")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_LOC_FILENAME;
		} else if (rule.getPlaceholderName().equals("C")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_LOC_CLASS;
		} else if (rule.getPlaceholderName().equals("M")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_LOC_METHOD;
		} else if (rule.getPlaceholderName().equals("L")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_LOC_LINE;
		} else if (rule.getPlaceholderName().equals("x")) { //$NON-NLS-1$
			return Log4JFieldProvider.FIELD_NDC;
		}
		throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
				NLS.bind(Messages.Log4JConversionRuleTranslator_error_unsupportedConversionCharacter, rule.getPlaceholderName())));
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.log4j.pattern.IConversionRuleTranslator#extractField(net.sf.logsaw.core.model.LogEntry, net.sf.logsaw.dialect.log4j.pattern.ConversionRule, net.sf.logsaw.core.model.ALogEntryField, java.lang.String)
	 */
	@Override
	public void extractField(LogEntry entry, ConversionRule rule,
			String val) throws CoreException {
		if (rule.getPlaceholderName().equals("d")) { //$NON-NLS-1$
			DateFormat df = rule.getProperty(PROP_DATEFORMAT, DateFormat.class);
			Assert.isNotNull(df, "dateFormat"); //$NON-NLS-1$
			try {
				entry.put(Log4JFieldProvider.FIELD_TIMESTAMP, df.parse(val.trim()));
			} catch (ParseException e) {
				throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
						NLS.bind(Messages.Log4JConversionRuleTranslator_error_failedToParseTimestamp, val.trim())));
			}
		} else if (rule.getPlaceholderName().equals("p")) { //$NON-NLS-1$
			Level lvl = Log4JFieldProvider.FIELD_LEVEL.getLevelProvider().findLevel(val.trim());
			if (ILogLevelProvider.ID_LEVEL_UNKNOWN == lvl.getValue()) {
				throw new CoreException(new Status(IStatus.WARNING, Log4JDialectPlugin.PLUGIN_ID, 
						NLS.bind(Messages.Log4JConversionRuleTranslator_warning_unknownPriority, val.trim())));
			}
			entry.put(Log4JFieldProvider.FIELD_LEVEL, lvl);
		} else if (rule.getPlaceholderName().equals("c")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_LOGGER, val.trim());
		} else if (rule.getPlaceholderName().equals("t")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_THREAD, val.trim());
		} else if (rule.getPlaceholderName().equals("m")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_MESSAGE, val.trim());
		} else if (rule.getPlaceholderName().equals("F")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_LOC_FILENAME, val.trim());
		} else if (rule.getPlaceholderName().equals("C")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_LOC_CLASS, val.trim());
		} else if (rule.getPlaceholderName().equals("M")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_LOC_METHOD, val.trim());
		} else if (rule.getPlaceholderName().equals("L")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_LOC_LINE, val.trim());
		} else if (rule.getPlaceholderName().equals("x")) { //$NON-NLS-1$
			entry.put(Log4JFieldProvider.FIELD_NDC, val.trim());
		} else {
			throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
					NLS.bind(Messages.Log4JConversionRuleTranslator_error_unsupportedConversionCharacter, rule.getPlaceholderName())));
		}
	}
}
