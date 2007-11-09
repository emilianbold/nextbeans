/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.products.jdk;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.StringUtils;
import static org.netbeans.installer.utils.StringUtils.QUOTE;
import static org.netbeans.installer.utils.StringUtils.BACK_SLASH;
import static org.netbeans.installer.utils.StringUtils.EMPTY_STRING;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.applications.JavaUtils;
import static org.netbeans.installer.utils.applications.JavaUtils.JDK_KEY;
import static org.netbeans.installer.utils.applications.JavaUtils.JRE_KEY;
import static org.netbeans.installer.utils.applications.JavaUtils.JAVAHOME_VALUE;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.NativeException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.EnvironmentScope;
import org.netbeans.installer.utils.helper.ExecutionResults;
import org.netbeans.installer.utils.helper.NbiThread;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.helper.RemovalMode;
import org.netbeans.installer.utils.helper.Status;
import org.netbeans.installer.utils.progress.CompositeProgress;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.utils.system.launchers.LauncherResource;
import static org.netbeans.installer.utils.system.windows.WindowsRegistry.HKLM;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.WizardComponent;

/**
 *
 * @author Dmitry Lipin
 */
public class ConfigurationLogic extends ProductConfigurationLogic {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private List<WizardComponent> wizardComponents;
    
    public ConfigurationLogic() throws InitializationException {
        wizardComponents = Wizard.loadWizardComponents(
                WIZARD_COMPONENTS_URI,
                getClass().getClassLoader());
    }
    
    public void install(
            final Progress progress) throws InstallationException {
        if(progress.isCanceled()) return;
        
        final File location = getProduct().getInstallationLocation();
        final File installer = new File(location, JDK_INSTALLER_FILE_NAME);
        
        try {
            File logFile = LogManager.getLogFile();
            File jdkLogFile = null;
            File jreLogFile = null;
            
            if(logFile!=null) {
                String nameJdk = logFile.getName();
                String nameJre = logFile.getName();
                
                if(nameJdk.lastIndexOf(".")==-1) {
                    nameJdk += "_jdk.log";
                    nameJre += "_jre.log";
                } else {
                    String ext = nameJdk.substring(nameJdk.lastIndexOf("."));
                    nameJdk = nameJdk.substring(0, nameJdk.lastIndexOf(".")) +
                            "_jdk" + ext;
                    nameJre = nameJre.substring(0, nameJre.lastIndexOf(".")) +
                            "_jre" + ext;
                }
                
                jdkLogFile = new File(LogManager.getLogFile().getParentFile(),nameJdk);
                jreLogFile = new File(LogManager.getLogFile().getParentFile(),nameJre);
            }
            if(!SystemUtils.isWindows()) {
                jdkLogFile = null;
                jreLogFile = null;
            }
            String [] commands = null;
            ExecutionResults results = null;
            
            
            boolean jreInstallation = false;
            final CompositeProgress overallProgress = new CompositeProgress();
            overallProgress.synchronizeTo(progress);
            overallProgress.synchronizeDetails(true);
            
            if(SystemUtils.isWindows()) {
                final File jdk = JavaUtils.findJDKHome(getProduct().getVersion());
                final File jre = JavaUtils.findJreHome(getProduct().getVersion());
                if (jdk == null) {
                    final Progress jdkProgress = new Progress();
                    final Progress jreProgress = new Progress();
                    if(jre!=null) {
                        overallProgress.addChild(jdkProgress, progress.COMPLETE);
                    } else {
                        overallProgress.addChild(jdkProgress, progress.COMPLETE * 3 / 5 );
                        overallProgress.addChild(jreProgress, progress.COMPLETE * 2 / 5);
                    }
                    results = runJDKInstallerWindows(location, installer, jdkLogFile, jdkProgress);
                    addUninsallationJVM(results, location);
                    
                    jdkProgress.setPercentage(Progress.COMPLETE);
                    if(!progress.isCanceled() && results.getErrorCode()==0) {
                        if(jre == null) {
                            jreInstallation = true;
                            final File jreInstaller = findJREWindowsInstaller();
                            if(jreInstaller!=null) {
                                results = runJREInstallerWindows(jreInstaller,jreLogFile, jreProgress);
                                jreProgress.setPercentage(Progress.COMPLETE);
                                addUninsallationJVM(results, JavaUtils.findJreHome(getProduct().getVersion()));
                            }
                        } else {
                            LogManager.log("... jre " + getProduct().getVersion() +
                                    " is already installed, skipping its configuration");
                        }
                    }
                } else {
                    LogManager.log("... jdk " + getProduct().getVersion() +
                            " is already installed, skipping JDK and JRE configuration");
                }
            } else {
                final Progress jdkProgress = new Progress();
                overallProgress.addChild(jdkProgress,Progress.COMPLETE);
                results = runJDKInstallerUnix(location, installer, jdkLogFile, jdkProgress);
                addUninsallationJVM(results, location);
            }
            
            
            if(results.getErrorCode()!=0) {
                throw new InstallationException(
                        ResourceUtils.getString(ConfigurationLogic.class,
                        (jreInstallation) ? ERROR_JRE_INSTALL_SCRIPT_RETURN_NONZERO_KEY
                        : ERROR_JDK_INSTALL_SCRIPT_RETURN_NONZERO_KEY,
                        StringUtils.EMPTY_STRING + results.getErrorCode()));
            }
        }  finally {
            try {
                FileUtils.deleteFile(installer);
            } catch (IOException e) {
                LogManager.log("Cannot delete installer file "+ installer, e);
                
            }
        }
        
        /////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);
    }
    
