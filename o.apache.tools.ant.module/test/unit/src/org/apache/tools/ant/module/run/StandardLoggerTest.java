/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.apache.tools.ant.module.run;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.netbeans.junit.NbTestCase;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakSet;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 * Tests functionality of the standard logger using a mock execution environment.
 * @author Jesse Glick
 */
public class StandardLoggerTest extends NbTestCase {
    
    // XXX set up mock StatusDisplayer impl?
    
    public StandardLoggerTest(String name) {
        super(name);
    }
    
    public void testBasicUsage() throws Exception {
        // Just simulates something like
        // <target name="some-target">
        //     <echo>some message</echo>
        // <target>
        AntLogger[] loggers = new AntLogger[] {new StandardLogger(15345L)};
        MockAntSession session = new MockAntSession(loggers, AntEvent.LOG_INFO);
        AntSession realSession = LoggerTrampoline.ANT_SESSION_CREATOR.makeAntSession(session);
        session.sendBuildStarted(makeAntEvent(realSession, null, -1, null, null, null));
        session.sendTargetStarted(makeAntEvent(realSession, null, -1, null, "some-target", null));
        session.sendMessageLogged(makeAntEvent(realSession, "some message", AntEvent.LOG_WARN, null, null, null));
        session.sendTargetFinished(makeAntEvent(realSession, null, -1, null, "some-target", null));
        session.sendBuildFinished(makeAntEvent(realSession, null, -1, null, null, null));
        List<Message> expectedMessages = Arrays.asList(new Message[] {
            new Message(NbBundle.getMessage(StandardLogger.class, "MSG_target_started_printed", "some-target"), false, null),
            new Message("some message", true, null),
            new Message(NbBundle.getMessage(StandardLogger.class, "FMT_finished_target_printed", new Integer(0), new Integer(15)), false, null),
        });
        assertEquals("correct text printed", expectedMessages, session.messages);
    }
    
    public void testMultilineMessagesSplit() throws Exception {
        // Tests what happens when messages with embedded newlines are sent.
        // Does not cover build-terminating errors; for that use testThrownErrors.
        AntLogger[] loggers = new AntLogger[] {new MockStackTraceLogger(), new StandardLogger(1000L)};
        MockAntSession session = new MockAntSession(loggers, AntEvent.LOG_INFO);
        AntSession realSession = LoggerTrampoline.ANT_SESSION_CREATOR.makeAntSession(session);
        session.sendBuildStarted(makeAntEvent(realSession, null, -1, null, null, null));
        session.sendMessageLogged(makeAntEvent(realSession, "Stack trace in separate lines:", AntEvent.LOG_INFO, null, null, null));
        session.sendMessageLogged(makeAntEvent(realSession, "\tat Foo.java:3", AntEvent.LOG_INFO, null, null, null));
        session.sendMessageLogged(makeAntEvent(realSession, "\tat Bar.java:5", AntEvent.LOG_INFO, null, null, null));
        session.sendMessageLogged(makeAntEvent(realSession, "Stack trace in one big line:\n" +
                                                            "\tat Quux.java:11\n" +
                                                            "\tat Baz.java:6\n" +
                                                            "...and an unrelated message",
            AntEvent.LOG_WARN, null, null, null));
        session.sendBuildFinished(makeAntEvent(realSession, null, -1, null, null, null));
        List<Message> expectedMessages = Arrays.asList(new Message[] {
            new Message("Stack trace in separate lines:", false, null),
            new Message("\tat Foo.java:3", false, new MockHyperlink("file:/src/Foo.java", "stack trace", 3, -1, -1, -1)),
            new Message("\tat Bar.java:5", false, new MockHyperlink("file:/src/Bar.java", "stack trace", 5, -1, -1, -1)),
            new Message("Stack trace in one big line:", true, null),
            new Message("\tat Quux.java:11", true, new MockHyperlink("file:/src/Quux.java", "stack trace", 11, -1, -1, -1)),
            new Message("\tat Baz.java:6", true, new MockHyperlink("file:/src/Baz.java", "stack trace", 6, -1, -1, -1)),
            new Message("...and an unrelated message", true, null),
            new Message(NbBundle.getMessage(StandardLogger.class, "FMT_finished_target_printed", new Integer(0), new Integer(1)), false, null),
        });
        assertEquals("correct text printed", expectedMessages, session.messages);
    }
    
    // XXX testVerbosityLevels
    // XXX testThrownErrors
    // XXX testFileHyperlinks
    // XXX testCaretShowingColumn
    
