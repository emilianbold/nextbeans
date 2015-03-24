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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.apt.impl.support.clank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.netbeans.modules.cnd.antlr.TokenStream;
import org.netbeans.modules.cnd.apt.debug.APTTraceFlags;
import org.netbeans.modules.cnd.apt.support.APTFileSearch;
import org.netbeans.modules.cnd.apt.support.ClankDriver;
import org.netbeans.modules.cnd.apt.support.IncludeDirEntry;
import org.netbeans.modules.cnd.apt.support.api.PPIncludeHandler;
import org.netbeans.modules.cnd.apt.support.api.StartEntry;
import org.netbeans.modules.cnd.apt.utils.APTUtils;
import org.netbeans.modules.cnd.repository.spi.Persistent;
import org.netbeans.modules.cnd.repository.spi.RepositoryDataInput;
import org.netbeans.modules.cnd.repository.spi.RepositoryDataOutput;
import org.netbeans.modules.cnd.repository.support.SelfPersistent;
import org.openide.filesystems.FileSystem;
import org.openide.util.CharSequences;
import org.openide.util.Parameters;

/**
 * implementation of include handler based on clank's Preprocessor
 * @author Vladimir Voskresensky
 */
public class ClankIncludeHandlerImpl implements PPIncludeHandler {
    private List<IncludeDirEntry> systemIncludePaths;
    private List<IncludeDirEntry> userIncludePaths;
    private List<IncludeDirEntry> userIncludeFilePaths;

    private StartEntry startFile;
    private final APTFileSearch fileSearch;
    private static final ClankDriver.APTTokenStreamCache NO_TOKENS = new APTTokenStreamCacheImpl(-1);

    private int inclStackIndex;
    private ClankDriver.APTTokenStreamCache cachedTokens = NO_TOKENS;
    private LinkedList<IncludeInfo> inclStack = null;    

    public  ClankIncludeHandlerImpl(StartEntry startFile) {
        this(startFile, new ArrayList<IncludeDirEntry>(0), new ArrayList<IncludeDirEntry>(0), new ArrayList<IncludeDirEntry>(0), startFile.getFileSearch());
    }
    
    public ClankIncludeHandlerImpl(StartEntry startFile,
                                    List<IncludeDirEntry> systemIncludePaths,
                                    List<IncludeDirEntry> userIncludePaths,
                                    List<IncludeDirEntry> userIncludeFilePaths, APTFileSearch fileSearch) {
        assert APTTraceFlags.USE_CLANK;
        Parameters.notNull("startFile", startFile);
        this.startFile = startFile;
        this.systemIncludePaths = systemIncludePaths;
        this.userIncludePaths = userIncludePaths;
        this.userIncludeFilePaths = userIncludeFilePaths;
        this.fileSearch = fileSearch;
        this.inclStackIndex = 0;
    }

    @Override
    public IncludeState pushInclude(FileSystem fs, CharSequence path, int line, int offset, int resolvedDirIndex) {
        return pushIncludeImpl(fs, path, line, offset, resolvedDirIndex);
    }

    @Override
    public CharSequence popInclude() {
        return popIncludeImpl();
    }
    
    @Override
    public StartEntry getStartEntry() {
        return startFile;
    }
    
    private CharSequence getCurPath() {
        assert (inclStack != null);
        IncludeInfo info = inclStack.getLast();
        return info.getIncludedPath();
    }
    
    private int getCurDirIndex() {
        if (inclStack != null && !inclStack.isEmpty()) {
            IncludeInfo info = inclStack.getLast();
            return info.getIncludedDirIndex();
        } else {
            return 0;
        }
    }    
    ////////////////////////////////////////////////////////////////////////////
    // manage state (save/restore)
    
    @Override
    public State getState() {
        return createStateImpl();
    }
    
    @Override
    public void setState(State state) {
        if (state instanceof StateImpl) {
	    StateImpl stateImpl = ((StateImpl)state);
	    assert ! stateImpl.isCleaned();
            stateImpl.restoreTo(this);
        }
    }
    