    private ExecutionResults runJDKInstallerWindows(File location, File installer, File logFile, Progress progress) throws InstallationException {
        progress.setDetail(PROGRESS_DETAIL_RUNNING_JDK_INSTALLER);
        
        try {
            SystemUtils.setEnvironmentVariable("TEMP",
                    SystemUtils.getTempDirectory().getAbsolutePath(),
                    EnvironmentScope.PROCESS,
                    false);
            SystemUtils.setEnvironmentVariable("TMP",
                    SystemUtils.getTempDirectory().getAbsolutePath(),
                    EnvironmentScope.PROCESS,
                    false);
        } catch (NativeException e) {
            throw new InstallationException(
                    ResourceUtils.getString(ConfigurationLogic.class,
                    ERROR_INSTALL_JDK_ERROR_KEY),e);
        }
        
        final String loggingOption = (logFile!=null) ?
            "/log " + BACK_SLASH + QUOTE  + logFile.getAbsolutePath()  + BACK_SLASH + QUOTE +" ":
            EMPTY_STRING;
        final String installLocationOption = "/qn INSTALLDIR=" + BACK_SLASH + QUOTE + location + BACK_SLASH + QUOTE;
        LogManager.log("... JDK installation log file : " + logFile);
        
        String [] commands = new String [] {
            installer.getAbsolutePath(),
            "/s",
            "/v" + loggingOption + installLocationOption};
        
        ProgressThread progressThread = new ProgressThread(progress,location,getJDKinstallationSize());
        try {
            progressThread.start();
            return SystemUtils.executeCommand(location, commands);
        } catch (IOException e) {
            throw new InstallationException(
                    ResourceUtils.getString(ConfigurationLogic.class,
                    ERROR_INSTALL_JDK_ERROR_KEY),e);
        } finally {
            progressThread.finish();
        }
    }
    
    
    
