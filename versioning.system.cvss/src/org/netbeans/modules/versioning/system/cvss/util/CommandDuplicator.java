/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.versioning.system.cvss.util;

import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.log.RlogCommand;
import org.netbeans.lib.cvsclient.command.tag.RtagCommand;
import org.netbeans.lib.cvsclient.command.remove.RemoveCommand;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;

/**
 * Utility class that can create copies of commands. 
 * 
 * @author Maros Sandor
 */
public abstract class CommandDuplicator {
    
    public static CommandDuplicator getDuplicator(Command src) {
        if (src instanceof CommitCommand) return new CommitCloner((CommitCommand) src);
        if (src instanceof UpdateCommand) return new UpdateCloner((UpdateCommand) src);
        if (src instanceof RemoveCommand) return new RemoveCloner((RemoveCommand) src);
        if (src instanceof RtagCommand) return new RtagCloner((RtagCommand) src);
        if (src instanceof RlogCommand) return new RlogCloner((RlogCommand) src);
        throw new IllegalArgumentException("Clone not supported for command type: " + src.getClass().getName());
    }

    public abstract Command duplicate();
    
    private static class CommitCloner extends CommandDuplicator {
        private final CommitCommand sample;

        public CommitCloner(CommitCommand sample) {
            this.sample = sample;
        }

        public Command duplicate() {
            CommitCommand c = new CommitCommand();
            c.setForceCommit(sample.isForceCommit());
            c.setLogMessageFromFile(sample.getLogMessageFromFile());
            c.setMessage(sample.getMessage());
            c.setNoModuleProgram(sample.isNoModuleProgram());
            c.setRecursive(sample.isRecursive());
            c.setToRevisionOrBranch(sample.getToRevisionOrBranch());
            return c;
        }
    }

    private static class UpdateCloner extends CommandDuplicator {
        private final UpdateCommand sample;

        public UpdateCloner(UpdateCommand sample) {
            this.sample = sample;
        }

        public Command duplicate() {
            UpdateCommand c = new UpdateCommand();
            c.setBuildDirectories(sample.isBuildDirectories());
            c.setCleanCopy(sample.isCleanCopy());
            c.setKeywordSubst(sample.getKeywordSubst());
            c.setMergeRevision1(sample.getMergeRevision1());
            c.setMergeRevision2(sample.getMergeRevision2());
            c.setPipeToOutput(sample.isPipeToOutput());
            c.setPruneDirectories(sample.isPruneDirectories());
            c.setResetStickyOnes(sample.isResetStickyOnes());
            c.setUpdateByDate(sample.getUpdateByDate());
            c.setUpdateByRevision(sample.getUpdateByRevision());
            c.setUseHeadIfNotFound(sample.isUseHeadIfNotFound());
            c.setRecursive(sample.isRecursive());
            return c;
        }
    }


    private static class RemoveCloner extends CommandDuplicator {
        private final RemoveCommand sample;

        public RemoveCloner(RemoveCommand sample) {
            this.sample = sample;
        }

        public Command duplicate() {
            RemoveCommand c = new RemoveCommand();
            c.setDeleteBeforeRemove(sample.isDeleteBeforeRemove());
            c.setIgnoreLocallyExistingFiles(sample.isIgnoreLocallyExistingFiles());
            c.setRecursive(sample.isRecursive());
            return c;
        }
    }

    private static class RtagCloner extends CommandDuplicator {
        private final RtagCommand sample;

        public RtagCloner(RtagCommand sample) {
            this.sample = sample;
        }

        public Command duplicate() {
            RtagCommand c = new RtagCommand();
            c.setClearFromRemoved(sample.isClearFromRemoved());
            c.setDeleteTag(sample.isDeleteTag());
            c.setMakeBranchTag(sample.isMakeBranchTag());
            c.setMatchHeadIfRevisionNotFound(sample.isMatchHeadIfRevisionNotFound());
            c.setModules(sample.getModules());
            c.setNoExecTagProgram(sample.isNoExecTagProgram());
            c.setOverrideExistingTag(sample.isOverrideExistingTag());
            c.setTag(sample.getTag());
            c.setTagByDate(sample.getTagByDate());
            c.setTagByRevision(sample.getTagByRevision());
            c.setRecursive(sample.isRecursive());
            return c;
        }
    }

    private static class RlogCloner extends CommandDuplicator {
        private final RlogCommand sample;

        public RlogCloner(RlogCommand sample) {
            this.sample = sample;
        }

        public Command duplicate() {
            RlogCommand c = new RlogCommand();
            c.setDateFilter(c.getDateFilter());
            c.setDefaultBranch(c.isDefaultBranch());
            c.setHeaderAndDescOnly(c.isHeaderAndDescOnly());
            c.setHeaderOnly(c.isHeaderOnly());
            c.setNoTags(c.isNoTags());
            c.setRevisionFilter(c.getRevisionFilter());
            c.setStateFilter(c.getStateFilter());
            c.setSuppressHeader(c.isSuppressHeader());
            c.setUserFilter(c.getUserFilter());
            c.setModules(sample.getModules());
            c.setRecursive(sample.isRecursive());
            return c;
        }
    }
}
