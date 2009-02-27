/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
package org.netbeans.modules.dlight.management.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.netbeans.modules.dlight.api.collector.DataCollectorConfiguration;
import org.netbeans.modules.dlight.api.execution.DLightTarget;
import org.netbeans.modules.dlight.api.execution.Validateable;
import org.netbeans.modules.dlight.api.execution.ValidationListener;
import org.netbeans.modules.dlight.api.execution.ValidationStatus;
import org.netbeans.modules.dlight.api.impl.DLightToolConfigurationAccessor;
import org.netbeans.modules.dlight.api.indicator.IndicatorConfiguration;
import org.netbeans.modules.dlight.api.indicator.IndicatorDataProviderConfiguration;
import org.netbeans.modules.dlight.api.tool.DLightToolConfiguration;
import org.netbeans.modules.dlight.management.api.impl.DLightToolAccessor;
import org.netbeans.modules.dlight.spi.collector.DataCollector;
import org.netbeans.modules.dlight.spi.impl.DataCollectorProvider;
import org.netbeans.modules.dlight.spi.indicator.Indicator;
import org.netbeans.modules.dlight.spi.indicator.IndicatorDataProvider;
import org.netbeans.modules.dlight.spi.impl.IDPProvider;
import org.netbeans.modules.dlight.spi.impl.IndicatorProvider;
import org.netbeans.modules.dlight.util.DLightExecutorService;
import org.netbeans.modules.dlight.util.DLightLogger;
import org.openide.util.Exceptions;


/**
 * D-Light Tool is a set of registered collector used to collect data,
 * set of indicators to display in Indicators Panel when tool is running
 * You should implement interface {@link org.netbeans.modules.dlight.core.model.DLightToolConfigurationProvider}
 * which should create new instance of {@link org.netbeans.modules.dlight.core.model.DLightTool.Configuration}
 * object each time create() method is invoked and register it in D-Light filesystem
 */
public final class DLightTool implements Validateable<DLightTarget> {

  private static final Logger log = DLightLogger.getLogger(DLightTool.class);
  private String toolName;
  private boolean enabled;
  private final List<DataCollector> dataCollectors;
  private final List<IndicatorDataProvider> indicatorDataProviders;
  private final List<Indicator> indicators;
  private ValidationStatus validationStatus = ValidationStatus.initialStatus();
  private final List<ValidationListener> validationListeners = Collections.synchronizedList(new ArrayList<ValidationListener>());
  private boolean collectorsTurnedOn = true;
  //register accessor which will be used ne friend packages of API/SPI accessor packages
  //to get access to tool creation, etc.
  

  static {
    DLightToolAccessor.setDefault(new DLightToolAccessorImpl());
  }

  private DLightTool(DLightToolConfiguration configuration) {
    this.toolName = DLightToolConfigurationAccessor.getDefault().getToolName(configuration);
    dataCollectors = Collections.synchronizedList(new ArrayList<DataCollector>());
    indicators = Collections.synchronizedList(new ArrayList<Indicator>());
    indicatorDataProviders = Collections.synchronizedList(new ArrayList<IndicatorDataProvider>());
    List<DataCollectorConfiguration> configurations = DLightToolConfigurationAccessor.getDefault().getDataCollectors(configuration);
    List<IndicatorDataProviderConfiguration> idpConfigurations = DLightToolConfigurationAccessor.getDefault().getIndicatorDataProviders(configuration);

    for (DataCollectorConfiguration conf : configurations) {
      DataCollector collector = DataCollectorProvider.getInstance().createDataCollector(conf);
      if (collector == null) {
        log.info("Could not find DataCollector for configuration with id:" + conf.getID() + " check if " +
            "DataColelctorFactory is registered in Global Lookup with the same ID");
        continue;
      }
      registerCollector(collector);
      if (collector instanceof IndicatorDataProvider){
        registerIndicatorDataProvider((IndicatorDataProvider)collector);
      }
    }
    for (IndicatorDataProviderConfiguration idp : idpConfigurations) {
      //we could create already object
      if (!configurations.contains(idp)) {
        IndicatorDataProvider indDataProvider = IDPProvider.getInstance().create(idp);
        if (indDataProvider == null) {
          log.info("Could not find IndicatorDataProvider for configuration with id:" + idp.getID() + " check if " +
              "IndicatorDataPeoviderFactory is registered in Global Lookup with the same ID");
          continue;

        }
        registerIndicatorDataProvider(indDataProvider);
      }
    }

    List<IndicatorConfiguration> indConfigurationsList = DLightToolConfigurationAccessor.getDefault().getIndicators(configuration);
    for (IndicatorConfiguration indConfiguration : indConfigurationsList) {
      Indicator indicator = IndicatorProvider.getInstance().createIndicator(toolName, indConfiguration);
      if (indicator == null) {
        log.info("Could not find Indicator for configuration with id:" + indConfiguration.getID() + " check if " +
            "IndicatorFactory is registered in Global Lookup with the same ID");
        continue;

      }

      addIndicator(indicator);
      // Name is already set in IndicatorProvider.createIndicator()
      // IndicatorAccessor.getDefault().setToolName(indicator, toolName);
    }
  }

