/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.fmt;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * Support for tag handlers for &lt;bundle&gt;, the resource bundle loading
 * tag in JSTL.
 * @version 1.1
 *
 * TODO decide how to implement setResponseLocale
 */
public class BundleTag extends TagSupport {

    //*********************************************************************
    // Private constants

    private static final Locale EMPTY_LOCALE = new Locale("", "");

    //*********************************************************************
    // Protected state

    /*
     * Gets the resource bundle with the given base name and preferred locale.
     *
     * This method calls java.util.ResourceBundle.getBundle(), but ignores
     * its return value unless its locale represents an exact or language match
     * with the given preferred locale.
     *
     * @param basename the resource bundle base name
     * @param pref the preferred locale
     * @param cl   classloader used to find resource bundle
     * @return the requested resource bundle, or {@code null} if no resource
     * bundle with the given base name exists or if there is no exact- or
     * language-match between the preferred locale and the locale of
     * the bundle returned by java.util.ResourceBundle.getBundle().
     */
    private static ResourceBundle findMatch(final String basename, final Locale pref, final ClassLoader cl) {
        ResourceBundle match = null;

        try {
            final ResourceBundle bundle =
            ResourceBundle.getBundle(basename, pref, cl);
            final Locale avail = bundle.getLocale();
            if (pref.equals(avail)) {
                // Exact match
                match = bundle;
            } else if (pref.getLanguage().equals(avail.getLanguage())
            && "".equals(avail.getCountry())) {

                // Language match.
                // By making sure the available locale does not have a
                // country and matches the preferred locale's language, we
                // rule out "matches" based on the container's default
                // locale. For example, if the preferred locale is
                // "en-US", the container's default locale is "en-UK", and
                // there is a resource bundle (with the requested base
                // name) available for "en-UK", ResourceBundle.getBundle()
                // will return it, but even though its language matches
                // that of the preferred locale, we must ignore it,
                // because matches based on the container's default locale
                // are not portable across different containers with
                // different default locales.

                match = bundle;
            }
        } catch (final MissingResourceException mre) {
        }

        return match;
    }
    /**
     * Gets the default I18N localization context.
     *
     * @param jc Page in which to look up the default I18N localization context
     */
    public static LocalizationContext getLocalizationContext(final JellyContext jc) {
        LocalizationContext locCtxt = null;

        final Object obj = jc.getVariable(Config.FMT_LOCALIZATION_CONTEXT);
        if (obj == null) {
            return null;
        }

        if (obj instanceof LocalizationContext) {
            locCtxt = (LocalizationContext) obj;
        } else {
            // localization context is a bundle basename
            locCtxt = getLocalizationContext(jc, (String) obj);
        }

        return locCtxt;
    }
    /**
     * Gets the resource bundle with the given base name, whose locale is
     * determined as follows:
     *
     * Check if a match exists between the ordered set of preferred
     * locales and the available locales, for the given base name.
     * The set of preferred locales consists of a single locale
     * (if the <code>org.apache.commons.jelly.tags.fmt.locale</code> configuration
     * setting is present).
     *
     * <p> If no match was found in the previous step, check if a match
     * exists between the fallback locale (given by the
     * <code>org.apache.commons.jelly.tags.fmt.fallbackLocale</code> configuration
     * setting) and the available locales, for the given base name.
     *
     * @param jellyContext Page in which the resource bundle with the
     * given base name is requested
     * @param basename Resource bundle base name
     * @return Localization context containing the resource bundle with the
     * given base name and the locale that led to the resource bundle match,
     * or the empty localization context if no resource bundle match was found
     */
    public static LocalizationContext getLocalizationContext(final JellyContext jellyContext,
    final String basename) {
        LocalizationContext locCtxt = null;
        ResourceBundle bundle = null;

        if (basename == null || basename.isEmpty()) {
            return new LocalizationContext();
        }

        // Try preferred locales
        Locale pref = null; {
            final Object tmp = jellyContext.getVariable(Config.FMT_LOCALE);
            if (tmp != null && tmp instanceof Locale) {
                pref = (Locale) tmp;
            }
        }
        if (pref != null) {
            // Preferred locale is application-based
            bundle = findMatch(basename, pref, jellyContext.getClassLoader());
            if (bundle != null) {
                locCtxt = new LocalizationContext(bundle, pref);
            }
        }

        if (locCtxt == null) {
            // No match found with preferred locales, try using fallback locale
            {
                final Object tmp = jellyContext.getVariable(Config.FMT_FALLBACK_LOCALE);
                if (tmp != null && tmp instanceof Locale) {
                    pref = (Locale) tmp;
                }
            }
            if (pref != null) {
                bundle = findMatch(basename, pref, jellyContext.getClassLoader());
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, pref);
                }
            }
        }

        if (locCtxt == null) {
            // try using the root resource bundle with the given basename
            try {
                bundle = ResourceBundle.getBundle(basename, EMPTY_LOCALE,
                jellyContext.getClassLoader());
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, null);
                }
            } catch (final MissingResourceException mre) {
                // do nothing
            }
        }

        if (locCtxt != null) {
            // set response locale
            if (locCtxt.getLocale() != null) {
                // TODO
                // SetLocaleSupport.setResponseLocale(jc, locCtxt.getLocale());
            }
        } else {
            // create empty localization context
            locCtxt = new LocalizationContext();
        }

        return locCtxt;
    }
    private Expression basename;                  // 'basename' attribute

    //*********************************************************************
    // Private state

    private Expression prefix;                    // 'prefix' attribute

    //*********************************************************************
    // Constructor and initialization

    /** Evaluated basename */
    private String ebasename;

    //*********************************************************************
    // Collaboration with subtags

    /** Evaluated prefix */
    private String eprefix;

    private LocalizationContext locCtxt;

    //*********************************************************************
    // Tag logic

    public BundleTag() {
    }

    //*********************************************************************
    // Public utility methods

    /**
     * Evaluates this tag after all the tags properties have been initialized.
     *
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        Object basenameInput = null;
        if (this.basename != null) {
            basenameInput = this.basename.evaluate(context);
        }
        if (basenameInput != null) {
            ebasename = basenameInput.toString();
        }

        Object prefixInput = null;
        if (this.prefix != null) {
            prefixInput = this.prefix.evaluate(context);
        }
        if (prefixInput != null) {
            eprefix = prefixInput.toString();
        }

        this.locCtxt = BundleTag.getLocalizationContext(context, ebasename);
        invokeBody(output);
    }

    public LocalizationContext getLocalizationContext() {
        return locCtxt;
    }

    public String getPrefixAsString() {
        return eprefix;
    }

    /** Setter for property basename.
     * @param basename New value of property basename.
     *
     */
    public void setBasename(final Expression basename) {
        this.basename = basename;
    }

    /** Setter for property prefix.
     * @param prefix New value of property prefix.
     *
     */
    public void setPrefix(final Expression prefix) {
        this.prefix = prefix;
    }

}
