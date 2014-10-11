/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.api.extexecution.base;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.extexecution.base.BaseExecutionDescriptor.InputProcessorFactory;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.input.InputReaderTask;
import org.netbeans.api.extexecution.base.input.InputReaders;
import org.netbeans.modules.extexecution.base.ProcessInputStream;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;

/**
 * Base Execution service provides the facility to execute a process.
 * <p>
 * All processes launched by this class are terminated on VM exit (if
 * these are not finished or terminated earlier).
 *
 * <div class="nonnormative">
 * <p>
 * Sample usage (ls command):
 * <pre>
 *     BaseExecutionDescriptor descriptor = new BaseExecutionDescriptor()
 *             .outProcessorFactory(new BaseExecutionDescriptor.InputProcessorFactory() {
 *
 *         &#64;Override
 *         public InputProcessor newInputProcessor() {
 *             return InputProcessors.copying(new BufferedWriter(new OutputStreamWriter(System.out)));
 *         }
 *     });
 *
 *     ProcessBuilder processBuilder = ProcessBuilder.getLocal();
 *     processBuilder.setExecutable(ls);
 *
 *     BaseExecutionService service = BaseExecutionService.newService(processBuilder, descriptor);
 *     Future&lt;Integer&gt; task = service.run();
 * </pre>
 * <p>
 * Even simpler usage but without displaying output (ls command):
 * <pre>
 *     ProcessBuilder processBuilder = ProcessBuilder.getLocal();
 *     processBuilder.setExecutable(ls);
 *
 *     ExecutionService service = ExecutionService.newService(processBuilder, new BaseExecutionDescriptor());
 *     Future&lt;Integer&gt; task = service.run();
 * </pre>
 * </div>
 *
 * @author Petr Hejl
 * @see #newService(java.util.concurrent.Callable, org.netbeans.api.extexecution.base.BaseExecutionDescriptor)
 * @see BaseExecutionDescriptor
 */
public final class BaseExecutionService {

    private static final Logger LOGGER = Logger.getLogger(BaseExecutionService.class.getName());

    private static final Set<Process> RUNNING_PROCESSES = new HashSet<Process>();

    private static final int EXECUTOR_SHUTDOWN_SLICE = 1000;

    private static final ExecutorService EXECUTOR_SERVICE = new RequestProcessor(BaseExecutionService.class.getName(), Integer.MAX_VALUE);