  static DLightTool newDLightTool(DLightToolConfiguration configuration) {
    return new DLightTool(configuration);
  }

  /**
   *
   * @return false if tool cannot be validated
   */
  public final Future<Boolean> enable(final DLightTarget target) {
    return DLightExecutorService.service.submit(new Callable<Boolean>() {

      public Boolean call() throws Exception {
        if (!validationStatus.isKnown()) {
          validationStatus = validate(target).get();
        }

        enabled = validationStatus.isValid();
        return enabled == true;
      }
    });
  }

  /**
   * Disable tool
   */
  public final void disable() {
    enabled = false;
  }

  public void turnCollectorsState(boolean turnedOn){
      collectorsTurnedOn = turnedOn;
  }

  public boolean collectorsTurnedOn(){
    return collectorsTurnedOn;
  }

  public ValidationStatus getValidationStatus() {
    return validationStatus;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public final String getName() {
    return toolName;
  }

  final List<DataCollector> getCollectors() {
    return dataCollectors;
  }

  private final void registerIndicatorDataProvider(IndicatorDataProvider idp) {
    if (!indicatorDataProviders.contains(idp)) {
      indicatorDataProviders.add(idp);
    }
  }

  List<IndicatorDataProvider> getIndicatorDataProviders() {
    return indicatorDataProviders;
  }

  private void addIndicator(Indicator indicator) {
    if (!indicators.contains(indicator)) {
      indicators.add(indicator);
    }
  }

  final List<Indicator> getIndicators() {
    return indicators;
  }

  void registerCollector(DataCollector collector) {
    if (collector == null) {
      log.info("Cannot register collector");
    }
    if (!dataCollectors.contains(collector)) {
      dataCollectors.add(collector);
    }

    collector.addValidationListener(new ValidationListener() {

      public void validationStateChanged(Validateable source, ValidationStatus oldStatus, ValidationStatus newStatus) {
        notifyStatusChanged(oldStatus, newStatus);
      }
    });
  }

  public final Future<ValidationStatus> validate(final DLightTarget target) {
    return DLightExecutorService.service.submit(new Callable<ValidationStatus>() {

      public ValidationStatus call() throws Exception {
        if (validationStatus.isValid()) {
          return validationStatus;
        }

        ValidationStatus oldStatus = validationStatus;
        ValidationStatus newStatus = doValidation(target);

        notifyStatusChanged(oldStatus, newStatus);

        validationStatus = newStatus;
        return newStatus;
      }
    });
  }

  public final void invalidate() {
    validationStatus = ValidationStatus.initialStatus();
    notifyStatusChanged(null, validationStatus);
  }

  final synchronized ValidationStatus doValidation(DLightTarget target) {
    // VK: in the case there are collectors, consider the tool valid
    if (dataCollectors.isEmpty()) {
        return ValidationStatus.validStatus();
    }
    ValidationStatus result = ValidationStatus.initialStatus();

    for (DataCollector dc : dataCollectors) {
      try {
          Future<ValidationStatus> validationTask = dc.validate(target);
          result = result.merge(validationTask.get());
      } catch (InterruptedException ex) {
        Exceptions.printStackTrace(ex);
      } catch (ExecutionException ex) {
        Exceptions.printStackTrace(ex);
      }

      if (result.isInvalid()) {
        break;
      }
    }

    return result;
  }

  public final void addValidationListener(ValidationListener listener) {
    if (!validationListeners.contains(listener)) {
      validationListeners.add(listener);
    }
  }

  public final void removeValidationListener(ValidationListener listener) {
    validationListeners.remove(listener);
  }

  private final void notifyStatusChanged(ValidationStatus oldStatus, ValidationStatus newStatus) {
      if (oldStatus.equals(newStatus)) {
          return;
      }
    for (ValidationListener validationListener : validationListeners) {
      validationListener.validationStateChanged(this, oldStatus, newStatus);
    }
  }

  @Override
  public String toString() {
    return getName();
  }

  private static final class DLightToolAccessorImpl extends DLightToolAccessor {

    @Override
    public List<IndicatorDataProvider> getIndicatorDataProviders(DLightTool tool) {
      return tool.getIndicatorDataProviders();
    }

    @Override
    public DLightTool newDLightTool(DLightToolConfiguration configuration) {
      return new DLightTool(configuration);
    }

    @Override
    public List<Indicator> getIndicators(DLightTool tool) {
      return tool.getIndicators();
    }

    @Override
    public List<DataCollector> getCollectors(DLightTool tool) {
      return tool.getCollectors();
    }
  }

}