    /**
     * Create an event to be delivered.
     */
    private static AntEvent makeAntEvent(AntSession realSession, String message, int level, Throwable exc, String target, String task) {
        return LoggerTrampoline.ANT_EVENT_CREATOR.makeAntEvent(new MockAntEvent(realSession, message, level, exc, target, task));
    }

    /**
     * Minimal session implementation.
     * Supports delivery of various events, though it does no filtering beyond
     * filtering of messageLogged according to log level (has a custom verbosity).
     * Handles custom data and consumption of exceptions.
     * See {@link MockHyperlink} for hyperlink impl.
     * Display name always "Mock Session"; orig target is "mock-target"; orig script is "/tmp/mock-script".
     */
    private static final class MockAntSession implements LoggerTrampoline.AntSessionImpl {
        
        private final AntLogger[] loggers;
        private final int verbosity;
        private final Map<AntLogger,Object> customData = new WeakHashMap<AntLogger,Object>();
        private final Set<Throwable> consumedExceptions = new WeakSet();
        public final List<Message> messages = new ArrayList<Message>();
        
        public MockAntSession(AntLogger[] loggers, int verbosity) {
            this.loggers = loggers;
            this.verbosity = verbosity;
        }
        
        public void sendMessageLogged(AntEvent event) {
            for (AntLogger logger : loggers) {
                int[] levels = logger.interestedInLogLevels(event.getSession());
                Arrays.sort(levels);
                if (Arrays.binarySearch(levels, event.getLogLevel()) >= 0) {
                    logger.messageLogged(event);
                }
            }
        }
        
        public void sendBuildStarted(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.buildStarted(event);
            }
        }
        