    private StateImpl createStateImpl() {
        return new StateImpl(this);
    }

    public List<IncludeDirEntry> getUserIncludeFilePaths() {
        return Collections.unmodifiableList(userIncludeFilePaths);
    }

    public List<IncludeDirEntry> getUserIncludePaths() {
        return Collections.unmodifiableList(userIncludePaths);
    }

    public List<IncludeDirEntry> getSystemIncludePaths() {
        return Collections.unmodifiableList(systemIncludePaths);
    }

    /*package*/boolean isFirstLevel() {
        return inclStack == null || inclStack.isEmpty();
    }
    
    public ClankDriver.APTTokenStreamCache getCachedTokens() {
        if (cachedTokens == NO_TOKENS) {
            return new APTTokenStreamCacheImpl(inclStackIndex);
        } else {
            return cachedTokens;
        }
    }

    public int getInclStackIndex() {
        return inclStackIndex;
    }

    void cacheTokens(ClankDriver.APTTokenStreamCache cache) {
        assert cache != null;
        if (!cache.hasTokenStream()) {
            this.cachedTokens = NO_TOKENS;
        } else {
            this.cachedTokens = cache;
        }
        this.inclStackIndex = cache.getFileIndex();
    }
    
    /** immutable state object of include handler */
    // Not SelfPersistent any more because I have to pass unitIndex into write() method
    // It is private, so I don't think it's a problem. VK.
    public final static class StateImpl implements State, Persistent  {
        private static final List<IncludeDirEntry> CLEANED_MARKER = Collections.unmodifiableList(new ArrayList<IncludeDirEntry>(0));
        // for now just remember lists
        private final List<IncludeDirEntry> systemIncludePaths;
        private final List<IncludeDirEntry> userIncludePaths;
        private final List<IncludeDirEntry> userIncludeFilePaths;
        private final StartEntry   startFile;

        private final int inclStackIndex;
        private final ClankDriver.APTTokenStreamCache cachedTokens;
        private static final IncludeInfo[] EMPTY_STACK = new IncludeInfo[0];
        private final IncludeInfo[] inclStack;        
        private int hashCode = 0;
        
        protected StateImpl(ClankIncludeHandlerImpl handler) {
            this.systemIncludePaths = handler.systemIncludePaths;
            this.userIncludePaths = handler.userIncludePaths;
            this.userIncludeFilePaths = handler.userIncludeFilePaths;
            this.startFile = handler.startFile;

            if (handler.inclStack != null && !handler.inclStack.isEmpty()) {
                this.inclStack = handler.inclStack.toArray(new IncludeInfo[handler.inclStack.size()]);
            } else {
                this.inclStack = EMPTY_STACK;
            }
            
            this.inclStackIndex = handler.inclStackIndex;
            this.cachedTokens = handler.cachedTokens;
            assert this.cachedTokens != null;
        }
        
        private StateImpl(StateImpl other, boolean cleanState) {
            // shared information
            this.startFile = other.startFile;
            
            // state object is immutable => safe to share stacks
            this.inclStack = other.inclStack;
            
            this.inclStackIndex = other.inclStackIndex;
            if (cleanState) {
                this.systemIncludePaths = CLEANED_MARKER;
                this.userIncludePaths = CLEANED_MARKER;
                this.userIncludeFilePaths = CLEANED_MARKER;
                this.cachedTokens = NO_TOKENS;
            } else {
                this.systemIncludePaths = other.systemIncludePaths;
                this.userIncludePaths = other.userIncludePaths;
                this.userIncludeFilePaths = other.userIncludeFilePaths;
                this.cachedTokens = other.cachedTokens;
            }
            assert this.cachedTokens != null;
        }
        
        int getIncludeStackDepth() {
            return inclStack.length;
        }
        
