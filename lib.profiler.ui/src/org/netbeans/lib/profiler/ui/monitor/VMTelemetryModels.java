/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package org.netbeans.lib.profiler.ui.monitor;

import org.netbeans.lib.profiler.charts.swing.LongRect;
import org.netbeans.lib.profiler.charts.Timeline;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItem;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItemsModel;
import org.netbeans.lib.profiler.results.DataManagerListener;
import org.netbeans.lib.profiler.results.monitor.VMTelemetryDataManager;
import org.netbeans.lib.profiler.ui.charts.xy.ProfilerGCXYItem;
import org.netbeans.lib.profiler.ui.graphs.GraphsUI;

/**
 *
 * @author Jiri Sedlacek
 */
public final class VMTelemetryModels {

    // --- Instance variables --------------------------------------------------

    private final VMTelemetryDataManager dataManager;

    private final Timeline timeline;
    private final SynchronousXYItemsModel memoryItemsModel;
    private final SynchronousXYItemsModel generationsItemsModel;
    private final SynchronousXYItemsModel threadsItemsModel;


    // --- Constructor ---------------------------------------------------------

    public VMTelemetryModels(VMTelemetryDataManager dataManager) {
        this.dataManager = dataManager;

        timeline = createTimeline();
        memoryItemsModel = createMemoryItemsModel(timeline);
        generationsItemsModel = createGenerationsItemsModel(timeline);
        threadsItemsModel = createThreadsItemsModel(timeline);

        dataManager.addDataListener(new DataManagerListener() {
            public void dataChanged() { dataChangedImpl(); }
            public void dataReset() { dataResetImpl(); }
        });
    }


    // --- Public interface ----------------------------------------------------

    public VMTelemetryDataManager getDataManager() {
        return dataManager;
    }

    public SynchronousXYItemsModel memoryItemsModel() {
        return memoryItemsModel;
    }

    public SynchronousXYItemsModel generationsItemsModel() {
        return generationsItemsModel;
    }

    public SynchronousXYItemsModel threadsItemsModel() {
        return threadsItemsModel;
    }


    // --- DataManagerListener implementation ----------------------------------

    private void dataChangedImpl() {
        memoryItemsModel.valuesAdded();
        generationsItemsModel.valuesAdded();
        threadsItemsModel.valuesAdded();
    }

    private void dataResetImpl() {
        memoryItemsModel.valuesReset();
        generationsItemsModel.valuesReset();
        threadsItemsModel.valuesReset();
    }


    // --- Private implementation ----------------------------------------------

    private Timeline createTimeline() {
        return new Timeline() {
            public int getTimestampsCount() { return dataManager.getItemCount(); }
            public long getTimestamp(int index) { return dataManager.timeStamps[index]; }
        };
    }

    private SynchronousXYItemsModel createMemoryItemsModel(Timeline timeline) {
        // Heap size
        SynchronousXYItem heapSizeItem = new SynchronousXYItem(GraphsUI.HEAP_SIZE_NAME, 0) {
            public long getYValue(int index) {
                return dataManager.totalMemory[index];
            }
        };
        heapSizeItem.setInitialBounds(new LongRect(0, 0, 0, GraphsUI.HEAP_SIZE_INITIAL_VALUE));

        // Used heap
        SynchronousXYItem usedHeapItem = new SynchronousXYItem(GraphsUI.USED_HEAP_NAME, 0) {
            public long getYValue(int index) {
                return dataManager.usedMemory[index];
            }
        };
        usedHeapItem.setInitialBounds(new LongRect(0, 0, 0, GraphsUI.USED_HEAP_INITIAL_VALUE));

        // Model
        SynchronousXYItemsModel model = new SynchronousXYItemsModel(timeline,
                           new SynchronousXYItem[] { heapSizeItem, usedHeapItem });

        return model;
    }

    private SynchronousXYItemsModel createGenerationsItemsModel(Timeline timeline) {
        // Surviving generations
        SynchronousXYItem survivingGenerationsItem = new SynchronousXYItem(GraphsUI.SURVGEN_NAME, 0) {
            public long getYValue(int index) {
                return dataManager.nSurvivingGenerations[index];
            }
        };
        survivingGenerationsItem.setInitialBounds(new LongRect(0, 0, 0, GraphsUI.SURVGEN_INITIAL_VALUE));

        // Relative time spent in GC
        SynchronousXYItem gcTimeItem = new SynchronousXYItem(GraphsUI.GC_TIME_NAME, 0, 1000) {
            public long getYValue(int index) {
                return dataManager.relativeGCTimeInPerMil[index];
            }
        };
        gcTimeItem.setInitialBounds(new LongRect(0, 0, 0, 1000));

        // GC intervals
        ProfilerGCXYItem gcIntervalsItem = new ProfilerGCXYItem("") { // NOI18N

            public long[] getGCStarts(int index) {
                return dataManager.gcStarts[index];
            }

            public long[] getGCEnds(int index) {
                return dataManager.gcFinishs[index];
            }

        };

        // Model
        SynchronousXYItemsModel model = new SynchronousXYItemsModel(timeline,
                 new SynchronousXYItem[] { gcIntervalsItem,
                                        survivingGenerationsItem,
                                        gcTimeItem });

        return model;
    }

    private SynchronousXYItemsModel createThreadsItemsModel(Timeline timeline) {
        // Threads
        SynchronousXYItem threadsItem = new SynchronousXYItem(GraphsUI.THREADS_NAME, 0) {
            public long getYValue(int index) {
                return dataManager.nTotalThreads[index];
            }
        };
        threadsItem.setInitialBounds(new LongRect(0, 0, 0, GraphsUI.THREADS_INITIAL_VALUE));

        // Loaded classes
        SynchronousXYItem loadedClassesItem = new SynchronousXYItem(GraphsUI.LOADED_CLASSES_NAME, 0) {
            public long getYValue(int index) {
                return dataManager.loadedClassesCount[index];
            }
        };
        loadedClassesItem.setInitialBounds(new LongRect(0, 0, 0, GraphsUI.LOADED_CLASSES_INITIAL_VALUE));

        // Model
        SynchronousXYItemsModel model = new SynchronousXYItemsModel(timeline,
                       new SynchronousXYItem[] { threadsItem, loadedClassesItem });

        return model;
    }

}