        public void sendBuildFinished(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.buildFinished(event);
            }
        }
        
        public void sendBuildInitializationFailed(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.buildInitializationFailed(event);
            }
        }
        
        public void sendTargetStarted(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.targetStarted(event);
            }
        }
        
        public void sendTargetFinished(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.targetFinished(event);
            }
        }
        
        public void sendTaskStarted(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.taskStarted(event);
            }
        }
        
        public void sendTaskFinished(AntEvent event) {
            for (AntLogger logger : loggers) {
                logger.taskFinished(event);
            }
        }
        
        public void deliverMessageLogged(AntEvent originalEvent, String message, int level) {
            sendMessageLogged(makeAntEvent(originalEvent.getSession(), message, level, null, originalEvent.getTargetName(), originalEvent.getTaskName()));
        }

        public OutputListener createStandardHyperlink(URL file, String message, int line1, int column1, int line2, int column2) {
            return new MockHyperlink(file.toExternalForm(), message, line1, column1, line2, column2);
        }

        public void println(String message, boolean err, OutputListener listener) {
            messages.add(new Message(message, err, listener));
        }

        public void putCustomData(AntLogger logger, Object data) {
            customData.put(logger, data);
        }

        public boolean isExceptionConsumed(Throwable t) { // copied from NbBuildLogger
            if (consumedExceptions.contains(t)) {
                return true;
            }
            Throwable nested = t.getCause();
            if (nested != null && isExceptionConsumed(nested)) {
                consumedExceptions.add(t);
                return true;
            }
            return false;
        }

        public void consumeException(Throwable t) throws IllegalStateException { // copied from NbBuildLogger
            if (isExceptionConsumed(t)) {
                throw new IllegalStateException();
            }
            consumedExceptions.add(t);
        }

        public Object getCustomData(AntLogger logger) {
            return customData.get(logger);
        }

        public int getVerbosity() {
            return verbosity;
        }

        public String[] getOriginatingTargets() {
            return new String[] {"mock-target"};
        }

        public File getOriginatingScript() {
            return new File(System.getProperty("java.io.tmpdir"), "mock-script");
        }

        public String getDisplayName() {
            return "Mock Session";
        }
        
    }

    /**
     * Minimal event class.
     * Supports a message, level, exception, target and task.
     * Supports isConsumed/consume.
     */
    private static final class MockAntEvent implements LoggerTrampoline.AntEventImpl {
        
        private final AntSession session;
        private final String message;
        private final int level;
        private final Throwable exc;
        private final String target;
        private final String task;
        private boolean consumed = false;
        
        public MockAntEvent(AntSession session, String message, int level, Throwable exc, String target, String task) {
            this.session = session;
            this.message = message;
            this.level = level;
            this.exc = exc;
            this.target = target;
            this.task = task;
        }
        
        public String evaluate(String text) {
            return null;
        }

        public String getProperty(String name) {
            return null;
        }

        public boolean isConsumed() {
            return consumed;
        }

        public void consume() throws IllegalStateException {
            if (consumed) {
                throw new IllegalStateException();
            } else {
                consumed = true;
            }
        }

        public Throwable getException() {
            return exc;
        }

        public int getLine() {
            return -1;
        }

        public int getLogLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        public Set<String> getPropertyNames() {
            return Collections.emptySet();
        }

        public File getScriptLocation() {
            return null;
        }

        public AntSession getSession() {
            return session;
        }

        public String getTargetName() {
            return target;
        }

        public String getTaskName() {
            return task;
        }

        public TaskStructure getTaskStructure() {
            return null;
        }
        
    }
    
    /**
     * Struct representing a message printed to an output stream.
     */
    private static final class Message {
        
        public final String message;
        public final boolean err;
        public final MockHyperlink hyperlink;
        
        public Message(String message, boolean err, OutputListener listener) {
            this.message = message;
            this.err = err;
            if (listener instanceof MockHyperlink) {
                hyperlink = (MockHyperlink) listener;
            } else {
                hyperlink = null;
            }
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof Message) {
                Message m = (Message) o;
                return m.message.equals(message) && m.err == err && Utilities.compareObjects(m.hyperlink, hyperlink);
            } else {
                return false;
            }
        }
        
        @Override
        public String toString() {
            return "Message[" + message + "]" + (err ? "(err)" : "(out)") + (hyperlink != null ? "(" + hyperlink + ")" : "");
        }
        
    }

    /**
     * Struct representing a hyperlink in a message.
     */
    private static final class MockHyperlink implements OutputListener {
        
        public final String url;
        public final String message;
        public final int line1;
        public final int column1;
        public final int line2;
        public final int column2;
        
        public MockHyperlink(String url, String message, int line1, int column1, int line2, int column2) {
            this.url = url;
            this.message = message;
            this.line1 = line1;
            this.column1 = column1;
            this.line2 = line2;
            this.column2 = column2;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof MockHyperlink) {
                MockHyperlink h = (MockHyperlink) o;
                return h.url.equals(url) && h.message.equals(message) &&
                    h.line1 == line1 && h.column1 == column1 && h.line2 == line2 && h.column2 == column2;
            } else {
                return false;
            }
        }
        
        @Override
        public String toString() {
            return "MockHyperlink[" + url + ":" + line1 + ":" + column1 + ":" + line2 + ":" + column2 + ":" + message + "]";
        }

        public void outputLineSelected(OutputEvent ev) {}

        public void outputLineCleared(OutputEvent ev) {}

        public void outputLineAction(OutputEvent ev) {}
        
    }

    /**
     * Sample logger which hyperlinks stuff looking a bit like Java stack traces,
     * to test redelivery of logging events.
     */
    private static final class MockStackTraceLogger extends AntLogger {
        
        private static final Pattern STACK_TRACE_LINE = Pattern.compile("\tat ([a-zA-Z]+\\.java):([0-9]+)");
        
        public MockStackTraceLogger() {}

        @Override
        public void messageLogged(AntEvent event) {
            if (event.isConsumed()) {
                return;
            }
            Matcher m = STACK_TRACE_LINE.matcher(event.getMessage());
            if (m.matches()) {
                event.consume();
                String filename = m.group(1);
                int line = Integer.parseInt(m.group(2));
                AntSession session = event.getSession();
                URL u;
                try {
                    u = new URL("file:/src/" + filename);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                session.println(event.getMessage(),
                    event.getLogLevel() <= AntEvent.LOG_WARN,
                    session.createStandardHyperlink(u, "stack trace", line, -1, -1, -1));
            }
        }

        @Override
        public String[] interestedInTasks(AntSession session) {
            return AntLogger.ALL_TASKS;
        }

        @Override
        public String[] interestedInTargets(AntSession session) {
            return AntLogger.ALL_TARGETS;
        }

        @Override
        public boolean interestedInSession(AntSession session) {
            return true;
        }

        @Override
        public int[] interestedInLogLevels(AntSession session) {
            return new int[] {
                AntEvent.LOG_INFO,
                AntEvent.LOG_WARN,
                AntEvent.LOG_ERR,
            };
        }

        @Override
        public boolean interestedInAllScripts(AntSession session) {
            return true;
        }

        @Override
        public boolean interestedInScript(File script, AntSession session) {
            return true;
        }
        
    }
    
}
