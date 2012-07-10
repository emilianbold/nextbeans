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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.*;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.php.editor.CompletionContextFinder.CompletionContext;
import org.netbeans.modules.php.editor.CompletionContextFinder.KeywordCompletionType;
import org.netbeans.modules.php.editor.actions.IconsUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.PhpElementKind;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.api.QualifiedNameKind;
import org.netbeans.modules.php.editor.api.elements.BaseFunctionElement.PrintAs;
import org.netbeans.modules.php.editor.api.elements.ConstantElement;
import org.netbeans.modules.php.editor.api.elements.FieldElement;
import org.netbeans.modules.php.editor.api.elements.ParameterElement.OutputType;
import org.netbeans.modules.php.editor.api.elements.*;
import org.netbeans.modules.php.editor.codegen.CodegenUtils;
import org.netbeans.modules.php.editor.elements.ParameterElementImpl;
import org.netbeans.modules.php.editor.elements.TypeNameResolverImpl;
import org.netbeans.modules.php.editor.indent.CodeStyle;
import org.netbeans.modules.php.editor.index.PredefinedSymbolElement;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.model.*;
import org.netbeans.modules.php.editor.model.impl.VariousUtils;
import org.netbeans.modules.php.editor.model.nodes.NamespaceDeclarationInfo;
import org.netbeans.modules.php.editor.nav.NavUtils;
import org.netbeans.modules.php.editor.options.CodeCompletionPanel.CodeCompletionType;
import org.netbeans.modules.php.editor.options.OptionsUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.BodyDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.netbeans.modules.php.project.api.PhpLanguageProperties;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 */
public abstract class PHPCompletionItem implements CompletionProposal {

    private static final String PHP_KEYWORD_ICON = "org/netbeans/modules/php/editor/resources/php16Key.png"; //NOI18N
    protected static ImageIcon keywordIcon = null;
    final CompletionRequest request;
    private final ElementHandle element;
    protected QualifiedNameKind generateAs;
    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static final Cache<FileObject, PhpLanguageProperties> PROPERTIES_CACHE = new Cache<FileObject, PhpLanguageProperties>(new WeakHashMap<FileObject, PhpLanguageProperties>());

    PHPCompletionItem(ElementHandle element, CompletionRequest request, QualifiedNameKind generateAs) {
        this.request = request;
        this.element = element;
        keywordIcon = new ImageIcon(ImageUtilities.loadImage(PHP_KEYWORD_ICON));
        this.generateAs = generateAs;
    }

    PHPCompletionItem(ElementHandle element, CompletionRequest request) {
        this(element, request, null);
    }

    @Override
    public int getAnchorOffset() {
        return request.anchor;
    }