        private void restoreTo(ClankIncludeHandlerImpl handler) {
            handler.userIncludePaths = this.userIncludePaths;
            handler.userIncludeFilePaths = this.userIncludeFilePaths;
            handler.systemIncludePaths = this.systemIncludePaths;
            handler.startFile = this.startFile;
            
            handler.inclStackIndex = this.inclStackIndex;
            handler.cachedTokens = this.cachedTokens;
            // do not restore include info if state is cleaned
            if (!isCleaned()) {
                if (this.inclStack.length > 0) {
                    handler.inclStack = new LinkedList<IncludeInfo>();
                    handler.inclStack.addAll(Arrays.asList(this.inclStack));
//                    if (CHECK_INCLUDE_DEPTH < 0) {
//                        handler.recurseIncludes = new HashMap<CharSequence, Integer>();
//                        for (IncludeInfo includeInfo : this.inclStack) {
//                            CharSequence path = includeInfo.getIncludedPath();
//                            Integer counter = handler.recurseIncludes.get(path);
//                            counter = (counter == null) ? Integer.valueOf(1) : Integer.valueOf(counter.intValue() + 1);
//                            handler.recurseIncludes.put(path, counter);
//                        }
//                    }
                }
            }
        }

        @Override
        public String toString() {
            return ClankIncludeHandlerImpl.toString(startFile.getStartFile(), systemIncludePaths, userIncludePaths, userIncludeFilePaths, inclStackIndex, Arrays.asList(inclStack));
        }
        
        public void write(RepositoryDataOutput output) throws IOException {
            assert output != null;
            startFile.write(output);
            
            assert systemIncludePaths != null;
            assert userIncludePaths != null;
            
            int size = systemIncludePaths.size();
            output.writeInt(size);
            for (int i = 0; i < size; i++) {
                IncludeDirEntry inc = systemIncludePaths.get(i);
                output.writeFileSystem(inc.getFileSystem());
                output.writeFilePathForFileSystem(inc.getFileSystem(), inc.getAsSharedCharSequence());
            }
            
            size = userIncludePaths.size();
            output.writeInt(size);
            
            for (int i = 0; i < size; i++) {
                IncludeDirEntry inc = userIncludePaths.get(i);
                output.writeFileSystem(inc.getFileSystem());
                output.writeFilePathForFileSystem(inc.getFileSystem(), inc.getAsSharedCharSequence());
            }
            
            size = userIncludeFilePaths.size();
            output.writeInt(size);
            
            for (int i = 0; i < size; i++) {
                IncludeDirEntry inc = userIncludeFilePaths.get(i);
                output.writeFileSystem(inc.getFileSystem());
                output.writeFilePathForFileSystem(inc.getFileSystem(), inc.getAsSharedCharSequence());
            }

            output.writeInt(inclStackIndex);
            
            output.writeInt(inclStack.length);
            for (IncludeInfo inclInfo : inclStack) {
                assert inclInfo != null;
                final IncludeInfoImpl inclInfoImpl;
                if (inclInfo instanceof IncludeInfoImpl) {
                    inclInfoImpl = (IncludeInfoImpl) inclInfo;
                } else {
                    inclInfoImpl = new IncludeInfoImpl(
                            inclInfo.getFileSystem(),
                            inclInfo.getIncludedPath(),
                            inclInfo.getIncludeDirectiveLine(),
                            inclInfo.getIncludeDirectiveOffset(),
                            inclInfo.getIncludedDirIndex());
                }
                assert inclInfoImpl != null;
                inclInfoImpl.write(output);
            }            
        }
        
