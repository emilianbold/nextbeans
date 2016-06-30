/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.editor.document.LineDocumentUtils;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.EditList;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.modules.php.api.PhpVersion;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.actions.FixUsesAction.Options;
import org.netbeans.modules.php.editor.actions.ImportData.ItemVariant;
import org.netbeans.modules.php.editor.api.AliasedName;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.indent.CodeStyle;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.model.GroupUseScope;
import org.netbeans.modules.php.editor.model.ModelElement;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.NamespaceScope;
import org.netbeans.modules.php.editor.model.Scope;
import org.netbeans.modules.php.editor.model.UseScope;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.UnusedUsesCollector;
import org.netbeans.modules.php.editor.parser.UnusedUsesCollector.UnusedOffsetRanges;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.netbeans.modules.php.editor.parser.astnodes.UseStatement;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.util.Exceptions;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class FixUsesPerformer {

    private static final String NEW_LINE = "\n"; //NOI18N
    private static final char SEMICOLON = ';'; //NOI18N
    private static final char SPACE = ' '; //NOI18N
    private static final String USE_KEYWORD = "use"; //NOI18N
    private static final String USE_PREFIX = NEW_LINE + USE_KEYWORD + SPACE; //NOI18N
    private static final String USE_CONST_PREFIX = NEW_LINE + USE_KEYWORD + SPACE + "const" + SPACE; //NOI18N
    private static final String USE_FUNCTION_PREFIX = NEW_LINE + USE_KEYWORD + SPACE + "function" + SPACE; //NOI18N
    private static final String AS_KEYWORD = "as"; //NOI18N
    private static final String AS_CONCAT = SPACE + AS_KEYWORD + SPACE;
    private static final String EMPTY_STRING = ""; //NOI18N
    private static final char COMMA = ','; //NOI18N
    private static final char CURLY_OPEN = '{'; //NOI18N
    private static final char CURLY_CLOSE = '}'; //NOI18N
    private final PHPParseResult parserResult;
    private final ImportData importData;
    private final List<ItemVariant> selections;
    private final boolean removeUnusedUses;
    private final Options options;
    private EditList editList;
    private BaseDocument baseDocument;

    public FixUsesPerformer(
            final PHPParseResult parserResult,
            final ImportData importData,
            final List<ItemVariant> selections,
            final boolean removeUnusedUses,
            final Options options) {
        this.parserResult = parserResult;
        this.importData = importData;
        this.selections = selections;
        this.removeUnusedUses = removeUnusedUses;
        this.options = options;
    }

    public void perform() {
        final Document document = parserResult.getSnapshot().getSource().getDocument(false);
        if (document instanceof BaseDocument) {
            baseDocument = (BaseDocument) document;
            editList = new EditList(baseDocument);
            processExistingUses();
            processSelections();
            editList.apply();
        }
    }

    private void processSelections() {
        final List<ImportData.DataItem> dataItems = resolveDuplicateSelections();
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(parserResult.getModel().getFileScope(), importData.caretPosition);
        assert namespaceScope != null;
        int startOffset = getOffset(baseDocument, namespaceScope);
        List<UsePart> useParts = new ArrayList<>();
        Collection<? extends GroupUseScope> declaredGroupUses = namespaceScope.getDeclaredGroupUses();
        for (GroupUseScope groupUseElement : declaredGroupUses) {
            for (UseScope useElement : groupUseElement.getUseScopes()) {
                processUseElement(useElement, useParts);
            }
        }
        Collection<? extends UseScope> declaredUses = namespaceScope.getDeclaredSingleUses();
        for (UseScope useElement : declaredUses) {
            assert !useElement.isPartOfGroupUse() : useElement;
            processUseElement(useElement, useParts);
        }
        for (int i = 0; i < selections.size(); i++) {
            ItemVariant itemVariant = selections.get(i);
            if (itemVariant.canBeUsed()) {
                SanitizedUse sanitizedUse = new SanitizedUse(
                        new UsePart(modifyUseName(itemVariant.getName()), UsePart.Type.create(itemVariant.getType()), itemVariant.isFromAliasedElement()),
                        useParts,
                        createAliasStrategy(i, useParts, selections));
                if (sanitizedUse.shouldBeUsed()) {
                    useParts.add(sanitizedUse.getSanitizedUsePart());
                }
                for (UsedNamespaceName usedNamespaceName : dataItems.get(i).getUsedNamespaceNames()) {
                    editList.replace(usedNamespaceName.getOffset(), usedNamespaceName.getReplaceLength(), sanitizedUse.getReplaceName(usedNamespaceName), false, 0);
                }
            }
        }
        replaceUnimportedItems();
        editList.replace(startOffset, 0, createInsertString(useParts), false, 0);
    }

    private void replaceUnimportedItems() {
        for (ImportData.DataItem dataItem : importData.getItemsToReplace()) {
            ItemVariant defaultVariant = dataItem.getDefaultVariant();
            for (UsedNamespaceName usedNamespaceName : dataItem.getUsedNamespaceNames()) {
                editList.replace(usedNamespaceName.getOffset(), usedNamespaceName.getReplaceLength(), defaultVariant.getName(), false, 0);
            }
        }
    }

    private List<ImportData.DataItem> resolveDuplicateSelections() {
        final List<ImportData.DataItem> dataItems = new ArrayList<>(importData.getItems());
        List<ItemVariant> selectionsCopy = new ArrayList<>(selections);
        List<Integer> itemIndexesToRemove = new ArrayList<>();
        for (int i = 0; i < selections.size(); i++) {
            List<UsedNamespaceName> usedNamespaceNames = new ArrayList<>();
            ItemVariant baseVariant = selections.get(i);
            if (!baseVariant.canBeUsed()) {
                continue;
            }
            for (int j = i + 1; j < selectionsCopy.size(); j++) {
                ItemVariant testedVariant = selectionsCopy.get(j);
                if (baseVariant.equals(testedVariant)
                        && itemIndexesToRemove.add(j)) {
                    ImportData.DataItem duplicateItem = dataItems.get(j);
                    usedNamespaceNames.addAll(duplicateItem.getUsedNamespaceNames());
                }
            }
            if (!usedNamespaceNames.isEmpty()) {
                dataItems.get(i).addUsedNamespaceNames(usedNamespaceNames);
            }
        }
        Collections.sort(itemIndexesToRemove);
        Collections.reverse(itemIndexesToRemove);
        for (int itemIndexToRemove : itemIndexesToRemove) {
            // we need to remove selection as well as its dataitem (they are paired by their indexes)
            selections.remove(itemIndexToRemove);
            dataItems.remove(itemIndexToRemove);
        }
        return dataItems;
    }

    private AliasStrategy createAliasStrategy(final int selectionIndex, final List<UsePart> existingUseParts, final List<ItemVariant> selections) {
        AliasStrategy createAliasStrategy;
        if (options.aliasesCapitalsOfNamespaces()) {
            createAliasStrategy = new CapitalsStrategy(selectionIndex, existingUseParts, selections);
        } else {
            createAliasStrategy = new UnqualifiedNameStrategy(selectionIndex, existingUseParts, selections);
        }
        return createAliasStrategy;
    }

    private void processUseElement(final UseScope useElement, final List<UsePart> useParts) {
        if (isUsed(useElement) || !removeUnusedUses) {
            AliasedName aliasedName = useElement.getAliasedName();
            if (aliasedName != null) {
                useParts.add(new UsePart(
                        modifyUseName(aliasedName.getRealName().toString()) + AS_CONCAT + aliasedName.getAliasName(),
                        UsePart.Type.create(useElement.getType())));
            } else {
                useParts.add(new UsePart(
                        modifyUseName(useElement.getName()),
                        UsePart.Type.create(useElement.getType())));
            }
        }
    }

    private String modifyUseName(final String useName) {
        String result = useName;
        if (options.startUseWithNamespaceSeparator()) {
            result = result.startsWith(ImportDataCreator.NS_SEPARATOR) ? result : ImportDataCreator.NS_SEPARATOR + result;
        } else {
            result = result.startsWith(ImportDataCreator.NS_SEPARATOR) ? result.substring(ImportDataCreator.NS_SEPARATOR.length()) : result;
        }
        return result;
    }

    private boolean isUsed(final UseScope useElement) {
        boolean result = true;
        for (UnusedOffsetRanges unusedRange : new UnusedUsesCollector(parserResult).collect()) {
            if (unusedRange.getRangeToVisualise().containsInclusive(useElement.getOffset())) {
                result = false;
                break;
            }
        }
        return result;
    }

    private String createInsertString(final List<UsePart> useParts) {
        StringBuilder insertString = new StringBuilder();
        Collections.sort(useParts);
        if (useParts.size() > 0) {
            insertString.append(NEW_LINE);
        }
        String indentString = null;
        if (options.preferGroupUses()
                || options.preferMultipleUseStatementsCombined()) {
            CodeStyle codeStyle = CodeStyle.get(baseDocument);
            indentString = IndentUtils.createIndentString(codeStyle.getIndentSize(), codeStyle.expandTabToSpaces(), codeStyle.getTabSize());
        }

        if (options.preferGroupUses()
                && options.getPhpVersion().compareTo(PhpVersion.PHP_70) >= 0) {
            insertString.append(createStringForGroupUse(useParts, indentString));
        } else if (options.preferMultipleUseStatementsCombined()) {
            insertString.append(createStringForMultipleUse(useParts, indentString));
        } else {
            insertString.append(createStringForCommonUse(useParts));
        }
        return insertString.toString();
    }

    private String createStringForGroupUse(List<UsePart> useParts, String indentString) {
        List<UsePart> typeUseParts = new ArrayList<>(useParts.size());
        List<UsePart> constUseParts = new ArrayList<>(useParts.size());
        List<UsePart> functionUseParts = new ArrayList<>(useParts.size());
        for (UsePart usePart : useParts) {
            switch (usePart.getType()) {
                case TYPE:
                    typeUseParts.add(usePart);
                    break;
                case CONST:
                    constUseParts.add(usePart);
                    break;
                case FUNCTION:
                    functionUseParts.add(usePart);
                    break;
                default:
                    assert false : "Unknown type: " + usePart.getType();
            }
        }
        StringBuilder insertString = new StringBuilder();
        // types
        createStringForGroupUse(insertString, indentString, USE_PREFIX, typeUseParts);
        // constants
        createStringForGroupUse(insertString, indentString, USE_CONST_PREFIX, constUseParts);
        // functions
        createStringForGroupUse(insertString, indentString, USE_FUNCTION_PREFIX, functionUseParts);
        return insertString.toString();
    }

    private List<String> usePartsToNamespaces(List<UsePart> useParts) {
        return useParts.stream()
                .map(part -> part.getTextPart())
                .collect(Collectors.toList());
    }

    private void createStringForGroupUse(StringBuilder insertString, String indentString, String usePrefix, List<UsePart> useParts) {
        List<UsePart> groupedUseParts = new ArrayList<>(useParts.size());
        List<UsePart> nonGroupedUseParts = new ArrayList<>(useParts.size());
        List<String> prefixes = CodeUtils.getCommonNamespacePrefixes(usePartsToNamespaces(useParts));
        String lastGroupUsePrefix = null;
        for (UsePart usePart : useParts) {
            String fqNamespace = CodeUtils.fullyQualifyNamespace(usePart.getTextPart());
            String groupUsePrefix = null;
            for (String prefix : prefixes) {
                if (fqNamespace.startsWith(prefix)) {
                    groupUsePrefix = prefix;
                    break;
                }
            }
            if (groupUsePrefix != null) {
                if (lastGroupUsePrefix != null
                        && !lastGroupUsePrefix.equals(groupUsePrefix)) {
                    processGroupedUseParts(insertString, indentString, usePrefix, lastGroupUsePrefix, groupedUseParts);
                }
                lastGroupUsePrefix = groupUsePrefix;
                processNonGroupedUseParts(insertString, indentString, nonGroupedUseParts);
                groupedUseParts.add(usePart);
            } else {
                processGroupedUseParts(insertString, indentString, usePrefix, lastGroupUsePrefix, groupedUseParts);
                nonGroupedUseParts.add(usePart);
            }
        }
        processNonGroupedUseParts(insertString, indentString, nonGroupedUseParts);
        processGroupedUseParts(insertString, indentString, usePrefix, lastGroupUsePrefix, groupedUseParts);
    }

    private void processNonGroupedUseParts(StringBuilder insertString, String indentString, List<UsePart> nonGroupedUseParts) {
        if (nonGroupedUseParts.isEmpty()) {
            return;
        }
        if (options.preferMultipleUseStatementsCombined()) {
            insertString.append(createStringForMultipleUse(nonGroupedUseParts, indentString));
        } else {
            insertString.append(createStringForCommonUse(nonGroupedUseParts));
        }
        nonGroupedUseParts.clear();
    }

    private void processGroupedUseParts(StringBuilder insertString, String indentString, String usePrefix, String groupUsePrefix, List<UsePart> groupedUseParts) {
        if (groupedUseParts.isEmpty()) {
            return;
        }
        assert groupUsePrefix != null : groupedUseParts;
        String properGroupUsePrefix = modifyUseName(groupUsePrefix);
        insertString.append(usePrefix).append(properGroupUsePrefix).append(SPACE).append(CURLY_OPEN).append(NEW_LINE);
        boolean first = true;
        int prefixLength = properGroupUsePrefix.length();
        for (UsePart groupUsePart : groupedUseParts) {
            if (first) {
                first = false;
            } else {
                insertString.append(COMMA).append(NEW_LINE);
            }
            insertString.append(indentString).append(groupUsePart.getTextPart().substring(prefixLength));
        }
        insertString.append(NEW_LINE).append(CURLY_CLOSE).append(SEMICOLON);
        groupedUseParts.clear();
    }

    private String createStringForMultipleUse(List<UsePart> useParts, String indentString) {
        StringBuilder insertString = new StringBuilder();
        UsePart.Type lastUsePartType = null;
        for (Iterator<UsePart> it = useParts.iterator(); it.hasNext(); ) {
            UsePart usePart = it.next();
            if (lastUsePartType != null) {
                if (lastUsePartType == usePart.getType()) {
                    insertString.append(COMMA).append(NEW_LINE).append(indentString);
                } else {
                    insertString.append(SEMICOLON);
                }
            }
            if (lastUsePartType != usePart.getType()) {
                lastUsePartType = usePart.getType();
                switch (usePart.getType()) {
                    case TYPE:
                        insertString.append(USE_PREFIX);
                        break;
                    case CONST:
                        insertString.append(USE_CONST_PREFIX);
                        break;
                    case FUNCTION:
                        insertString.append(USE_FUNCTION_PREFIX);
                        break;
                    default:
                        insertString.append(USE_PREFIX);
                }
            }
            insertString.append(usePart.getTextPart());
        }
        if (!useParts.isEmpty()) {
            insertString.append(SEMICOLON);
        }
        return insertString.toString();
    }

    private String createStringForCommonUse(List<UsePart> useParts) {
        StringBuilder result = new StringBuilder();
        for (UsePart usePart : useParts) {
            result.append(usePart.getUsePrefix()).append(usePart.getTextPart()).append(SEMICOLON);
        }
        return result.toString();
    }

    private void processExistingUses() {
        ExistingUseStatementVisitor visitor = new ExistingUseStatementVisitor();
        Program program = parserResult.getProgram();
        if (program != null) {
            program.accept(visitor);
        }
        for (OffsetRange offsetRange : visitor.getUsedRanges()) {
            int startOffset = getOffsetWithoutLeadingWhitespaces(offsetRange.getStart());
            editList.replace(startOffset, offsetRange.getEnd() - startOffset, EMPTY_STRING, false, 0);
        }
    }

    private int getOffsetWithoutLeadingWhitespaces(final int startOffset) {
        int result = startOffset;
        baseDocument.readLock();
        try {
            TokenSequence<PHPTokenId> ts = LexUtilities.getPHPTokenSequence(baseDocument, startOffset);
            if (ts != null) {
                ts.move(startOffset);
                while (ts.movePrevious() && ts.token().id().equals(PHPTokenId.WHITESPACE)) {
                    result = ts.offset();
                }
            }
        } finally {
            baseDocument.readUnlock();
        }
        return result;
    }

    private static int getOffset(BaseDocument baseDocument, NamespaceScope namespaceScope) {
        try {
            ModelElement lastSingleUse = getLastUse(namespaceScope, false);
            ModelElement lastGroupUse = getLastUse(namespaceScope, true);
            if (lastSingleUse != null
                    && lastGroupUse != null) {
                if (lastSingleUse.getOffset() > lastGroupUse.getOffset()) {
                    return LineDocumentUtils.getLineEnd(baseDocument, lastSingleUse.getOffset());
                }
                // XXX is this correct?
                return LineDocumentUtils.getLineEnd(baseDocument, lastGroupUse.getNameRange().getEnd());
            }
            if (lastSingleUse != null) {
                return LineDocumentUtils.getLineEnd(baseDocument, lastSingleUse.getOffset());
            }
            if (lastGroupUse != null) {
                // XXX is this correct?
                return LineDocumentUtils.getLineEnd(baseDocument, lastGroupUse.getNameRange().getEnd());
            }
            return LineDocumentUtils.getLineEnd(baseDocument, namespaceScope.getOffset());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return 0;
    }

    @CheckForNull
    private static ModelElement getLastUse(NamespaceScope namespaceScope, boolean group) {
        ModelElement offsetElement = null;
        Collection<? extends Scope> declaredUses = group ? namespaceScope.getDeclaredGroupUses() : namespaceScope.getDeclaredSingleUses();
        for (Scope useElement : declaredUses) {
            if (offsetElement == null
                    || offsetElement.getOffset() < useElement.getOffset()) {
                offsetElement = useElement;
            }
        }
        return offsetElement;
    }

    private interface AliasStrategy {
        String createAlias(final QualifiedName qualifiedName);
    }

    private abstract static class AliasStrategyImpl implements AliasStrategy {

        private final int selectionIndex;
        private final List<UsePart> existingUseParts;
        private final List<ItemVariant> selections;

        public AliasStrategyImpl(final int selectionIndex, final List<UsePart> existingUseParts, final List<ItemVariant> selections) {
            this.selectionIndex = selectionIndex;
            this.existingUseParts = existingUseParts;
            this.selections = selections;
        }

        @Override
        public String createAlias(final QualifiedName qualifiedName) {
            String result = "";
            final String possibleAliasedName = getPossibleAliasName(qualifiedName);
            String newAliasedName = possibleAliasedName;
            int i = 1;
            while (existSelectionWith(newAliasedName, selectionIndex) || existUseWith(newAliasedName)) {
                i++;
                newAliasedName = possibleAliasedName + i;
                result = newAliasedName;
            }
            return result.isEmpty() && mustHaveAlias(qualifiedName) ? possibleAliasedName : result;
        }

        private boolean mustHaveAlias(final QualifiedName qualifiedName) {
            final String unqualifiedName = qualifiedName.getName();
            return existSelectionWith(unqualifiedName, selectionIndex) || existUseWith(unqualifiedName);
        }

        private boolean existSelectionWith(final String name, final int selectionIndex) {
            for (int i = selectionIndex + 1; i < selections.size(); i++) {
                if (endsWithName(selections.get(i).getName(), name)) {
                    return true;
                }
            }
            return false;
        }

        private boolean existUseWith(final String name) {
            for (UsePart existingUsePart : existingUseParts) {
                if (endsWithName(existingUsePart.getTextPart(), name) || existingUsePart.getTextPart().endsWith(SPACE + name)) {
                    return true;
                }
            }
            return false;
        }

        private boolean endsWithName(final String usePart, final String name) {
            return usePart.endsWith(ImportDataCreator.NS_SEPARATOR + name);
        }

        protected abstract String getPossibleAliasName(final QualifiedName qualifiedName);

    }

    private static class CapitalsStrategy extends AliasStrategyImpl {

        public CapitalsStrategy(int selectionIndex, List<UsePart> existingUseParts, List<ItemVariant> selections) {
            super(selectionIndex, existingUseParts, selections);
        }

        @Override
        protected String getPossibleAliasName(final QualifiedName qualifiedName) {
            final StringBuilder sb = new StringBuilder();
            for (String segment : qualifiedName.getSegments()) {
                sb.append(Character.toUpperCase(segment.charAt(0)));
            }
            return sb.toString();
        }

    }

    private static class UnqualifiedNameStrategy extends AliasStrategyImpl {

        public UnqualifiedNameStrategy(int selectionIndex, List<UsePart> existingUseParts, List<ItemVariant> selections) {
            super(selectionIndex, existingUseParts, selections);
        }

        @Override
        protected String getPossibleAliasName(QualifiedName qualifiedName) {
            return qualifiedName.getName();
        }

    }

    private static class SanitizedUse {

        private final UsePart usePartToSanitization;
        private String alias;
        private final boolean shouldBeUsed;

        public SanitizedUse(final UsePart usePartToSanitization, final List<UsePart> existingUseParts, final AliasStrategy createAliasStrategy) {
            this.usePartToSanitization = usePartToSanitization;
            QualifiedName qualifiedName = QualifiedName.create(usePartToSanitization.getTextPart());
            if (!existingUseParts.contains(usePartToSanitization) && !usePartToSanitization.isFromAliasedElement()) {
                alias = createAliasStrategy.createAlias(qualifiedName);
                shouldBeUsed = true;
            } else {
                shouldBeUsed = false;
            }
        }

        public UsePart getSanitizedUsePart() {
            return new UsePart(hasAlias() ? usePartToSanitization.getTextPart() + AS_CONCAT + alias : usePartToSanitization.getTextPart(), usePartToSanitization.getType(), usePartToSanitization.isFromAliasedElement());
        }

        private boolean hasAlias() {
            return alias != null && !alias.isEmpty();
        }

        public String getReplaceName(final UsedNamespaceName usedNamespaceName) {
            String result;
            if (hasAlias()) {
                result = alias;
            } else {
                if (usePartToSanitization.isFromAliasedElement()) {
                    result = usePartToSanitization.getTextPart();
                } else {
                    result = usedNamespaceName.getReplaceName();
                }
            }
            return result;
        }

        public boolean shouldBeUsed() {
            return shouldBeUsed;
        }
    }

    private static class ExistingUseStatementVisitor extends DefaultVisitor {

        private final List<OffsetRange> usedRanges = new LinkedList<>();

        public List<OffsetRange> getUsedRanges() {
            return Collections.unmodifiableList(usedRanges);
        }

        @Override
        public void visit(UseStatement node) {
            usedRanges.add(new OffsetRange(node.getStartOffset(), node.getEndOffset()));
        }
    }

    private static class UsePart implements Comparable<UsePart> {

        enum Type {
            TYPE {
                @Override
                String getUsePrefix() {
                    return USE_PREFIX;
                }
            },
            CONST {
                @Override
                String getUsePrefix() {
                    return USE_CONST_PREFIX;
                }
            },
            FUNCTION {
                @Override
                String getUsePrefix() {
                    return USE_FUNCTION_PREFIX;
                }
            };

            abstract String getUsePrefix();

            static Type create(ItemVariant.Type type) {
                Type result;
                switch (type) {
                    case CLASS:
                    case INTERFACE:
                    case TRAIT:
                        result = TYPE;
                        break;
                    case CONST:
                        result = CONST;
                        break;
                    case FUNCTION:
                        result = FUNCTION;
                        break;
                    default:
                        result = TYPE;
                }
                return result;
            }

            static Type create(UseScope.Type type) {
                Type result;
                switch (type) {
                    case TYPE:
                        result = TYPE;
                        break;
                    case CONST:
                        result = CONST;
                        break;
                    case FUNCTION:
                        result = FUNCTION;
                        break;
                    default:
                        result = TYPE;
                }
                return result;
            }
        }

        private final String textPart;
        private final Type type;
        private final boolean isFromAliasedElement;

        private UsePart(String textPart, Type type, boolean isFromAliasedElement) {
            this.textPart = textPart;
            this.type = type;
            this.isFromAliasedElement = isFromAliasedElement;
        }

        private UsePart(String textPart, Type type) {
            this(textPart, type, false);
        }

        public String getTextPart() {
            return textPart;
        }

        public Type getType() {
            return type;
        }

        public String getUsePrefix() {
            return type.getUsePrefix();
        }

        public boolean isFromAliasedElement() {
            return isFromAliasedElement;
        }

        @Override
        public int compareTo(UsePart other) {
            int result = 0;
            if (UsePart.Type.TYPE.equals(getType()) && UsePart.Type.TYPE.equals(other.getType())) {
                result = 0;
            } else if (UsePart.Type.TYPE.equals(getType()) && UsePart.Type.CONST.equals(other.getType())) {
                result = -1;
            } else if (UsePart.Type.TYPE.equals(getType()) && UsePart.Type.FUNCTION.equals(other.getType())) {
                result = -1;
            } else if (UsePart.Type.CONST.equals(getType()) && UsePart.Type.TYPE.equals(other.getType())) {
                result = 1;
            } else if (UsePart.Type.CONST.equals(getType()) && UsePart.Type.CONST.equals(other.getType())) {
                result = 0;
            } else if (UsePart.Type.CONST.equals(getType()) && UsePart.Type.FUNCTION.equals(other.getType())) {
                result = -1;
            } else if (UsePart.Type.FUNCTION.equals(getType()) && UsePart.Type.TYPE.equals(other.getType())) {
                result = 1;
            } else if (UsePart.Type.FUNCTION.equals(getType()) && UsePart.Type.CONST.equals(other.getType())) {
                result = 1;
            } else if (UsePart.Type.FUNCTION.equals(getType()) && UsePart.Type.FUNCTION.equals(other.getType())) {
                result = 0;
            }
            return result == 0 ? getTextPart().compareToIgnoreCase(other.getTextPart()) : result;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 71 * hash + Objects.hashCode(this.textPart);
            hash = 71 * hash + Objects.hashCode(this.type);
            hash = 71 * hash + (this.isFromAliasedElement ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final UsePart other = (UsePart) obj;
            if (!Objects.equals(this.textPart, other.textPart)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            return this.isFromAliasedElement == other.isFromAliasedElement;
        }

        @Override
        public String toString() {
            return "UsePart{" + type + " " + textPart + '}'; // NOI18N
        }

    }

}