    private ExecutionResults runJDKInstallerUnix(File location, File installer, File logFile, Progress progress) throws InstallationException {
        File yesFile = null;
        ExecutionResults results = null;
        try {
            progress.setDetail(PROGRESS_DETAIL_RUNNING_JDK_INSTALLER);
            yesFile = FileUtils.createTempFile();
            FileUtils.writeFile(yesFile, "yes" + SystemUtils.getLineSeparator());
            
            final String loggingOption = (logFile!=null) ?
                " > " + logFile.getAbsolutePath() + " 2>&1"  :
                EMPTY_STRING;
            
            SystemUtils.correctFilesPermissions(installer);
            
            String [] commands = new String [] {
                "/bin/sh", "-c",
                installer.getAbsolutePath() +
                        " < " + yesFile.getAbsolutePath() +
                        loggingOption
            };
            ProgressThread progressThread = new ProgressThread(progress,location, getJDKinstallationSize());
            try {
                progressThread.start();
                results = SystemUtils.executeCommand(location, commands);
            } finally {
                progressThread.finish();
            }
            // unix JDK installers create extra level directory jdkxxx
            File [] jdkDirs = location.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return (pathname.isDirectory() &&
                            pathname.getName().startsWith("jdk"));
                }
            });
            
            try {
                for(File dir : jdkDirs) {
                    for(File f : dir.listFiles()) {
                        SystemUtils.executeCommand("mv", "-f", f.getPath(), location.getAbsolutePath());
                    }
                    FileUtils.deleteFile(dir);
                }
            }  catch (IOException e) {
                throw new InstallationException(
                        ResourceUtils.getString(ConfigurationLogic.class,
                        ERROR_INSTALL_CANNOT_MOVE_DATA_KEY),e);
            }
            
            
        } catch (IOException e) {
            throw new InstallationException(
                    ResourceUtils.getString(ConfigurationLogic.class,
                    ERROR_INSTALL_JDK_ERROR_KEY),e);
        } finally {
            if(yesFile!=null) {
                try {
                    FileUtils.deleteFile(yesFile);
                } catch (IOException e) {
                    LogManager.log(e);
                }
            }
        }
        return results;
    }
    
    private ExecutionResults runJREInstallerWindows(File jreInstaller, File logFile, Progress progress) throws InstallationException {
        progress.setDetail(PROGRESS_DETAIL_RUNNING_JRE_INSTALLER);
        final String [] command = new String [] {
            "msiexec.exe",
            "/qn",
            "/i",
            jreInstaller.getPath(),
            "IEXPLORER=1",
            "MOZILLA=1",
            "/log",
            logFile.getAbsolutePath()
        };
        
        LogManager.log("... JRE installation log file : " + logFile);
        
        final File location = new File(parseString("$E{ProgramFiles}"),
                "Java\\jre" + getProduct().getVersion().toJdkStyle());
        LogManager.log("... JRE installation location (default) : " + location);
        
        try {
            SystemUtils.setEnvironmentVariable("TEMP",
                    SystemUtils.getTempDirectory().getAbsolutePath(),
                    EnvironmentScope.PROCESS,
                    false);
            SystemUtils.setEnvironmentVariable("TMP",
                    SystemUtils.getTempDirectory().getAbsolutePath(),
                    EnvironmentScope.PROCESS,
                    false);
            
        }  catch (NativeException e) {
            throw new InstallationException(
                    ResourceUtils.getString(ConfigurationLogic.class,
                    ERROR_INSTALL_JRE_ERROR_KEY),e);
        }
        ProgressThread progressThread = new ProgressThread(progress,location, getJREinstallationSize());
        try {
            progressThread.start();
            return SystemUtils.executeCommand(command);
        } catch (IOException e) {
            throw new InstallationException(
                    ResourceUtils.getString(ConfigurationLogic.class,
                    ERROR_INSTALL_JRE_ERROR_KEY),e);
        } finally {
            progressThread.finish();
        }        
    }
    private void addUninsallationJVM(ExecutionResults results, File location) {
        if(results!=null && results.getErrorCode()==0 && location!=null) {
            SystemUtils.getNativeUtils().addUninstallerJVM(new LauncherResource(false, location));
        }
    }
    /** Find path to public JRE installer ie. jre.msi file WITHOUT file itself.
     * @return null if jre.msi file for given JRE version is not found
     */
    private File findJREWindowsInstaller() {
        String installerName = null;
        
        File baseImagesDir  = new File(parseString("$E{CommonProgramFiles}"),
                "Java\\Update\\Base Images");
        if (!baseImagesDir.exists()) {
            LogManager.log("... cannot find images dir : " + baseImagesDir);
            return null;
        }
        
        File [] files = baseImagesDir.listFiles();
        File jdkDirFile = null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(JDK_PATCH_DIRECTORY)) {
                LogManager.log("... using JDK dir : " + files[i]);
                jdkDirFile = files[i];
                break;
            }
        }
        if (jdkDirFile==null) {
            LogManager.log("... cannot find default JDK dir");
            return null;
        }
        if (!jdkDirFile.exists()) {
            LogManager.log("... default JDK directory does not exist : " + jdkDirFile);
            return null;
        }
        
        files = jdkDirFile.listFiles();
        File patchDirFile = null;
        
        for (int i = 0; i < files.length; i++) {
            LogManager.log("... investigating : " + files [i]);
            if (files[i].getName().startsWith("patch-" + JDK_DEFAULT_INSTALL_DIR)) {
                patchDirFile = files[i];
                LogManager.log("... using JDK patch dir : " + patchDirFile);
                break;
            }
        }
        if (patchDirFile==null) {
            LogManager.log("... cannot find default JDK patch dir");
            return null;
        }
        if (!patchDirFile.exists()) {
            LogManager.log("... default JDK patch directory does not exist : " + patchDirFile);
            return null;
        }
        File jreInstallerFile = new File(patchDirFile,
                JRE_MSI_NAME);
        if (!jreInstallerFile.exists()) {
            LogManager.log("... JRE installer doesn`t exist : " + jreInstallerFile);
            return null;
        }
        LogManager.log("... found JRE windows installer at " + jreInstallerFile.getPath());
        return jreInstallerFile;
    }
    private long getJREinstallationSize() {
        return getProduct().getVersion().getMinor()==5 ?
            70000000L :
            (getProduct().getVersion().getMinor()==6 ?
                90000000L :
                100000000L);
    }
    private long getJDKinstallationSize() {
        final long size;
        if(getProduct().getVersion().getMinor()==5) {
            if(SystemUtils.isWindows()) {
                size = 140000000L ;
            } else if(SystemUtils.isLinux()){
                size = 150000000L ;
            } else if(SystemUtils.getCurrentPlatform().isCompatibleWith(Platform.SOLARIS_SPARC)) {
                size = 148000000L;
            } else if(SystemUtils.getCurrentPlatform().isCompatibleWith(Platform.SOLARIS_X86)) {
                size = 140000000L;
            } else {
                // who knows...
                size = 160000000L;
            }
        } else if(getProduct().getVersion().getMinor()==6) {
            if(SystemUtils.isWindows()) {
                size = 170000000L ;
            } else if(SystemUtils.isLinux()){
                size = 200000000L ;
            } else if(SystemUtils.getCurrentPlatform().isCompatibleWith(Platform.SOLARIS_SPARC)) {
                size = 178000000L;
            } else if(SystemUtils.getCurrentPlatform().isCompatibleWith(Platform.SOLARIS_X86)) {
                size = 170000000L;
            } else {
                // who knows...
                size = 180000000L;
            }
        } else {
            // who knows...
            size = 200000000L;
        }
        return size;
    }
    
    @Override
    public boolean registerInSystem() {
        return false;
    }
    
    public void uninstall(
            final Progress progress) throws UninstallationException {
        final File location = getProduct().getInstallationLocation();
        
        /////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);
    }
    
    public List<WizardComponent> getWizardComponents() {
        return wizardComponents;
    }
    
    @Override
    public String getIcon() {
        if (SystemUtils.isWindows()) {
            return "bin/javaws.exe";
        } else {
            return null;
        }
    }
    @Override
    public int getLogicPercentage() {
        return 90;
    }
    
    @Override
    public boolean allowModifyMode() {
        return false;
    }
    public RemovalMode getRemovalMode() {
        return RemovalMode.ALL;
    }
    
    @Override
    public String validateInstallation() {
        if(super.validateInstallation()!=null) {
            LogManager.log("JDK validation:");
            LogManager.log(super.validateInstallation());
            getProduct().setStatus(Status.NOT_INSTALLED);
            getProduct().getParent().removeChild(getProduct());
        }
        return null;
    }
    class ProgressThread extends NbiThread {
        private File directory ;
        private long deltaSize = 0;
        private long initialSize = 0L;
        private Progress progress;
        private final Object LOCK = new Object();
        private boolean loop = false;
        
        public ProgressThread(Progress progress, File directory, final long maxDeltaSize) {
            LogManager.log("... new ProgressThread created");
            this.directory = directory;
            if(directory.exists()) {
                initialSize = FileUtils.getSize(directory);
            }
            this.deltaSize = maxDeltaSize;
            this.progress = progress;
            LogManager.log("... directory : " + directory);
            LogManager.log("...   initial : " + initialSize);
            LogManager.log("...     delta : " + deltaSize);
        }
        public void run() {
            LogManager.log("... progress thread started");
            long sleepTime = 1000L;
            try {
                synchronized (LOCK) {
                    loop = true;
                }                
                while (isRunning()) {
                    try {                         
                        if (directory.exists()) {                            
                            updateProgressBar();
                        }                               
                        Thread.currentThread().sleep(sleepTime);                        
                    } catch (InterruptedException ex) {                        
                        LogManager.log(ex);
                        break;
                    } catch (Exception ex) {                        
                        LogManager.log(ex);
                        break;
                    }
                }
            }  finally {                                
                synchronized (LOCK) {                    
                    LOCK.notify();                    
                }
            }
            progress.setPercentage(Progress.COMPLETE);
            LogManager.log("... progress thread finished");
        }
        public void finish() {            
            if(!isRunning()) return;            
            synchronized (LOCK) {
                loop = false;
            }                        
            synchronized (LOCK) {                
                try {                                        
                    LOCK.wait();                                      
                } catch (InterruptedException e){
                    LogManager.log(e);                    
                }
            }            
        }
        private boolean isRunning() {                        
            boolean result;
            synchronized (LOCK) {                                
                result = loop;
            }                        
            return result;
        }
        private void updateProgressBar() {
            LogManager.log("... get directory size");
            long size = FileUtils.getSize(directory);
            LogManager.log("... size : " + size);
            long d = progress.COMPLETE * (size - initialSize) / deltaSize;
            LogManager.log(".... real progress : " + d);
            d = progress.getPercentage() + (d  - progress.getPercentage() + 1) / 2;
            LogManager.log("... bound progress : " + d);
            d = (d<0) ? 0 : (d > progress.COMPLETE ? progress.COMPLETE : d);
            if(((int)d) > progress.getPercentage()) {
                LogManager.log("..... set progress : " + d);
                progress.setPercentage(d);
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////
// Constants
    public static final String WIZARD_COMPONENTS_URI =
            "resource:" + // NOI18N
            "org/netbeans/installer/products/jdk/wizard.xml"; // NOI18N
    
    
    public static final String JDK_INSTALLER_FILE_NAME =
            ResourceUtils.getString(ConfigurationLogic.class,
            "CL.jdk.installer.file");
    public static final String ERROR_JDK_INSTALL_SCRIPT_RETURN_NONZERO_KEY =
            "CL.error.jdk.installation.return.nonzero";//NOI18N
    public static final String ERROR_JRE_INSTALL_SCRIPT_RETURN_NONZERO_KEY =
            "CL.error.jre.installation.return.nonzero";//NOI18N
    public static final String ERROR_INSTALL_JDK_ERROR_KEY =
            "CL.error.install.jdk.exception";//NOI18N
    public static final String ERROR_INSTALL_JRE_ERROR_KEY =
            "CL.error.install.jre.exception";//NOI18N
    public static final String ERROR_INSTALL_CANNOT_MOVE_DATA_KEY =
            "CL.error.install.cannot.move.data";//NOI18N
    public static final String PROGRESS_DETAIL_RUNNING_JDK_INSTALLER =
            ResourceUtils.getString(ConfigurationLogic.class,
            "CL.progress.detail.install.jdk");
    public static final String PROGRESS_DETAIL_RUNNING_JRE_INSTALLER =
            ResourceUtils.getString(ConfigurationLogic.class,
            "CL.progress.detail.install.jre");
    
    public static final String JDK_PATCH_DIRECTORY =
            ResourceUtils.getString(ConfigurationLogic.class,
            "CL.jdk.patch.directory");//NOI18N
    public static final String JDK_DEFAULT_INSTALL_DIR =
            ResourceUtils.getString(ConfigurationLogic.class,
            "CL.jdk.install.dir");//NOI18N
    public static final String JRE_MSI_NAME =
            "jre.msi";//NOI18N
}