        public StateImpl(final RepositoryDataInput input) throws IOException {
            assert input != null;
            
            startFile = new StartEntry(input);
            
            int size = input.readInt();
            systemIncludePaths = new ArrayList<IncludeDirEntry>(size);
            for (int i = 0; i < size; i++) {
                FileSystem fs = input.readFileSystem();
                IncludeDirEntry path = IncludeDirEntry.get(fs, input.readFilePathForFileSystem(fs).toString());
                systemIncludePaths.add(i, path);
            }
            
            size = input.readInt();
            userIncludePaths = new ArrayList<IncludeDirEntry>(size);
            for (int i = 0; i < size; i++) {
                FileSystem fs = input.readFileSystem();
                IncludeDirEntry path = IncludeDirEntry.get(fs, input.readFilePathForFileSystem(fs).toString());
                userIncludePaths.add(i, path);
            }

            size = input.readInt();
            userIncludeFilePaths = new ArrayList<IncludeDirEntry>(size);
            for (int i = 0; i < size; i++) {
                FileSystem fs = input.readFileSystem();
                IncludeDirEntry path = IncludeDirEntry.get(fs, input.readFilePathForFileSystem(fs).toString());
                userIncludeFilePaths.add(i, path);
            }
            
            inclStackIndex = input.readInt();
            cachedTokens = NO_TOKENS;
            assert this.cachedTokens != null;
            
            size = input.readInt();
            
            if (size == 0) {
                inclStack = EMPTY_STACK;
            } else {
                inclStack = new IncludeInfo[size];
                for (int i = 0; i < size; i++) {
                    final IncludeInfo impl = new IncludeInfoImpl(input);
                    assert impl != null;
                    inclStack[i] = impl;
                }
            }            
        }        
	
        public final StartEntry getStartEntry() {
            return startFile;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || (obj.getClass() != this.getClass())) {
                return false;
            }
            StateImpl other = (StateImpl)obj;
            return this.startFile.equals(other.startFile) &&
                    (this.inclStackIndex == other.inclStackIndex) &&
                    compareStacks(this.inclStack, other.inclStack);
        }

        @Override
        public int hashCode() {
            int hash = hashCode;
            if (hash == 0) {
                hash = 5;
                hash = 67 * hash + (this.startFile != null ? this.startFile.hashCode() : 0);
                hash = 67 * hash + this.inclStackIndex;
                hashCode = hash;
            }
            return hash;
        }
        
        private boolean compareStacks(IncludeInfo[] inclStack1, IncludeInfo[] inclStack2) {
            if (inclStack1 == inclStack2) {
                return true;
            }
            if (inclStack1.length != inclStack2.length) {
                return false;
            }
            for (int i = 0; i < inclStack1.length; i++) {
                IncludeInfo cur1 = inclStack1[i];
                IncludeInfo cur2 = inclStack2[i];
                if (!cur1.equals(cur2)) {
                    return false;
                }
            }
            return true;
        } 
        
        public Collection<IncludeInfo> getIncludeStack() {
            return Arrays.asList(this.inclStack);
        }
        
        public  boolean isCleaned() {
            return this.userIncludeFilePaths == CLEANED_MARKER; // was created as clean state
        }
        
        public  ClankIncludeHandlerImpl.State copy(boolean cleanState) {
            return new StateImpl(this, cleanState);
        }
        
        public  List<IncludeDirEntry> getSysIncludePaths() {
            return this.systemIncludePaths;
        }
        
        public  List<IncludeDirEntry> getUserIncludePaths() {
            return this.userIncludePaths;
        }        

        public  List<IncludeDirEntry> getUserIncludeFilePaths() {
            return this.userIncludeFilePaths;
        }