    @Override
    public ElementHandle getElement() {
        return element;
    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public String getSortText() {
        return getName();
    }

    @Override
    public int getSortPrioOverride() {
        return 0;
    }

    @Override
    public String getLhsHtml(HtmlFormatter formatter) {
        formatter.appendText(getName());
        return formatter.getText();
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public Set<Modifier> getModifiers() {
        Set<Modifier> emptyModifiers = Collections.emptySet();
        ElementHandle handle = getElement();
        return (handle != null) ? handle.getModifiers() : emptyModifiers;
    }

    public String getFileNameURL() {
        ElementHandle elem = getElement();
        return (elem instanceof PhpElement) ? ((PhpElement) elem).getFilenameUrl() : "";//NOI18N
    }

    @Override
    public boolean isSmart() {
        String url = getFileNameURL();
        return (url != null && url.equals(request.currentlyEditedFileURL)) || (element instanceof AliasedElement);
    }

    private static NamespaceDeclaration findEnclosingNamespace(PHPParseResult info, int offset) {
        final Program program = info.getProgram();
        List<ASTNode> nodes = NavUtils.underCaret(info, Math.min((program != null) ? program.getEndOffset() : offset, offset));
        for (ASTNode node : nodes) {
            if (node instanceof NamespaceDeclaration) {
                return (NamespaceDeclaration) node;
            }
        }
        return null;
    }

    @Override
    public String getCustomInsertTemplate() {
        return null;
    }

    @Override
    public String getInsertPrefix() {
        StringBuilder template = new StringBuilder();
        ElementHandle elem = getElement();
        if (elem instanceof MethodElement) {
            final MethodElement method = (MethodElement) elem;
            if (method.isConstructor() && request.context.equals(CompletionContext.NEW_CLASS)) {
                elem = method.getType();
            }
        }
        if (elem instanceof FullyQualifiedElement) {
            FullyQualifiedElement ifq = (FullyQualifiedElement) elem;
            final QualifiedName qn = QualifiedName.create(request.prefix);
            final FileObject fileObject = request.result.getSnapshot().getSource().getFileObject();
            PhpLanguageProperties props = PROPERTIES_CACHE.get(fileObject);
            if (props == null) {
                props = PhpLanguageProperties.forFileObject(fileObject);
                PropertyChangeListener propertyChangeListener = WeakListeners.propertyChange(new PhpVersionChangeListener(fileObject), props);
                props.addPropertyChangeListener(propertyChangeListener);
                PROPERTIES_CACHE.save(fileObject, props);
            }
            if (props.getPhpVersion() != PhpLanguageProperties.PhpVersion.PHP_5) {
                if (generateAs == null) {
                    CodeCompletionType codeCompletionType = OptionsUtils.codeCompletionType();
                    switch (codeCompletionType) {
                        case FULLY_QUALIFIED:
                            template.append(ifq.getFullyQualifiedName());
                            return template.toString();
                        case UNQUALIFIED:
                            template.append(getName());
                            return template.toString();
                        case SMART:
                            generateAs = qn.getKind();
                            break;
                    }

                } else if (generateAs.isQualified() && (ifq instanceof TypeElement)
                        && ifq.getNamespaceName().toString().equals(NamespaceDeclarationInfo.DEFAULT_NAMESPACE_NAME)) {
                    //TODO: this is sort of hack for CCV after use, namespace keywords - should be changed
                    generateAs = QualifiedNameKind.FULLYQUALIFIED;
                }
            } else {
                template.append(getName());
                return template.toString();
            }
            switch (generateAs) {
                case FULLYQUALIFIED:
                    template.append(ifq.getFullyQualifiedName());
                    break;
                case QUALIFIED:
                    final String fqn = ifq.getFullyQualifiedName().toString();
                    int indexOf = fqn.toLowerCase().indexOf(qn.toNamespaceName().toString().toLowerCase());
                    if (indexOf != -1) {
                        template.append(fqn.substring(indexOf == 0 ? 1 : indexOf));
                        break;
                    }
                case UNQUALIFIED:
                    boolean fncOrConstFromDefaultNamespace = (((ifq instanceof FunctionElement) || (ifq instanceof ConstantElement))
                            && (ifq.getIn() == null || ifq.getIn().isEmpty())
                            && NamespaceDeclarationInfo.DEFAULT_NAMESPACE_NAME.equals(ifq.getNamespaceName().toString()));
                    final boolean isUnqualified = ifq.isAliased()
                            && (ifq instanceof AliasedElement) && ((AliasedElement) ifq).isNameAliased();
                    if (!fncOrConstFromDefaultNamespace && !isUnqualified) {
                        Model model = request.result.getModel();
                        NamespaceDeclaration namespaceDeclaration = findEnclosingNamespace(request.result, request.anchor);
                        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(namespaceDeclaration, model.getFileScope());

                        if (namespaceScope != null) {
                            LinkedList<String> segments = ifq.getFullyQualifiedName().getSegments();
                            QualifiedName fqna = QualifiedName.create(false, segments);
                            if (!namespaceScope.isDefaultNamespace() || !fqna.getKind().isUnqualified()) {
                                QualifiedName suffix = VariousUtils.getPreferredName(fqna, namespaceScope);
                                if (suffix != null) {
                                    template.append(suffix.toString());
                                    break;
                                }
                            }
                        }
                    }
                    template.append(getName());
                    break;
            }

            return template.toString();
        }

        return getName();
    }

    @Override
    public String getRhsHtml(HtmlFormatter formatter) {
        if (element instanceof TypeMemberElement) {
            TypeMemberElement classMember = (TypeMemberElement) element;
            TypeElement type = classMember.getType();
            QualifiedName qualifiedName = type.getNamespaceName();
            if (qualifiedName.isDefaultNamespace()) {
                formatter.appendText(type.getName());
                return formatter.getText();
            } else {
                formatter.appendText(type.getFullyQualifiedName().toString());
                return formatter.getText();
            }
        }
        final String in = element.getIn();
        if (in != null && in.length() > 0) {
            formatter.appendText(element.getIn());
            return formatter.getText();
        } else if (element instanceof PhpElement) {
            PhpElement ie = (PhpElement) element;
            if (ie.isPlatform()) {
                return NbBundle.getMessage(PHPCompletionItem.class, "PHPPlatform");
            }

            String filename = ie.getFilenameUrl();
            if (filename != null) {
                int index = filename.lastIndexOf('/');
                if (index != -1) {
                    filename = filename.substring(index + 1);
                }

                formatter.appendText(filename);
                return formatter.getText();
            } else if (ie.getFileObject() != null) {
                formatter.appendText(ie.getFileObject().getNameExt());
                return formatter.getText();
            }
        }


        return null;
    }

    static class NewClassItem extends MethodElementItem {

        /**
         * @return more than one instance in case if optional parameters exists
         */
        static List<NewClassItem> getNewClassItems(final MethodElement methodElement, CompletionRequest request) {
            final List<NewClassItem> retval = new ArrayList<NewClassItem>();
            List<FunctionElementItem> items = FunctionElementItem.getItems(methodElement, request);
            for (FunctionElementItem functionElementItem : items) {
                retval.add(new NewClassItem(functionElementItem));
            }
            return retval;
        }

        private NewClassItem(FunctionElementItem function) {
            super(function);
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (getElement().getIn() != null) {
                String namespaceName = ((MethodElement) getElement()).getType().getNamespaceName().toString();
                if (namespaceName != null && !NamespaceDeclarationInfo.DEFAULT_NAMESPACE_NAME.equals(namespaceName)) {
                    formatter.appendText(namespaceName);
                    return formatter.getText();
                }
            }
            return super.getRhsHtml(formatter);
        }

        @Override
        public String getName() {
            String in = getElement().getIn();
            return (in != null) ? in : super.getName();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CONSTRUCTOR;
        }

        @Override
        public boolean isSmart() {
            return (getElement() instanceof AliasedElement) ? true : super.isSmart();
        }
    }

    public static class MethodElementItem extends FunctionElementItem {

        /**
         * @return more than one instance in case if optional parameters exists
         */
        static List<MethodElementItem> getItems(final MethodElement methodElement, CompletionRequest request) {
            final List<MethodElementItem> retval = new ArrayList<MethodElementItem>();
            List<FunctionElementItem> items = FunctionElementItem.getItems(methodElement, request);
            for (FunctionElementItem functionElementItem : items) {
                retval.add(new MethodElementItem(functionElementItem));
            }
            return retval;
        }

        MethodElementItem(FunctionElementItem function) {
            super(function.getBaseFunctionElement(), function.request, function.parameters);
        }
    }

    private static class ExistingVariableResolver {

        private final CompletionRequest request;
        private final int caretOffset;
        private final List<VariableName> usedVariables = new LinkedList<VariableName>();
        private static final RequestProcessor RP = new RequestProcessor("ExistingVariableResolver"); //NOI18N
        private static final Logger LOGGER = Logger.getLogger(ExistingVariableResolver.class.getName());
        private static final int RESOLVING_TIMEOUT = 300;

        public ExistingVariableResolver(CompletionRequest request) {
            this.request = request;
            caretOffset = request.anchor;
        }

        public ParameterElement resolveVariable(final ParameterElement param) {
            if (OptionsUtils.codeCompletionSmartParametersPreFilling()) {
                Future<VariableName> futureVariableToUse = RP.submit(new Callable<VariableName>() {

                    @Override
                    public VariableName call() throws Exception {
                        Collection<? extends VariableName> declaredVariables = getDeclaredVariables();
                        VariableName variableToUse = null;
                        if (declaredVariables != null) {
                            int oldOffset = 0;
                            for (VariableName variable : declaredVariables) {
                                if (!usedVariables.contains(variable) && !variable.representsThis()) {
                                    if (isPreviousVariable(variable)) {
                                        if (hasCorrectType(variable, param.getTypes())) {
                                            if (variable.getName().equals(param.getName())) {
                                                variableToUse = variable;
                                                break;
                                            }
                                            int newOffset = variable.getNameRange().getStart();
                                            if (newOffset > oldOffset) {
                                                oldOffset = newOffset;
                                                variableToUse = variable;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return variableToUse;
                    }
                });
                VariableName variableToUseName = null;
                try {
                    variableToUseName = futureVariableToUse.get(RESOLVING_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.FINE, "Resolving of existing variables has been interrupted.");
                } catch (ExecutionException ex) {
                    LOGGER.log(Level.SEVERE, "Exception has been thrown during resolving of existing variables.", ex);
                } catch (TimeoutException ex) {
                    LOGGER.log(Level.FINE, "Timeout for resolving existing variables has been exceed: {0}", RESOLVING_TIMEOUT);
                }
                if (variableToUseName != null) {
                    usedVariables.add(variableToUseName);
                    return new ParameterElementImpl(variableToUseName.getName(), param.getDefaultValue(), param.getOffset(), param.getTypes(), param.isMandatory(), param.hasDeclaredType(), param.isReference());
                }
            }
            return param;
        }

        private Collection<? extends VariableName> getDeclaredVariables() {
            VariableScope variableScope = request.result.getModel().getVariableScope(caretOffset);
            if (variableScope != null) {
                return variableScope.getDeclaredVariables();
            }
            return null;
        }

        private boolean isPreviousVariable(VariableName variable) {
            int offsetDiff = caretOffset - variable.getNameRange().getStart();
            if (offsetDiff > 0) {
                return true;
            }
            return false;
        }

        private boolean hasCorrectType(VariableName variable, Set<TypeResolver> possibleTypes) {
            Collection<? extends String> typeNames = variable.getTypeNames(caretOffset);
            if (!typeNames.isEmpty()) {
                for (TypeResolver type : possibleTypes) {
                    if (typeNames.contains(type.getRawTypeName()) || type.getRawTypeName().equals("mixed")
                            || (typeNames.contains("real") && type.getRawTypeName().equals("float"))
                            || (typeNames.contains("int") && type.getRawTypeName().equals("integer"))) { // NOI18N
                        return true;
                    }
                }
            }
            return false;
        }
    }

    static class FunctionElementItem extends PHPCompletionItem {

        private List<ParameterElement> parameters;

        /**
         * @return more than one instance in case if optional parameters exists
         */
        static List<FunctionElementItem> getItems(final BaseFunctionElement function, CompletionRequest request) {
            final List<FunctionElementItem> retval = new ArrayList<FunctionElementItem>();
            final List<ParameterElement> parameters = new ArrayList<ParameterElement>();
            for (ParameterElement param : function.getParameters()) {
                if (!param.isMandatory()) {
                    if (retval.isEmpty()) {
                        retval.add(new FunctionElementItem(function, request, parameters));
                    }
                    parameters.add(param);
                    retval.add(new FunctionElementItem(function, request, parameters));
                } else {
                    //assert retval.isEmpty():param.asString();
                    parameters.add(param);
                }
            }
            if (retval.isEmpty()) {
                retval.add(new FunctionElementItem(function, request, parameters));
            }

            return retval;
        }

        FunctionElementItem(BaseFunctionElement function, CompletionRequest request, List<ParameterElement> parameters) {
            super(function, request);
            this.parameters = new ArrayList<ParameterElement>(parameters);
        }

        public BaseFunctionElement getBaseFunctionElement() {
            return (BaseFunctionElement) getElement();
        }

        @Override
        public ElementKind getKind() {
            return getBaseFunctionElement().getPhpElementKind().getElementKind();
        }

        @Override
        public String getInsertPrefix() {
            final String insertPrefix = super.getInsertPrefix();
            int indexOf = (request.prefix != null && insertPrefix != null) ? insertPrefix.toLowerCase().indexOf(request.prefix.toLowerCase()) : -1;
            return indexOf > 0 ? insertPrefix.substring(indexOf) : insertPrefix;
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder template = new StringBuilder();
            String superTemplate = super.getInsertPrefix();
            if (superTemplate != null) {
                template.append(superTemplate);
            } else {
                template.append(getName());
            }

            TokenHierarchy<?> tokenHierarchy = request.result.getSnapshot().getTokenHierarchy();
            TokenSequence<PHPTokenId> tokenSequence = (TokenSequence<PHPTokenId>) tokenHierarchy.tokenSequence();
            if (tokenSequence != null) {
                VariableScope variableScope = request.result.getModel().getVariableScope(request.anchor);
                if (variableScope != null) {
                    tokenSequence = tokenSequence.subSequence(request.anchor, variableScope.getBlockRange().getEnd());
                }
            }
            boolean wasWhitespace = false;
            while (tokenSequence.moveNext()) {
                Token<PHPTokenId> token = tokenSequence.token();
                PHPTokenId id = token.id();
                if (PHPTokenId.PHP_STRING.equals(id)) {
                    if (wasWhitespace) {
                        // this needs brackets: curl_set^ curl_setopt($ch, $option, $ch);
                        break;
                    } else {
                        // this doesn't need brackets: curl_setopt^  ($ch, $option, $ch);
                        continue;
                    }
                } else if (PHPTokenId.WHITESPACE.equals(id)) {
                    wasWhitespace = true;
                    continue;
                } else if (PHPTokenId.PHP_TOKEN.equals(id) && token.toString().equals("(")) { //NOI18N
                    return template.toString();
                } else {
                    break;
                }
            }

            template.append("("); //NOI18N

            List<String> params = getInsertParams();

            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                if (param.startsWith("&")) {//NOI18N
                    param = param.substring(1);
                }
                template.append(String.format("${php-cc-%d  default=\"%s\"}", i, param));

                if (i < params.size() - 1) {
                    template.append(", "); //NOI18N
                }
            }

            template.append(')');

            return template.toString();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            ElementKind kind = getKind();

            formatter.name(kind, true);

            if (emphasisName()) {
                formatter.emphasis(true);
                formatter.appendText(getName());
                formatter.emphasis(false);
            } else {
                formatter.appendText(getName());
            }

            formatter.name(kind, false);

            formatter.appendHtml("("); // NOI18N
            formatter.parameters(true);
            appendParamsStr(formatter);
            formatter.parameters(false);
            formatter.appendHtml(")"); // NOI18N

            return formatter.getText();
        }

        protected boolean emphasisName() {
            return true;//getFunction().isResolved();
        }

        public List<String> getInsertParams() {
            List<String> insertParams = new LinkedList<String>();
            final ExistingVariableResolver existingVariableResolver = new ExistingVariableResolver(request);
            for (ParameterElement parameter : parameters) {
                insertParams.add(existingVariableResolver.resolveVariable(parameter).getName());
            }
            return insertParams;
        }

        @Override
        public String getSortText() {
            return getName() + parameters.size();
        }

        private void appendParamsStr(HtmlFormatter formatter) {
            List<ParameterElement> allParameters = parameters;
            for (int i = 0; i < allParameters.size(); i++) {
                ParameterElement parameter = allParameters.get(i);
                if (i != 0) {
                    formatter.appendText(", "); // NOI18N
                }

                final String paramTpl = parameter.asString(OutputType.SHORTEN_DECLARATION);
                if (!parameter.isMandatory()) {
                    formatter.appendText(paramTpl);
                } else {
                    formatter.emphasis(true);
                    formatter.appendText(paramTpl);
                    formatter.emphasis(false);
                }
            }
        }
    }

    static class BasicFieldItem extends PHPCompletionItem {

        private String typeName;

        public static BasicFieldItem getItem(PhpElement field, String type, CompletionRequest request) {
            return new BasicFieldItem(field, type, request);
        }

        private BasicFieldItem(PhpElement field, String typeName, CompletionRequest request) {
            super(field, request);
            this.typeName = typeName;
        }

        @Override
        public String getInsertPrefix() {
            Completion.get().showToolTip();
            return getName();
        }

        @Override
        public ElementKind getKind() {
            //TODO: variable just because originally VARIABLE was returned and thus all tests fail
            //return ElementKind.FIELD;
            return ElementKind.VARIABLE;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.type(true);
            formatter.appendText(getTypeName() == null ? "" : getTypeName()); //NOI18N
            formatter.type(false);
            formatter.appendText(" "); //NOI18N
            formatter.name(getKind(), true);
            formatter.appendText(getName());
            formatter.name(getKind(), false);
            return formatter.getText();
        }

        @Override
        public String getName() {
            final String name = getElement().getName();
            return name.startsWith("$") ? name.substring(1) : name;
        }

        /**
         * @return the typeName
         */
        protected String getTypeName() {
            return typeName;
        }
    }

    static class FieldItem extends BasicFieldItem {

        public static FieldItem getItem(FieldElement field, CompletionRequest request) {
            return new FieldItem(field, request);
        }

        private FieldItem(FieldElement field, CompletionRequest request) {
            super(field, null, request);
        }

        FieldElement getField() {
            return (FieldElement) getElement();
        }

        @Override
        public String getName() {
            final FieldElement field = getField();
            return field.getName(field.isStatic());
        }

        @Override
        protected String getTypeName() {
            Set<TypeResolver> types = getField().getInstanceTypes();
            String typeName = types.isEmpty() ? "?" : types.size() > 1 ? "mixed" : "?";//NOI18N
            if (types.size() == 1) {
                TypeResolver typeResolver = types.iterator().next();
                if (typeResolver.isResolved()) {
                    QualifiedName qualifiedName = typeResolver.getTypeName(false);
                    if (qualifiedName != null) {
                        typeName = qualifiedName.toString();
                    }
                }
            }
            return typeName;
        }
    }

    static class TypeConstantItem extends PHPCompletionItem {

        public static TypeConstantItem getItem(TypeConstantElement constant, CompletionRequest request) {
            return new TypeConstantItem(constant, request);
        }

        private TypeConstantItem(TypeConstantElement constant, CompletionRequest request) {
            super(constant, request);
        }

        TypeConstantElement getConstant() {
            return (TypeConstantElement) getElement();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CONSTANT;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.name(getKind(), true);
            formatter.appendText(getName());
            formatter.name(getKind(), false);
            formatter.appendText(" "); //NOI18N
            String value = getConstant().getValue();
            formatter.type(true);
            formatter.appendText(value != null ? value : "?");//NOI18N
            formatter.type(false);

            return formatter.getText();
        }

        @Override
        public String getName() {
            return getConstant().getName();
        }

        @Override
        public String getInsertPrefix() {
            Completion.get().showToolTip();
            return getName();
        }
    }

    public static class MethodDeclarationItem extends MethodElementItem {

        public static MethodDeclarationItem getDeclarationItem(final MethodElement methodElement, CompletionRequest request) {
            return new MethodDeclarationItem(new FunctionElementItem(methodElement, request, methodElement.getParameters()));
        }

        public static MethodDeclarationItem forIntroduceHint(final MethodElement methodElement, CompletionRequest request) {
            return new MethodDeclarationItem(new FunctionElementItem(methodElement, request, methodElement.getParameters())) {

                @Override
                protected String getFunctionBodyForTemplate() {
                    return "\n";//NOI18N
                }
            };
        }

        public static MethodDeclarationItem forMethodName(final MethodElement methodElement, CompletionRequest request) {
            return new MethodDeclarationItem(new FunctionElementItem(methodElement, request, methodElement.getParameters())) {

                @Override
                public String getCustomInsertTemplate() {
                    return super.getNameAndFunctionBodyForTemplate();
                }
            };
        }

        private MethodDeclarationItem(FunctionElementItem functionItem) {
            super(functionItem);
        }

        public MethodElement getMethod() {
            return (MethodElement) getBaseFunctionElement();
        }

        @Override
        public boolean isSmart() {
            return isMagic() ? false : true;
        }

        @Override
        protected boolean emphasisName() {
            return isMagic() ? false : super.emphasisName();
        }

        public boolean isMagic() {
            return ((MethodElement) getBaseFunctionElement()).isMagic();
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder template = new StringBuilder();
            String modifierStr = BodyDeclaration.Modifier.toString(getBaseFunctionElement().getFlags());
            if (modifierStr.length() != 0) {
                modifierStr = modifierStr.replace("abstract", "").trim();//NOI18N
                template.append(modifierStr);
            }
            template.append(" ").append("function");//NOI18N
            template.append(getNameAndFunctionBodyForTemplate());
            return template.toString();
        }

        protected String getNameAndFunctionBodyForTemplate() {
            StringBuilder template = new StringBuilder();
            TypeNameResolver typeNameResolver = getBaseFunctionElement().getParameters().isEmpty() || request == null ? TypeNameResolverImpl.forNull() : CodegenUtils.createSmarterTypeNameResolver(getBaseFunctionElement(), request.result.getModel(), request.anchor);
            template.append(getBaseFunctionElement().asString(PrintAs.NameAndParamsDeclaration, typeNameResolver));
            template.append(" ").append("{\n");//NOI18N
            template.append(getFunctionBodyForTemplate());//NOI18N
            template.append("}");//NOI18N
            return template.toString();
        }

        /**
         * @return body or null
         */
        protected String getFunctionBodyForTemplate() {
            StringBuilder template = new StringBuilder();
            MethodElement method = (MethodElement) getBaseFunctionElement();
            TypeElement type = method.getType();
            if (isMagic() || type.isInterface() || method.isAbstract()) {
                template.append("${cursor};\n");//NOI18N
            } else {
                template.append("${cursor}parent::").append(getSignature().replace("&$", "$")).append(";\n");//NOI18N
            }
            return template.toString();
        }

        private String getSignature() {
            StringBuilder retval = new StringBuilder();
            retval.append(getBaseFunctionElement().getName());
            retval.append("(");
            StringBuilder parametersInfo = new StringBuilder();
            List<ParameterElement> parameters = getBaseFunctionElement().getParameters();
            for (ParameterElement parameter : parameters) {
                if (parametersInfo.length() > 0) {
                    parametersInfo.append(", ");//NOI18N
                }
                parametersInfo.append(parameter.getName());
            }
            retval.append(parametersInfo);
            retval.append(")");//NOI18N
            return retval.toString();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            StringBuilder sb = new StringBuilder();
            sb.append(super.getLhsHtml(formatter));
            sb.append(' ').append(NbBundle.getMessage(PHPCompletionItem.class, "Generate"));//NOI18N
            return sb.toString();
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (isMagic()) {
                final String message = NbBundle.getMessage(PHPCompletionItem.class, "MagicMethod");//NOI18N
                formatter.appendText(message);
                return formatter.getText();
            }
            return super.getRhsHtml(formatter);
        }
    }

    static class ClassScopeKeywordItem extends KeywordItem {

        private final String className;

        ClassScopeKeywordItem(final String className, final String keyword, final CompletionRequest request) {
            super(keyword, request);
            this.className = className;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            if (keyword.startsWith("$")) {//NOI18N
                if (className != null) {
                    formatter.type(true);
                    formatter.appendText(className);
                    formatter.type(false);
                }
                formatter.appendText(" "); //NOI18N
            }
            return super.getLhsHtml(formatter);
        }
    }

    static class KeywordItem extends PHPCompletionItem {

        private String description = null;
        String keyword = null;
        private static final List<String> CLS_KEYWORDS =
                Arrays.asList(PHPCodeCompletion.PHP_CLASS_KEYWORDS);

        KeywordItem(String keyword, CompletionRequest request) {
            super(null, request);
            this.keyword = keyword;
        }

        @Override
        public String getName() {
            return keyword;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.name(getKind(), true);
            formatter.appendText(getName());
            formatter.name(getKind(), false);

            return formatter.getText();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.KEYWORD;
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (description != null) {
                formatter.appendHtml(description);
                return formatter.getText();

            } else {
                return null;
            }
        }

        @Override
        public ImageIcon getIcon() {
            return keywordIcon;
        }

        @Override
        public boolean isSmart() {
            return CLS_KEYWORDS.contains(getName()) ? true : super.isSmart();
        }

        @Override
        public String getInsertPrefix() {
            return getName();
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder builder = new StringBuilder();
            if (CLS_KEYWORDS.contains(getName())) {
                scheduleShowingCompletion();
            }
            KeywordCompletionType type = PHPCodeCompletion.PHP_KEYWORDS.get(getName());
            if (type == null) {
                return getName();
            }
            CodeStyle codeStyle = CodeStyle.get(EditorRegistry.lastFocusedComponent().getDocument());
            boolean appendSpace = true;
            String name = null;
            switch (type) {
                case SIMPLE:
                    return null;
                case ENDS_WITH_SPACE:
                    builder.append(getName());
                    builder.append(" ${cursor}"); //NOI18N
                    break;
                case CURSOR_INSIDE_BRACKETS:
                    name = getName();
                    builder.append(name);
                    if (name.equals("foreach") || name.equals("for")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeForParen();
                    } else if (name.equals("if")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeIfParen();
                    } else if (name.equals("switch")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeSwitchParen();
                    } else if (name.equals("array")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeArrayDeclParen();
                    } else if (name.equals("while")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeWhileParen();
                    } else if (name.equals("catch")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeCatchParen();
                    }
                    if (appendSpace) {
                        builder.append(" "); //NOI18N
                    }
                    builder.append("(${cursor})"); //NOI18N
                    break;
                case ENDS_WITH_CURLY_BRACKETS:
                    name = getName();
                    builder.append(name);
                    if (name.equals("try")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeTryLeftBrace();
                    } else if (name.equals("do")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeDoLeftBrace();
                    } else if (name.equals("else")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeElseLeftBrace();
                    }
                    if (appendSpace) {
                        builder.append(" "); //NOI18N
                    }
                    builder.append("{${cursor}}"); //NOI18N
                    break;
                case ENDS_WITH_BRACKETS_AND_CURLY_BRACKETS:
                    name = getName();
                    builder.append(name);
                    if (name.equals("elseif")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeIfParen();
                    }
                    if (appendSpace) {
                        builder.append(" "); //NOI18N
                    }
                    builder.append("(${cursor})"); //NOI18N
                    if (name.equals("elseif")) { //NOI18N
                        appendSpace = codeStyle.spaceBeforeIfLeftBrace();
                    }
                    if (appendSpace) {
                        builder.append(" "); //NOI18N
                    }
                    builder.append("{}"); //NOI18N
                    break;
                case ENDS_WITH_SEMICOLON:
                    builder.append(getName());
                    CharSequence text = request.info.getSnapshot().getText();
                    int index = request.anchor + request.prefix.length();
                    if (index == text.length() || ';' != text.charAt(index)) { //NOI18N
                        builder.append(";"); //NOI18N
                    }
                    break;
                case ENDS_WITH_COLON:
                    builder.append(getName());
                    builder.append(" ${cursor}:"); //NOI18N
                    break;
                default:
                    assert false : type.toString();
                    break;
            }
            return builder.toString();
        }
    }

    static class SuperGlobalItem extends PHPCompletionItem {

        private String name;

        public SuperGlobalItem(CompletionRequest request, String name) {
            super(new PredefinedSymbolElement(name), request);
            this.name = name;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.name(getKind(), true);
            formatter.emphasis(true);
            formatter.appendText(getName());
            formatter.emphasis(false);
            formatter.name(getKind(), false);

            return formatter.getText();
        }

        @Override
        public String getName() {
            return "$" + name; //NOI18N
        }

        @Override
        public String getInsertPrefix() {
            //todo insert array brackets for array vars
            return getName();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.VARIABLE;
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            formatter.appendText(NbBundle.getMessage(PHPCompletionItem.class, "PHPPlatform"));
            return formatter.getText();
        }

        public String getDocumentation() {
            return null;
        }

        @Override
        public ImageIcon getIcon() {
            return keywordIcon;
        }
    }

    static class NamespaceItem extends PHPCompletionItem {

        Boolean isSmart;

        NamespaceItem(NamespaceElement namespace, CompletionRequest request, QualifiedNameKind generateAs) {
            super(namespace, request, generateAs);
        }

        @Override
        public int getSortPrioOverride() {
            return isSmart() ? -10001 : super.getSortPrioOverride();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.name(getKind(), true);
            formatter.appendText(getName());
            formatter.name(getKind(), false);

            return formatter.getText();
        }

        @Override
        public String getName() {
            return getNamespaceElement().getName();
        }

        NamespaceElement getNamespaceElement() {
            return (NamespaceElement) getElement();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.PACKAGE;
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            QualifiedName namespaceName = getNamespaceElement().getNamespaceName();
            if (namespaceName != null && !namespaceName.isDefaultNamespace()) {
                formatter.appendText(namespaceName.toString());
                return formatter.getText();
            }

            return null;
        }

        @Override
        public boolean isSmart() {
            if (isSmart == null && getElement() instanceof AliasedElement) {
                isSmart = true;
            }
            if (isSmart == null) {
                QualifiedName namespaceName = getNamespaceElement().getNamespaceName();
                isSmart = !(namespaceName == null || !namespaceName.isDefaultNamespace());
                if (!isSmart) {
                    FileScope fileScope = request.result.getModel().getFileScope();
                    NamespaceScope namespaceScope = (fileScope != null)
                            ? ModelUtils.getNamespaceScope(fileScope, request.anchor) : null;
                    if (namespaceScope != null) {
                        NamespaceElement ifq = getNamespaceElement();
                        LinkedList<String> segments = ifq.getFullyQualifiedName().getSegments();
                        QualifiedName fqna = QualifiedName.create(false, segments);
                        Collection<QualifiedName> relativeUses = VariousUtils.getRelativesToUses(namespaceScope, fqna);
                        for (QualifiedName qualifiedName : relativeUses) {
                            if (qualifiedName.getSegments().size() == 1) {
                                isSmart = true;
                                break;
                            }
                        }
                        if (!isSmart) {
                            relativeUses = VariousUtils.getRelativesToNamespace(namespaceScope, fqna);
                            for (QualifiedName qualifiedName : relativeUses) {
                                if (qualifiedName.getSegments().size() == 1) {
                                    isSmart = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return isSmart;
        }
    }

    static class ConstantItem extends PHPCompletionItem {

        ConstantItem(ConstantElement constant, CompletionRequest request) {
            super(constant, request);
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            String value = ((ConstantElement) getElement()).getValue();
            formatter.name(getKind(), true);
            if (emphasisName()) {
                formatter.emphasis(true);
                formatter.appendText(getName());
                formatter.emphasis(false);
            } else {
                formatter.appendText(getName());
            }
            formatter.name(getKind(), false);
            formatter.appendText(" "); //NOI18N
            formatter.type(true);
            formatter.appendText(value != null ? value : "?");//NOI18N
            formatter.type(false);

            return formatter.getText();
        }

        protected boolean emphasisName() {
            return true;//cons.isResolved()
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CONSTANT;
        }
    }

    static class TraitItem extends PHPCompletionItem {

        private static final ImageIcon ICON = IconsUtils.getElementIcon(PhpElementKind.TRAIT);

        TraitItem(TraitElement element, CompletionRequest request) {
            super(element, request);
        }

        @Override
        public ImageIcon getIcon() {
            return ICON;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CLASS;
        }

    }

    static class ClassItem extends PHPCompletionItem {

        private boolean endWithDoubleColon;

        ClassItem(ClassElement clazz, CompletionRequest request, boolean endWithDoubleColon, QualifiedNameKind generateAs) {
            super(clazz, request, generateAs);
            this.endWithDoubleColon = endWithDoubleColon;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CLASS;
        }

        @Override
        public String getInsertPrefix() {
            final String insertPrefix = super.getInsertPrefix();
            int indexOf = (request.prefix != null && insertPrefix != null) ? insertPrefix.toLowerCase().indexOf(request.prefix.toLowerCase()) : -1;
            return indexOf > 0 ? insertPrefix.substring(indexOf) : insertPrefix;
        }

        @Override
        public String getCustomInsertTemplate() {
            final String superTemplate = super.getInsertPrefix();
            if (endWithDoubleColon) {
                StringBuilder builder = new StringBuilder();
                if (superTemplate != null) {
                    builder.append(superTemplate);
                } else {
                    builder.append(getName());
                }
                boolean includeDoubleColumn = true;
                if (EditorRegistry.lastFocusedComponent() != null) {
                    Document doc = EditorRegistry.lastFocusedComponent().getDocument();
                    int caret = EditorRegistry.lastFocusedComponent().getCaretPosition();
                    try {
                        if (caret + 2 < doc.getLength() && "::".equals(doc.getText(caret, 2))) { //NOI18N
                            includeDoubleColumn = false;
                        }
                    } catch (BadLocationException ex) {
                        // do nothing
                    }
                }

                if (includeDoubleColumn) {
                    builder.append("::");
                }
                builder.append("${cursor}"); //NOI18N
                scheduleShowingCompletion();
                return builder.toString();
            } else if (CompletionContext.NEW_CLASS.equals(request.context)) {
                scheduleShowingCompletion();
            }
            return superTemplate;
        }
    }

    public static ImageIcon getInterfaceIcon() {
        return InterfaceItem.icon();
    }

    static class InterfaceItem extends PHPCompletionItem {

        private static final String PHP_INTERFACE_ICON = "org/netbeans/modules/php/editor/resources/interface.png"; //NOI18N
        private static ImageIcon INTERFACE_ICON = null;
        private boolean endWithDoubleColon;

        InterfaceItem(InterfaceElement iface, CompletionRequest request, boolean endWithDoubleColon) {
            super(iface, request);
            this.endWithDoubleColon = endWithDoubleColon;
        }

        InterfaceItem(InterfaceElement iface, CompletionRequest request, QualifiedNameKind generateAs, boolean endWithDoubleColon) {
            super(iface, request, generateAs);
            this.endWithDoubleColon = endWithDoubleColon;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CLASS;
        }

        private static ImageIcon icon() {
            if (INTERFACE_ICON == null) {
                INTERFACE_ICON = new ImageIcon(ImageUtilities.loadImage(PHP_INTERFACE_ICON));
            }
            return INTERFACE_ICON;
        }

        @Override
        public ImageIcon getIcon() {
            return icon();
        }

        @Override
        public String getInsertPrefix() {
            final String insertPrefix = super.getInsertPrefix();
            int indexOf = (request.prefix != null && insertPrefix != null) ? insertPrefix.toLowerCase().indexOf(request.prefix.toLowerCase()) : -1;
            return indexOf > 0 ? insertPrefix.substring(indexOf) : insertPrefix;
        }

        @Override
        public String getCustomInsertTemplate() {
            final String superTemplate = super.getInsertPrefix();
            if (endWithDoubleColon) {
                StringBuilder builder = new StringBuilder();
                if (superTemplate != null) {
                    builder.append(superTemplate);
                } else {
                    builder.append(getName());
                }
                builder.append("::${cursor}"); //NOI18N
                scheduleShowingCompletion();
                return builder.toString();
            }
            return superTemplate;
        }
    }

    static class VariableItem extends PHPCompletionItem {

        VariableItem(VariableElement variable, CompletionRequest request) {
            super(variable, request);
        }

        VariableElement getVariable() {
            return (VariableElement) getElement();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.type(true);
            formatter.appendText(getTypeName());
            formatter.type(false);
            formatter.appendText(" "); //NOI18N
            formatter.name(getKind(), true);
            formatter.appendText(getName());
            formatter.name(getKind(), false);

            return formatter.getText();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.VARIABLE;
        }

        @Override
        public String getInsertPrefix() {
            Completion.get().showToolTip();
            return getName();
        }

        protected String getTypeName() {
            Set<TypeResolver> types = getVariable().getInstanceTypes();
            String typeName = types.isEmpty() ? "?" : types.size() > 1 ? "mixed" : "?";//NOI18N
            if (types.size() == 1) {
                TypeResolver typeResolver = types.iterator().next();
                if (typeResolver.isResolved()) {
                    QualifiedName qualifiedName = typeResolver.getTypeName(false);
                    if (qualifiedName != null) {
                        typeName = qualifiedName.toString();
                    }
                }
            }
            return typeName;
        }
    }

    @NbBundle.Messages("LBL_LANGUAGE_CONSTRUCT=Language Construct")
    abstract static class LanguageConstructItem extends KeywordItem {

        private static final String SORT_AFTER_KEYWORDS = "z"; // NOI18N

        public LanguageConstructItem(String fncName, CompletionRequest request) {
            super(fncName, request);
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            formatter.appendText(Bundle.LBL_LANGUAGE_CONSTRUCT());
            return formatter.getText();
        }

        @Override
        public String getSortText() {
            return SORT_AFTER_KEYWORDS + super.getSortText();
        }

        protected void prependName(HtmlFormatter formatter) {
            formatter.name(getKind(), true);
            formatter.appendText(getName());
            formatter.name(getKind(), false);
        }
    }

    static class LanguageConstructWithQuotesItem extends LanguageConstructItem {

        public LanguageConstructWithQuotesItem(String fncName, CompletionRequest request) {
            super(fncName, request);
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder builder = new StringBuilder();
            builder.append(getName());
            builder.append(" '${cursor}';"); // NOI18N
            return builder.toString();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            prependName(formatter);
            formatter.appendText(" '';"); // NOI18N
            return formatter.getText();
        }
    }

    static class LanguageConstructWithParenthesesItem extends LanguageConstructItem {

        public LanguageConstructWithParenthesesItem(String fncName, CompletionRequest request) {
            super(fncName, request);
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder builder = new StringBuilder();
            builder.append(getName());
            builder.append("(${cursor})"); // NOI18N
            return builder.toString();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            prependName(formatter);
            formatter.appendText("()"); // NOI18N
            return formatter.getText();
        }
    }

    static class LanguageConstructWithSemicolonItem extends LanguageConstructItem {

        public LanguageConstructWithSemicolonItem(String fncName, CompletionRequest request) {
            super(fncName, request);
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder builder = new StringBuilder();
            builder.append(getName());
            builder.append(" ${cursor};"); // NOI18N
            return builder.toString();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            prependName(formatter);
            formatter.appendText(" ;"); // NOI18N
            return formatter.getText();
        }
    }

    static class LanguageConstructForTypeHint extends LanguageConstructItem {

        public LanguageConstructForTypeHint(String fncName, CompletionRequest request) {
            super(fncName, request);
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder builder = new StringBuilder();
            builder.append(getName());
            builder.append("${cursor}"); // NOI18N
            return builder.toString();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            prependName(formatter);
            return formatter.getText();
        }
    }

    static class TagItem extends KeywordItem {

        private int sortKey;

        public TagItem(String tag, int sortKey, CompletionRequest request) {
            super(tag, request);
            this.sortKey = sortKey;
        }

        @Override
        public String getSortText() {
            return "" + sortKey + getName();
        }
    }

    public static class CompletionRequest {

        public int anchor;
        public PHPParseResult result;
        public ParserResult info;
        public String prefix;
        public String currentlyEditedFileURL;
        public CompletionContext context;
        ElementQuery.Index index;
    }

    private static void scheduleShowingCompletion() {
        if (OptionsUtils.autoCompletionTypes()) {
            service.schedule(new Runnable() {

                @Override
                public void run() {
                    Completion.get().showCompletion();
                }
            }, 750, TimeUnit.MILLISECONDS);
        }
    }

    private class PhpVersionChangeListener implements PropertyChangeListener {

        private final WeakReference<FileObject> fileObjectReference;

        public PhpVersionChangeListener(FileObject fileObject) {
            this.fileObjectReference = new WeakReference<FileObject>(fileObject);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PhpLanguageProperties.PROP_PHP_VERSION.equals(evt.getPropertyName())) {
                FileObject fileObject = fileObjectReference.get();
                if (fileObject != null) {
                    PROPERTIES_CACHE.save(fileObject, PhpLanguageProperties.forFileObject(fileObject));
                }
            }
        }
    }
}
