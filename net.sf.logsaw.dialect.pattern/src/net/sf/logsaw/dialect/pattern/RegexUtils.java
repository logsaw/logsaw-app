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
package net.sf.logsaw.dialect.pattern;

import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.logsaw.dialect.pattern.internal.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philipp Nanz
 */
public class RegexUtils {

	private static transient Logger logger = LoggerFactory.getLogger(RegexUtils.class);

	/**
	 * Returns the Regex lazy suffix for the given rule.
	 * @param rule the conversion rule
	 * @return the Regex lazy suffix
	 */
	public static String getLazySuffix(ConversionRule rule) {
		if (rule.isFollowedByQuotedString()) {
			return "?"; //$NON-NLS-1$
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the Regex length hint for the given rule.
	 * @param rule the conversion rule
	 * @return the Regex length hint
	 */
	public static String getLengthHint(ConversionRule rule) {
		if ((rule.getMaxWidth() > 0) && (rule.getMaxWidth() == rule.getMinWidth())) {
			// Exact length specified
			return "{" + rule.getMaxWidth()+ "}"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (rule.getMaxWidth() > 0) {
			// Both min and max are specified
			return "{" + Math.max(0, rule.getMinWidth()) + "," + rule.getMaxWidth()+ "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (rule.getMinWidth() > 0) {
			// Only min is specified
			return "{" + rule.getMinWidth() + ",}"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Converts a given <code>java.lang.SimpleDateFormat</code> pattern into 
	 * a regular expression
	 * @param format the pattern
	 * @return the translated pattern
	 * @throws CoreException if an error occurred
	 */
	public static String getRegexForSimpleDateFormat(String format) throws CoreException {
		RegexUtils utils = new RegexUtils();
		return utils.doGetRegexForSimpleDateFormat(format);
	}

	private String doGetRegexForSimpleDateFormat(String format) throws CoreException {
		try {
			new SimpleDateFormat(format);
		} catch (Exception e) {
			// Pattern is invalid
			throw new CoreException(new Status(IStatus.ERROR, PatternDialectPlugin.PLUGIN_ID, 
					Messages.RegexUtils_error_invalidDateFormat));
		}
		// Initialize
		ReplacementContext ctx = new ReplacementContext();
		ctx.setBits(new BitSet(format.length()));
		ctx.setBuffer(new StringBuffer(format));
		
		// Unquote
		unquote(ctx);
		
		// G - Era designator
		replace(ctx, "G+", "[ADBC]{2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// y - Year
		replace(ctx, "[y]{3,}", "\\d{4}"); //$NON-NLS-1$ //$NON-NLS-2$
		replace(ctx, "[y]{2}", "\\d{2}"); //$NON-NLS-1$ //$NON-NLS-2$
		replace(ctx, "y", "\\d{4}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// M - Month in year
		replace(ctx, "[M]{3,}", "[a-zA-Z]*"); //$NON-NLS-1$ //$NON-NLS-2$
		replace(ctx, "[M]{2}", "\\d{2}"); //$NON-NLS-1$ //$NON-NLS-2$
		replace(ctx, "M", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// w - Week in year
		replace(ctx, "w+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// W - Week in month
		replace(ctx, "W+", "\\d"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// D - Day in year
		replace(ctx, "D+", "\\d{1,3}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// d - Day in month
		replace(ctx, "d+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// F - Day of week in month
		replace(ctx, "F+", "\\d"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// E - Day in week
		replace(ctx, "E+", "[a-zA-Z]*"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// a - Am/pm marker
		replace(ctx, "a+", "[AMPM]{2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// H - Hour in day (0-23)
		replace(ctx, "H+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// k - Hour in day (1-24)
		replace(ctx, "k+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// K - Hour in am/pm (0-11)
		replace(ctx, "K+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// h - Hour in am/pm (1-12)
		replace(ctx, "h+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// m - Minute in hour
		replace(ctx, "m+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// s - Second in minute
		replace(ctx, "s+", "\\d{1,2}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// S - Millisecond
		replace(ctx, "S+", "\\d{1,3}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// z - Time zone
		replace(ctx, "z+", "[a-zA-Z-+:0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Z - Time zone
		replace(ctx, "Z+", "[-+]\\d{4}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return ctx.getBuffer().toString();
	}

	private void unquote(ReplacementContext ctx) {
		Pattern p = Pattern.compile("'[^']+'"); //$NON-NLS-1$
		Matcher m = p.matcher(ctx.getBuffer().toString());
		while (m.find()) {
			logger.trace(ctx.toString());
			
			// Match is valid
			int offset = -2;
			
			// Copy all bits after the match
			for (int i = m.end(); i < ctx.getBuffer().length(); i++) {
				ctx.getBits().set(i + offset, ctx.getBits().get(i));
			}
			for (int i = m.start(); i < m.end() + offset; i++) {
				ctx.getBits().set(i);
			}
			ctx.getBuffer().replace(m.start(), m.start() + 1, ""); //$NON-NLS-1$
			ctx.getBuffer().replace(m.end() - 2, m.end() - 1, ""); //$NON-NLS-1$
			logger.trace(ctx.toString());
		}
		
		p = Pattern.compile("''"); //$NON-NLS-1$
		m = p.matcher(ctx.getBuffer().toString());
		while (m.find()) {
			logger.trace(ctx.toString());
			
			// Match is valid
			int offset = -1;
			
			// Copy all bits after the match
			for (int i = m.end(); i < ctx.getBuffer().length(); i++) {
				ctx.getBits().set(i + offset, ctx.getBits().get(i));
			}
			for (int i = m.start(); i < m.end() + offset; i++) {
				ctx.getBits().set(i);
			}
			ctx.getBuffer().replace(m.start(), m.start() + 1, ""); //$NON-NLS-1$
			logger.trace(ctx.toString());
		}
	}

	private void replace(ReplacementContext ctx, String regex, String replacement) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(ctx.getBuffer().toString());
		while (m.find()) {
			logger.trace(regex);
			logger.trace(ctx.toString());
			int idx = ctx.getBits().nextSetBit(m.start());
			if ((idx == -1) || (idx > m.end() - 1)) {
				// Match is valid
				int len = m.end() - m.start();
				int offset = replacement.length() - len;
				if (offset > 0) {
					// Copy all bits after the match, in reverse order
					for (int i = ctx.getBuffer().length() - 1; i > m.end(); i--) {
						ctx.getBits().set(i + offset, ctx.getBits().get(i));
					}
				} else if (offset < 0) {
					// Copy all bits after the match
					for (int i = m.end(); i < ctx.getBuffer().length(); i++) {
						ctx.getBits().set(i + offset, ctx.getBits().get(i));
					}
				}
				for (int i = m.start(); i < m.end() + offset; i++) {
					ctx.getBits().set(i);
				}
				ctx.getBuffer().replace(m.start(), m.end(), replacement);
				logger.trace(ctx.toString());
			}
		}
	}

	private class ReplacementContext {

		private BitSet bits;
		private StringBuffer buffer;

		/**
		 * @return the bits
		 */
		public BitSet getBits() {
			return bits;
		}

		/**
		 * @param bits the bits to set
		 */
		public void setBits(BitSet bits) {
			this.bits = bits;
		}

		/**
		 * @return the buffer
		 */
		public StringBuffer getBuffer() {
			return buffer;
		}

		/**
		 * @param buffer the buffer to set
		 */
		public void setBuffer(StringBuffer buffer) {
			this.buffer = buffer;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("ReplacementContext [bits="); //$NON-NLS-1$
			for (int i = 0; i < buffer.length(); i++) {
				sb.append(bits.get(i) ? '1' : '0');
			}
			sb.append(", buffer="); //$NON-NLS-1$
			sb.append(buffer);
			sb.append(']');
			return sb.toString();
		}
	}
}