    static {

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                EXECUTOR_SERVICE.shutdown();

                synchronized (RUNNING_PROCESSES) {
                    for (Process process : RUNNING_PROCESSES) {
                        process.destroy();
                    }
                }
            }
        });
    }

    private final Callable<? extends Process> processCreator;

    private final BaseExecutionDescriptor descriptor;

    private BaseExecutionService(Callable<? extends Process> processCreator,
            BaseExecutionDescriptor descriptor) {
        this.processCreator = processCreator;
        this.descriptor = descriptor;
    }

    /**
     * Creates new execution service. Service will wrap up the processes
     * created by <code>processCreator</code> and will manage them.
     *
     * @param processCreator callable returning the process to wrap up
     * @param descriptor descriptor describing the configuration of service
     * @return new execution service
     */
    @NonNull
    public static BaseExecutionService newService(@NonNull Callable<? extends Process> processCreator,
            @NonNull BaseExecutionDescriptor descriptor) {
        return new BaseExecutionService(processCreator, descriptor);
    }

    /**
     * Runs the process described by this service. The call does not block
     * and the task is represented by the returned value. Integer returned
     * as a result of the {@link Future} is exit code of the process.
     * <p>
     * This method can be invoked multiple times returning the different and
     * unrelated {@link Future}s. On each call <code>Callable&lt;Process&gt;</code>
     * passed to {@link #newService(java.util.concurrent.Callable, org.netbeans.api.extexecution.base.BaseExecutionDescriptor)}
     * is invoked in order to create the process. If the process creation fails
     * (throwing an exception) returned <code>Future</code> will throw
     * {@link java.util.concurrent.ExecutionException} on {@link Future#get()}
     * request.
     * <p>
     * For details on execution control see {@link BaseExecutionDescriptor}.
     *
     * @return task representing the actual run, value representing result
     *             of the {@link Future} is exit code of the process
     */
    @NonNull
    public Future<Integer> run() {
        final Reader in;
        BaseExecutionDescriptor.ReaderFactory factory = descriptor.getInReaderFactory();
        if (factory != null) {
            in = factory.newReader();
        } else {
            in = null;
        }

        final CountDownLatch finishedLatch = new CountDownLatch(1);

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                boolean interrupted = false;
                Process process = null;
                Integer ret = null;
                ExecutorService executor = null;

                ProcessInputStream outStream = null;
                ProcessInputStream errStream = null;

                List<InputReaderTask> tasks = new ArrayList<InputReaderTask>();

                try {
                    final Runnable pre = descriptor.getPreExecution();
                    if (pre != null) {
                        pre.run();
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }

                    process = processCreator.call();
                    synchronized (RUNNING_PROCESSES) {
                        RUNNING_PROCESSES.add(process);
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }

                    outStream = new ProcessInputStream(process, process.getInputStream());
                    errStream = new ProcessInputStream(process, process.getErrorStream());

                    executor = Executors.newFixedThreadPool(in != null ? 3 : 2);

                    Charset charset = descriptor.getCharset();
                    if (charset == null) {
                        charset = Charset.defaultCharset();
                    }

                    tasks.add(InputReaderTask.newDrainingTask(
                        InputReaders.forStream(new BufferedInputStream(outStream), charset),
                        createOutProcessor()));
                    tasks.add(InputReaderTask.newDrainingTask(
                        InputReaders.forStream(new BufferedInputStream(errStream), charset),
                        createErrProcessor()));
                    if (in != null) {
                        tasks.add(InputReaderTask.newTask(
                            InputReaders.forReader(in),
                            createInProcessor(process.getOutputStream(), charset)));
                    }
                    for (InputReaderTask task : tasks) {
                        executor.submit(task);
                    }

                    process.waitFor();
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.FINE, null, ex);
                    interrupted = true;
                } catch (Throwable t) {
                    LOGGER.log(Level.INFO, null, t);
                    throw new WrappedException(t);
                } finally {
                    try {
                        // fully evaluated - we want to clear interrupted status in any case
                        interrupted |= Thread.interrupted();

                        if (!interrupted) {
                            if (outStream != null) {
                                outStream.close(true);
                            }
                            if (errStream != null) {
                                errStream.close(true);
                            }
                        }

                        if (process != null) {
                            process.destroy();
                            synchronized (RUNNING_PROCESSES) {
                                RUNNING_PROCESSES.remove(process);
                            }

                            try {
                                ret = process.exitValue();
                            } catch (IllegalThreadStateException ex) {
                                LOGGER.log(Level.FINE, "Process not yet exited", ex);
                            }
                        }
                    } catch (Throwable t) {
                        LOGGER.log(Level.INFO, null, t);
                        throw new WrappedException(t);
                    } finally {
                        try {
                            cleanup(tasks, executor);

                            final ParametrizedRunnable<Integer> post
                                    = descriptor.getPostExecution();
                            if (post != null) {
                                post.run(ret);
                            }
                        } finally {
                            finishedLatch.countDown();
                            if (interrupted) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }

                return ret;
            }
        };

        final FutureTask<Integer> current = new FutureTask<Integer>(callable) {

            @Override
            protected void setException(Throwable t) {
                if (t instanceof WrappedException) {
                    super.setException(((WrappedException) t).getCause());
                } else {
                    super.setException(t);
                }
            }

        };

        EXECUTOR_SERVICE.execute(current);
        return current;
    }

    private void cleanup(final List<InputReaderTask> tasks, final ExecutorService processingExecutor) {
        boolean interrupted = false;
        if (processingExecutor != null) {
            try {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        processingExecutor.shutdown();
                        return null;
                    }
                });
                for (Cancellable cancellable : tasks) {
                    cancellable.cancel();
                }
                while (!processingExecutor.awaitTermination(EXECUTOR_SHUTDOWN_SLICE, TimeUnit.MILLISECONDS)) {
                    LOGGER.log(Level.INFO, "Awaiting processing finish");
                }
            } catch (InterruptedException ex) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private InputProcessor createOutProcessor() {
        InputProcessor outProcessor = null;
        InputProcessorFactory descriptorOutFactory = descriptor.getOutProcessorFactory();
        if (descriptorOutFactory != null) {
            outProcessor = descriptorOutFactory.newInputProcessor();
        }

        return outProcessor;
    }

    private InputProcessor createErrProcessor() {
        InputProcessor errProcessor = null;
        InputProcessorFactory descriptorErrFactory = descriptor.getErrProcessorFactory();
        if (descriptorErrFactory != null) {
            errProcessor = descriptorErrFactory.newInputProcessor();
        }

        return errProcessor;
    }

    private InputProcessor createInProcessor(OutputStream os, Charset charset) {
        return InputProcessors.copying(new OutputStreamWriter(os, charset));
    }

    private static class WrappedException extends Exception {

        public WrappedException(Throwable cause) {
            super(cause);
        }

    }
}