        public int getIncludeStackIndex() {
            return this.inclStackIndex;
        }
    }
    
    private IncludeState pushIncludeImpl(FileSystem fs, CharSequence path, int directiveLine, int directiveOffset, int resolvedDirIndex) {
        assert CharSequences.isCompact(path) : "must be char sequence key " + path; // NOI18N
        boolean okToPush = true;
//        if (CHECK_INCLUDE_DEPTH > 0) {
//            // variant without hash map
//            if (inclStack == null) {
//                inclStack = new LinkedList<IncludeInfo>();
//            }
//            if (inclStack.size() > CHECK_INCLUDE_DEPTH) {
//                APTUtils.LOG.log(Level.WARNING, "Deep inclusion:{0} in {1} on level {2}", new Object[] { path , getCurPath() , inclStack.size() }); // NOI18N
//                // check recurse inclusion
//                int counter = 0;
//                for (IncludeInfo includeInfo : inclStack) {
//                    if (includeInfo.getIncludedPath().equals(path)) {
//                        counter++;
//                        if (counter > MAX_INCLUDE_FILE_DEEP) {
//                            okToPush = false;
//                            break;
//                        }
//                    }
//                }
//            }
//        } else {
//            // variant with old hash map
//            if (recurseIncludes == null) {
//                assert (inclStack == null) : inclStack.toString() + " started on " + startFile;
//                inclStack = new LinkedList<IncludeInfo>();
//                recurseIncludes = new HashMap<CharSequence, Integer>();
//            }
//            Integer counter = recurseIncludes.get(path);
//            counter = (counter == null) ? Integer.valueOf(1) : Integer.valueOf(counter.intValue() + 1);
//            if (counter.intValue() < MAX_INCLUDE_FILE_DEEP) {
//                recurseIncludes.put(path, counter);
//            } else {
//                okToPush = false;
//            }
//        }
        if (inclStack == null) {
            inclStack = new LinkedList<IncludeInfo>();
        }
        if (okToPush) {
            inclStack.addLast(new IncludeInfoImpl(fs, path, directiveLine, directiveOffset, resolvedDirIndex));
            return IncludeState.Success;
        } else {
            APTUtils.LOG.log(Level.WARNING, "RECURSIVE inclusion:\n\t{0}\n\tin {1}\n", new Object[] { path , getCurPath() }); // NOI18N
            return IncludeState.Recursive;
        }
    }    

    private static final class IncludeInfoImpl implements IncludeInfo, SelfPersistent {
        private final FileSystem fs;
        private final CharSequence path;
        private final int directiveLine;
        private final int directiveOffset;
        private final int resolvedDirIndex;
        
        public IncludeInfoImpl(FileSystem fs, CharSequence path, int directiveLine, int directiveOffset, int resolvedDirIndex) {
            assert path != null;
            this.fs = fs;
            this.path = path;
            // in case of -include file we have negative line/offset
            assert directiveLine >= 0 || (directiveLine < 0 && directiveOffset < 0);
            this.directiveLine = directiveLine;
            this.directiveOffset = directiveOffset;
            this.resolvedDirIndex = resolvedDirIndex;
        }
        
        public IncludeInfoImpl(final RepositoryDataInput input) throws IOException {
            assert input != null;
            this.fs = input.readFileSystem();
            this.path = input.readFilePathForFileSystem(fs);
            directiveLine = input.readInt();
            directiveOffset = input.readInt();
            resolvedDirIndex = input.readInt();
        }

        @Override
        public FileSystem getFileSystem() {
            return fs;
        }
        
        @Override
        public CharSequence getIncludedPath() {
            return path;
        }

        @Override
        public int getIncludeDirectiveLine() {
            return directiveLine;
        }

        @Override
        public int getIncludeDirectiveOffset() {
            return directiveOffset;
        }

        @Override
        public String toString() {
            String retValue;
            
            retValue = "(" + getIncludeDirectiveLine() + "/" + getIncludeDirectiveOffset() + ": " + // NOI18N
                    getIncludedPath() + ":" + getIncludedDirIndex() + ")"; // NOI18N
            return retValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || (obj.getClass() != this.getClass())) {
                return false;
            }
            IncludeInfoImpl other = (IncludeInfoImpl)obj;
            return this.directiveLine == other.directiveLine && this.directiveOffset == other.directiveOffset &&
                    this.path.equals(other.path) && (resolvedDirIndex == other.resolvedDirIndex);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 73 * hash + (this.path != null ? this.path.hashCode() : 0);
            hash = 73 * hash + this.directiveLine;
            hash = 73 * hash + this.directiveOffset;
            hash = 73 * hash + this.resolvedDirIndex;
            return hash;
        }

        public void write(final RepositoryDataOutput output) throws IOException {
            assert output != null;
            output.writeFileSystem(fs);
            output.writeFilePathForFileSystem(fs, path);
            output.writeInt(directiveLine);
            output.writeInt(directiveOffset);
            output.writeInt(resolvedDirIndex);
        }

        @Override
        public int getIncludedDirIndex() {
            return this.resolvedDirIndex;
        }
    }
      
    private CharSequence popIncludeImpl() {
        assert (inclStack != null);
        assert (!inclStack.isEmpty());
        IncludeInfo inclInfo = inclStack.removeLast();
        CharSequence path = inclInfo.getIncludedPath();
//        if (CHECK_INCLUDE_DEPTH < 0) {
//            assert (recurseIncludes != null);
//            Integer counter = recurseIncludes.remove(path);
//            assert (counter != null) : "must be added before"; // NOI18N
//            // decrease include counter
//            counter = Integer.valueOf(counter.intValue()-1);
//            assert (counter.intValue() >= 0) : "can't be negative"; // NOI18N
//            if (counter.intValue() != 0) {
//                recurseIncludes.put(path, counter);
//            }
//        }
        return path;
    }
    
    @Override
    public String toString() {
        return ClankIncludeHandlerImpl.toString(startFile.getStartFile(), systemIncludePaths, userIncludePaths, userIncludeFilePaths, inclStackIndex, this.inclStack);
    }    
    
    private static String toString(CharSequence startFile,
                                    List<IncludeDirEntry> systemIncludePaths,
                                    List<IncludeDirEntry> userIncludePaths,
                                    List<IncludeDirEntry> userIncludeFilePaths,
                                    int inclStackIndex,
                                    Collection<IncludeInfo> inclStack) {
        StringBuilder retValue = new StringBuilder();
        if (!userIncludeFilePaths.isEmpty()) {
            retValue.append("User File Includes:\n"); // NOI18N
            retValue.append(APTUtils.includes2String(userIncludeFilePaths));
        }
        retValue.append("User includes:\n"); // NOI18N
        retValue.append(APTUtils.includes2String(userIncludePaths));
        retValue.append("\nSys includes:\n"); // NOI18N
        retValue.append(APTUtils.includes2String(systemIncludePaths));
        retValue.append("\nInclude Stack starting from:\n"); // NOI18N
        retValue.append(startFile).append("\n"); // NOI18N
        retValue.append(includesStack2String(inclStackIndex, inclStack));
        return retValue.toString();
    }

    private static String includesStack2String(int inclStackIndex, Collection<IncludeInfo> inclStack) {
        StringBuilder retValue = new StringBuilder();
        if (inclStackIndex == 0) {
            retValue.append("<not from #include>"); // NOI18N
        } else {
            retValue.append("from ").append(inclStackIndex).append("th #include "); // NOI18N
            for (Iterator<IncludeInfo>  it = inclStack.iterator(); it.hasNext();) {
                IncludeInfo info = it.next();
                retValue.append(info);
                if (it.hasNext()) {
                    retValue.append("->\n"); // NOI18N
                }
            }            
        }
        return retValue.toString();
    }

    private static class APTTokenStreamCacheImpl implements ClankDriver.APTTokenStreamCache {
      private final int inclStackIndex;
      public APTTokenStreamCacheImpl(int inclStackIndex) {
        this.inclStackIndex = inclStackIndex;
      }

      @Override
      public int getFileIndex() {
        return inclStackIndex;
      }

      @Override
      public int[] getSkippedRanges() {
        return null;
      }

      @Override
      public TokenStream getTokenStream() {
        return null;
      }

      @Override
      public boolean hasTokenStream() {
        return false;
      }
    }
}
