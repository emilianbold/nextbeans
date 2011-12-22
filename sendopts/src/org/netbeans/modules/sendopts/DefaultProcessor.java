/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.sendopts;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.netbeans.spi.sendopts.Arg;
import org.netbeans.spi.sendopts.Description;
import org.netbeans.spi.sendopts.ArgsProcessor;
import org.openide.util.Lookup;

/** Processor that is configured from a map, usually from a layer.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public final class DefaultProcessor extends OptionProcessor {
    private static final Option defArgs = Option.defaultArguments();
    private final String clazz;
    private final Set<Option> options;

    private DefaultProcessor(
        String clazz, Set<Option> arr
    ) {
        this.clazz = clazz;
        this.options = Collections.unmodifiableSet(arr);
    }

    private static Option createOption(String type, Character shortName, String longName, String displayName, String description) {
        Option o = null;
        if (shortName == null) {
            shortName = Option.NO_SHORT_NAME;
        }
        switch (Type.valueOf(type)) {
            case withoutArgument: o = Option.withoutArgument(shortName, longName); break;
            case requiredArgument: o = Option.requiredArgument(shortName, longName); break;
            case optionalArgument: o = Option.optionalArgument(shortName, longName); break;
            case additionalArguments: o = Option.additionalArguments(shortName, longName); break;
            default: assert false;
        }
        if (displayName != null) {
            String[] arr = displayName.split("#"); // NOI18N
            o = Option.displayName(o, arr[0], arr[1]);
        }
        if (description != null) {
            String[] arr = description.split("#"); // NOI18N
            o = Option.shortDescription(o, arr[0], arr[1]);
        }
        return o;
    }
    
    public static OptionProcessor create(Class<?> clazz) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("class", clazz.getName());
        int cnt = 1;
        for (Field e : clazz.getFields()) {
            Arg o = e.getAnnotation(Arg.class);
            if (o == null) {
                continue;
            }
            Description d = e.getAnnotation(Description.class);

            if (o.shortName() != Option.NO_SHORT_NAME) {
                map.put(cnt + ".shortName", o.shortName());
            }
            if (!o.longName().isEmpty()) {
                map.put(cnt + ".longName", o.longName());
            }
            if (e.getType() == boolean.class) {
                map.put(cnt + ".type", "withoutArgument");
            } else if (String.class == e.getType()) {
                if (o.defaultValue().equals("\u0000")) {
                    map.put(cnt + ".type", "requiredArgument");
                } else {
                    map.put(cnt + ".type", "optionalArgument");
                }
            } else {
                if (!String[].class.equals(e.getType())) {
                    throw new IllegalStateException("Field type has to be either boolean, String or String[]! " + e);
                }
                map.put(cnt + ".type", "additionalArguments");
            }
            if (o.implicit()) {
                map.put(cnt + ".implicit", true);
            }
            if (d != null) {
                writeBundle(map, cnt + ".displayName", d.displayName(), e);
                writeBundle(map, cnt + ".shortDescription", d.shortDescription(), e);
            }
            cnt++;
        }
        return create(map);
    }
    
    static OptionProcessor create(Map<?,?> map) {
        String c = (String) map.get("class");
        Set<Option> arr = new LinkedHashSet<Option>();
        for (int cnt = 1; ; cnt++) {
            Character shortName = (Character) map.get(cnt + ".shortName"); // NOI18N
            String longName = (String) map.get(cnt + ".longName"); // NOI18N
            if (shortName == null && longName == null) {
                break;
            }
            String type = (String) map.get(cnt + ".type"); // NOI18N
            String displayName = (String)map.get(cnt + ".displayName"); // NOI18N
            String description = (String)map.get(cnt + ".shortDescription"); // NOI18N
            arr.add(createOption(type, shortName, longName, displayName, description));
            if (Boolean.TRUE.equals(map.get(cnt + ".implicit"))) { // NOI18N
                arr.add(defArgs);
            }
        }
        return new DefaultProcessor(c, arr);
    }
    

    @Override
    protected Set<Option> getOptions() {
        return options;
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        try {
            ClassLoader l = findClassLoader();
            Class<?> realClazz = Class.forName(clazz, true, l);
            Object instance = realClazz.newInstance();
            Map<Option,Field> map = processFields(realClazz, options);
            for (Map.Entry<Option, String[]> entry : optionValues.entrySet()) {
                final Option option = entry.getKey();
                Type type = Type.valueOf(option);
                Field f = map.get(option);
                assert f != null : "No field for option: " + option;
                switch (type) {
                    case withoutArgument:
                        f.setBoolean(instance, true); break;
                    case requiredArgument:
                        f.set(instance, entry.getValue()[0]); break;
                    case optionalArgument:
                        if (entry.getValue().length == 1) {
                            f.set(instance, entry.getValue()[0]);
                        } else {
                            f.set(instance, f.getAnnotation(Arg.class).defaultValue());
                        }
                        break;
                    case additionalArguments:
                        f.set(instance, entry.getValue()); break;
                    case defaultArguments:
                        f.set(instance, entry.getValue()); break;
                }
            }
            if (instance instanceof Runnable) {
                ((Runnable)instance).run();
            }
            if (instance instanceof ArgsProcessor) {
                ((ArgsProcessor)instance).process(env);
            }
        } catch (Exception exception) {
            throw (CommandException)new CommandException(10, exception.getLocalizedMessage()).initCause(exception);
        }
    }

    private static ClassLoader findClassLoader() {
        ClassLoader l = null;
        try {
            l = findClassLoaderFromLookup();
        } catch (LinkageError ex) {
            // OK, lookup is not on classpath
        }
        if (l == null) {
            l = Thread.currentThread().getContextClassLoader();
        }
        if (l == null) {
            l = DefaultProcessor.class.getClassLoader();
        }
        return l;
    }

    private static ClassLoader findClassLoaderFromLookup() {
        return Lookup.getDefault().lookup(ClassLoader.class);
    }
    
    private static Map<Option,Field> processFields(Class<?> type, Set<Option> options) {
        Map<Option,Field> map = new HashMap<Option, Field>();
        for (Field f : type.getFields()) {
            Arg arg = f.getAnnotation(Arg.class);
            if (arg == null) {
                continue;
            }
            Option o = null;
            for (Option c : options) {
                char shortN = (char)OptionImpl.Trampoline.DEFAULT.getShortName(c);
                String longN = OptionImpl.Trampoline.DEFAULT.getLongName(c);
                
                if (shortN == (int)arg.shortName() && equalStrings(longN, arg)) {
                    o = c;
                    break;
                }
            }
            assert o != null : "No option for field " + f + " options: " + options;
            map.put(o, f);
            if (arg.implicit()) {
                map.put(defArgs, f);
            }
        }
        assert map.size() == options.size() : "Map " + map + " Options " + options;
        return map;
    }
    private static boolean equalStrings(String longN, Arg arg) {
        if (longN == null) {
            return arg.longName().isEmpty();
        } else {
            return longN.equals(arg.longName());
        }
    }

    private static enum Type {
        withoutArgument, requiredArgument, optionalArgument, 
        additionalArguments, defaultArguments;
        
        public static Type valueOf(Option o) {
            OptionImpl impl = OptionImpl.Trampoline.DEFAULT.impl(o);
            switch (impl.argumentType) {
                case 0: return withoutArgument;
                case 1: return requiredArgument;
                case 2: return optionalArgument;
                case 3: return additionalArguments;
                case 4: return defaultArguments;
            }
            assert false;
            return null;
        }
    }
    private static void writeBundle(Map<String,Object> f, String key, String value, Field e) throws IllegalStateException {
        if (value.isEmpty()) {
            return;
        }
        if (value.startsWith("#")) {
            Package pkg = e.getDeclaringClass().getPackage();
            value = pkg.getName() + ".Bundle" + value;
        }
        f.put(key, value);
    }
}
