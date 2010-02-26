/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.nativeexecution.terminal.spi.impl;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.netbeans.lib.terminalemulator.Term;
import org.netbeans.modules.nativeexecution.api.NativeProcess;
import org.netbeans.modules.nativeexecution.api.pty.PtySupport;
import org.netbeans.modules.nativeexecution.api.pty.PtySupport.Pty;
import org.netbeans.modules.nativeexecution.spi.pty.IOConnector;
import org.netbeans.modules.nativeexecution.spi.pty.PtyImpl;
import org.netbeans.modules.nativeexecution.spi.support.pty.PtyImplAccessor;
import org.netbeans.modules.terminal.api.IOResizable;
import org.netbeans.modules.terminal.api.IOTerm;
import org.netbeans.nativeexecution.terminal.spi.impl.PtyCreatorImpl.PtyImplementation;
import org.netbeans.terminal.example.TerminalIOProviderSupport;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author ak119685
 */
@ServiceProvider(service = IOConnector.class)
public class IOConnectorImpl implements IOConnector {

    public IOConnectorImpl() {
    }

    public boolean connect(final InputOutput io, final NativeProcess process) {
	if (!IOTerm.isSupported(io))
	    return false;

	Term term = IOTerm.term(io);
	if (term == null)
	    return false;

        final Pty pty = PtySupport.getPty(process);
        final PtyImpl ptyImpl = PtyImplAccessor.getDefault().getImpl(pty);

        TerminalIOProviderSupport.setInternal(io, ptyImpl == null);

        if (ptyImpl == null || !(ptyImpl instanceof PtyImplementation)) {
            IOTerm.connect(io, process.getOutputStream(), process.getInputStream(), process.getErrorStream());
        } else {
            PtyImplementation impl = (PtyImplementation) ptyImpl;
            IOTerm.connect(io, impl.getOutputStream(), impl.getInputStream(), process.getErrorStream());

            if (IOResizable.isSupported(io)) {
                IOResizable.addListener(io, new ResizeListener(impl));
            }

            RequestProcessor.getDefault().post(new Reaper(io, process, impl));
        }

        return true;
    }

    public boolean connect(final InputOutput io, final Pty pty) {
        if (pty == null || io == null) {
            throw new NullPointerException();
        }

	if (!IOTerm.isSupported(io))
	    return false;

	Term term = IOTerm.term(io);
	if (term == null)
	    return false;

        final PtyImpl ptyImpl = PtyImplAccessor.getDefault().getImpl(pty);

        if (!(ptyImpl instanceof PtyImplementation)) {
            return false;
        }

        TerminalIOProviderSupport.setInternal(io, false);

        PtyImplementation impl = (PtyImplementation) ptyImpl;
        IOTerm.connect(io, impl.getOutputStream(), impl.getInputStream(), impl.getErrorStream());

        if (IOResizable.isSupported(io)) {
            IOResizable.addListener(io, new ResizeListener(impl));
        }

        return true;
    }

    private static class ResizeListener implements IOResizable.Listener {

        private Task task = null;
        private Dimension cells;
        private Dimension pixels;

        public ResizeListener(final PtyImplementation pty) {
            this.task = RequestProcessor.getDefault().create(new Runnable() {

                public void run() {
                    Dimension c, p;

                    synchronized (ResizeListener.this) {
                        c = new Dimension(cells);
                        p = new Dimension(pixels);
                    }

                    pty.masterTIOCSWINSZ(c.width, c.height,
                            p.width, p.height);
                }
            }, true);
        }

        public synchronized void sizeChanged(Dimension cells, Dimension pixels) {
            if (cells == null || pixels == null) {
                throw new NullPointerException();
            }

            if (cells.equals(this.cells) && pixels.equals(this.pixels)) {
                return;
            }

            this.cells = new Dimension(cells);
            this.pixels = new Dimension(pixels);
            task.schedule(1000);
        }
    }

    private final static class Reaper implements Runnable {

        private final NativeProcess process;
        private final PtyImplementation pty;
        private final InputOutput io;

        public Reaper(final InputOutput io, final NativeProcess process, final PtyImplementation pty) {
            this.process = process;
            this.pty = pty;
            this.io = io;
        }

        @Override
        public void run() {
            try {
                Thread.currentThread().setName("ptysatellite reaper for " + process.getPID()); // NOI18N
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            try {
                process.waitFor();
                pty.close();
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        io.closeInputOutput();
                    }
                });

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
