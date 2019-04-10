
package com.hanlin.fadp.entityext.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.container.Container;
import com.hanlin.fadp.base.container.ContainerConfig;
import com.hanlin.fadp.base.container.ContainerException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.UtilURL;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;
import com.hanlin.fadp.entity.jdbc.DatabaseUtil;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.util.EntityDataLoader;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.entity.util.EntityUtil;
import com.hanlin.fadp.service.ServiceDispatcher;


/**
 * Some utility routines for loading seed data.
 */
public class EntityDataLoadContainer implements Container {

    public static final String module = EntityDataLoadContainer.class.getName();

    protected String overrideDelegator = null;
    protected String overrideGroup = null;
    protected String configFile = null;
    protected String readers = null;
    protected String directory = null;
    protected List<String> files = new LinkedList<String>();
    protected String component = null;
    protected boolean useDummyFks = false;
    protected boolean maintainTxs = false;
    protected boolean tryInserts = false;
    protected boolean repairColumns = false;
    protected boolean dropPks = false;
    protected boolean createPks = false;
    protected boolean dropConstraints = false;
    protected boolean createConstraints = false;
    protected int txTimeout = -1;

    private String name;

    public EntityDataLoadContainer() {
        super();
    }

    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name = name;
        this.configFile = configFile;
        // disable job scheduler, JMS listener and startup services
        // FIXME: This is not thread-safe.
        ServiceDispatcher.enableJM(false);
        ServiceDispatcher.enableJMS(false);
        ServiceDispatcher.enableSvcs(false);

        /*
           load-data arguments:
           readers (none, all, seed, demo, ext, etc - configured in entityengine.xml and associated via fadp-component.xml)
           timeout (transaction timeout default 7200)
           delegator (overrides the delegator name configured for the container)
           group (overrides the entity group name configured for the container)
           dir (imports all XML files in a directory)
           file (import a specific XML file)

           Example:
           $ java -jar fadp.jar -load-data -readers=seed,demo,ext -timeout=7200 -delegator=default -group=com.hanlin.fadp
           $ java -jar fadp.jar -load-data -file=/tmp/dataload.xml
           Currently no dashes before load-data, see OFBIZ-5872
               $ java -jar fadp.jar load-data -readers=seed,demo,ext -timeout=7200 -delegator=default -group=com.hanlin.fadp
               $ java -jar fadp.jar load-data -file=/tmp/dataload.xml
        */
        if (args != null) {
            for (String argument: args) {
                // arguments can prefix w/ a '-'. Just strip them off
                if (argument.startsWith("-")) {
                    int subIdx = 1;
                    if (argument.startsWith("--")) {
                        subIdx = 2;
                    }
                    argument = argument.substring(subIdx);
                }

                // parse the arguments
                String argumentName;
                String argumentVal;
                if (argument.indexOf("=") != -1) {
                    argumentName = argument.substring(0, argument.indexOf("="));
                    argumentVal = argument.substring(argument.indexOf("=") + 1);
                } else {
                    argumentName = argument;
                    argumentVal = "";
                }
                Debug.logInfo("Install Argument - " + argumentName + " = " + argumentVal, module);

                if ("readers".equalsIgnoreCase(argumentName)) {
                    this.readers = argumentVal;
                } else if ("timeout".equalsIgnoreCase(argumentName)) {
                    try {
                        this.txTimeout = Integer.parseInt(argumentVal);
                    } catch (Exception e) {
                        this.txTimeout = -1;
                    }
                } else if ("component".equalsIgnoreCase(argumentName)) {
                    this.component = argumentVal;
                } else if ("delegator".equalsIgnoreCase(argumentName)) {
                    this.overrideDelegator = argumentVal;
                } else if ("group".equalsIgnoreCase(argumentName)) {
                    this.overrideGroup = argumentVal;
                } else if ("file".equalsIgnoreCase(argumentName)) {
                    this.files.addAll(StringUtil.split(argumentVal, ","));
                } else if ("dir".equalsIgnoreCase(argumentName)) {
                    this.directory = argumentVal;
                } else if ("createfks".equalsIgnoreCase(argumentName)) {
                    this.useDummyFks = "true".equalsIgnoreCase(argumentVal);
                } else if ("maintainTxs".equalsIgnoreCase(argumentName)) {
                    this.maintainTxs = "true".equalsIgnoreCase(argumentVal);
                } else if ("inserts".equalsIgnoreCase(argumentName)) {
                    this.tryInserts = "true".equalsIgnoreCase(argumentVal);
                } else if ("repair-columns".equalsIgnoreCase(argumentName)) {
                    if (UtilValidate.isEmpty(argumentVal) || "true".equalsIgnoreCase(argumentVal)) {
                        repairColumns = true;
                    }
                } else if ("drop-pks".equalsIgnoreCase(argumentName)) {
                    if (UtilValidate.isEmpty(argumentVal) || "true".equalsIgnoreCase(argumentVal)) {
                        dropPks = true;
                    }
                } else if ("create-pks".equalsIgnoreCase(argumentName)) {
                    if (UtilValidate.isEmpty(argumentVal) || "true".equalsIgnoreCase(argumentVal)) {
                        createPks = true;
                    }
                } else if ("drop-constraints".equalsIgnoreCase(argumentName)) {
                    if (UtilValidate.isEmpty(argumentVal) || "true".equalsIgnoreCase(argumentVal)) {
                        dropConstraints = true;
                    }
                } else if ("create-constraints".equalsIgnoreCase(argumentName)) {
                    if (UtilValidate.isEmpty(argumentVal) || "true".equalsIgnoreCase(argumentVal)) {
                        createConstraints = true;
                    }
                } else if ("help".equalsIgnoreCase(argumentName)) {
                    //"java -jar fadp.jar -load-data [options]\n" +
                    // Currently no dashes before load-data, see OFBIZ-5872
                    String helpStr = "\n--------------------------------------\n" +
                    "java -jar fadp.jar load-data [options]\n" +
                    "-component=[name] .... only load from a specific component\n" +
                    "-delegator=[name] .... use the defined delegator (default-no-eca)\n" +
                    "-group=[name] ........ override the entity group (com.hanlin.fadp)\n" +
                    "-file=[path] ......... load a single file from location, several files separated by commas\n" +
                    "-createfks ........... create dummy (placeholder) FKs\n" +
                    "-maintainTxs ......... maintain timestamps in data file\n" +
                    "-inserts ............. use mostly inserts option\n" +
                    "-repair-columns ........... repair column sizes\n" +
                    "-drop-pks ............ drop primary keys\n" +
                    "-create-pks .......... create primary keys\n" +
                    "-drop-constraints..... drop indexes and foreign keys before loading\n" +
                    "-create-constraints... create indexes and foreign keys after loading (default is true w/ drop-constraints)\n" +
                    "-help ................ display this information\n";
                    throw new ContainerException(helpStr);
                }

                // special case
                if (this.readers == null && (!this.files.isEmpty() || this.directory != null)) {
                    this.readers = "none";
                }
            }
        }
    }

    /**
     * @see com.hanlin.fadp.base.container.Container#start()
     */
    @Override
    public boolean start() throws ContainerException {
        if("all-tenants".equals(this.overrideDelegator)) {
            if (!EntityUtil.isMultiTenantEnabled()) {
                Debug.logWarning("Multitenant is disabled. Please enable multitenant. (e.g. general.properties --> multitenant=Y)", module);
                return true;
            }
            ContainerConfig.Container cfg = ContainerConfig.getContainer(name, configFile);
            ContainerConfig.Container.Property delegatorNameProp = cfg.getProperty("delegator-name");
            String delegatorName = null;
            if (delegatorNameProp == null || UtilValidate.isEmpty(delegatorNameProp.value)) {
                throw new ContainerException("Invalid delegator-name defined in container configuration");
            } else {
                delegatorName = delegatorNameProp.value;
            }
            Delegator delegator = DelegatorFactory.getDelegator(delegatorName);
            if (delegator == null) {
                throw new ContainerException("Invalid delegator name!");
            }
            List<EntityExpr> expr = new LinkedList<EntityExpr>();
            expr.add(EntityCondition.makeCondition("disabled", EntityOperator.EQUALS, "N"));
            expr.add(EntityCondition.makeCondition("disabled", EntityOperator.EQUALS, null));
            List<GenericValue> tenantList;
            try {
                tenantList = EntityQuery.use(delegator).from("Tenant").where(expr, EntityOperator.OR).queryList();
            } catch (GenericEntityException e) {
                throw new ContainerException(e.getMessage());
            }
            for (GenericValue tenant : tenantList) {
                this.overrideDelegator = delegator.getDelegatorName() + "#" + tenant.getString("tenantId");
                loadContainer();
            }
        } else {
            loadContainer();
        }
        return true;
    }
    private void loadContainer() throws ContainerException{
        ContainerConfig.Container cfg = ContainerConfig.getContainer(name, configFile);
        ContainerConfig.Container.Property delegatorNameProp = cfg.getProperty("delegator-name");
        ContainerConfig.Container.Property entityGroupNameProp = cfg.getProperty("entity-group-name");

        String delegatorName = null;
        String entityGroupName = null;

        if (delegatorNameProp == null || UtilValidate.isEmpty(delegatorNameProp.value)) {
            throw new ContainerException("Invalid delegator-name defined in container configuration");
        } else {
            delegatorName = delegatorNameProp.value;
        }

        if (entityGroupNameProp == null || UtilValidate.isEmpty(entityGroupNameProp.value)) {
            throw new ContainerException("Invalid entity-group-name defined in container configuration");
        } else {
            entityGroupName = entityGroupNameProp.value;
        }

        // parse the pass in list of readers to use
        List<String> readerNames = null;
        if (this.readers != null && !"none".equalsIgnoreCase(this.readers)) {
            if (this.readers.indexOf(",") == -1) {
                readerNames = new LinkedList<String>();
                readerNames.add(this.readers);
            } else {
                readerNames = StringUtil.split(this.readers, ",");
            }
        }
        String delegatorNameToUse = overrideDelegator != null ? overrideDelegator : delegatorName;
        String groupNameToUse = overrideGroup != null ? overrideGroup : entityGroupName;
        Delegator delegator = DelegatorFactory.getDelegator(delegatorNameToUse);
        Delegator baseDelegator = null;
        if (delegator == null) {
            throw new ContainerException("Invalid delegator name!");
        }

        if (delegator.getDelegatorTenantId() != null) {
            baseDelegator = DelegatorFactory.getDelegator(delegator.getDelegatorBaseName());
        } else {
            baseDelegator = delegator;
        }

        GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(groupNameToUse);
        if (helperInfo == null) {
            throw new ContainerException("Unable to locate the datasource helper for the group [" + groupNameToUse + "]");
        }

        // get the database util object
        DatabaseUtil dbUtil = new DatabaseUtil(helperInfo);
        Map<String, ModelEntity> modelEntities;
        try {
            modelEntities = delegator.getModelEntityMapByGroup(groupNameToUse);
        } catch (GenericEntityException e) {
            throw new ContainerException(e.getMessage(), e);
        }
        TreeSet<String> modelEntityNames = new TreeSet<String>(modelEntities.keySet());
        // store all components
        Collection<ComponentConfig> allComponents = ComponentConfig.getAllComponents();
        for (ComponentConfig config : allComponents) {
            //Debug.logInfo("- Stored component : " + config.getComponentName(), module);
            GenericValue componentEntry = baseDelegator.makeValue("Component");
            componentEntry.set("componentName", config.getComponentName());
            componentEntry.set("rootLocation", config.getRootLocation());
            try {
                GenericValue componentCheck = EntityQuery.use(baseDelegator).from("Component").where("componentName", config.getComponentName()).queryOne();
                if (UtilValidate.isEmpty(componentCheck)) {
                    componentEntry.create();
                } else {
                    componentEntry.store();
                }
            } catch (GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
            }
        }
        // load specify components
        List<String> loadComponents = new LinkedList<String>();
        if (UtilValidate.isNotEmpty(delegator.getDelegatorTenantId()) && EntityUtil.isMultiTenantEnabled()) {
            try {
                if (UtilValidate.isEmpty(this.component)) {
                    for (ComponentConfig config : allComponents) {
                        loadComponents.add(config.getComponentName());
                    }
                    List<GenericValue> tenantComponents = EntityQuery.use(baseDelegator).from("TenantComponent").where("tenantId", delegator.getDelegatorTenantId()).orderBy("sequenceNum").queryList();
                    for (GenericValue tenantComponent : tenantComponents) {
                        loadComponents.add(tenantComponent.getString("componentName"));
                    }
                    Debug.logInfo("Loaded components by tenantId : " + delegator.getDelegatorTenantId() + ", " + tenantComponents.size() + " components", module);
                } else {
                    List<GenericValue> tenantComponents = EntityQuery.use(baseDelegator).from("TenantComponent").where("tenantId", delegator.getDelegatorTenantId(), "componentName", this.component).orderBy("sequenceNum").queryList();
                    for (GenericValue tenantComponent : tenantComponents) {
                        loadComponents.add(tenantComponent.getString("componentName"));
                    }
                    Debug.logInfo("Loaded tenantId : " + delegator.getDelegatorTenantId() + " and component : " + this.component, module);
                }
                Debug.logInfo("Loaded : " + loadComponents.size() + " components", module);
            } catch (GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
            }
        }
        // check for drop index/fks
        if (dropConstraints) {
            List<String> messages = new LinkedList<String>();

            Debug.logImportant("Dropping foreign key indcies...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.deleteForeignKeyIndices(modelEntity, messages);
                }
            }

            Debug.logImportant("Dropping declared indices...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.deleteDeclaredIndices(modelEntity, messages);
                }
            }

            Debug.logImportant("Dropping foreign keys...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.deleteForeignKeys(modelEntity, modelEntities, messages);
                }
            }

            if (messages.size() > 0) {
                if (Debug.infoOn()) {
                    for (String message : messages) {
                        Debug.logInfo(message, module);
                    }
                }
            }
        }

        // drop pks
        if (dropPks) {
            List<String> messages = new LinkedList<String>();
            Debug.logImportant("Dropping primary keys...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.deletePrimaryKey(modelEntity, messages);
                }
            }

            if (messages.size() > 0) {
                if (Debug.infoOn()) {
                    for (String message : messages) {
                        Debug.logInfo(message, module);
                    }
                }
            }
        }

        // repair columns
        if (repairColumns) {
            List<String> fieldsToRepair = new LinkedList<String>();
            List<String> messages = new LinkedList<String>();
            dbUtil.checkDb(modelEntities, fieldsToRepair, messages, false, false, false, false);
            if (fieldsToRepair.size() > 0) {
                messages = new LinkedList<String>();
                dbUtil.repairColumnSizeChanges(modelEntities, fieldsToRepair, messages);
                if (messages.size() > 0) {
                    if (Debug.infoOn()) {
                        for (String message : messages) {
                            Debug.logInfo(message, module);
                        }
                    }
                }
            }
        }

        // get the reader name URLs first
        List<URL> urlList = null;
        if (UtilValidate.isNotEmpty(loadComponents)) {
            if (UtilValidate.isNotEmpty(readerNames)) {
                urlList = EntityDataLoader.getUrlByComponentList(helperInfo.getHelperBaseName(), loadComponents, readerNames);
            } else if (!"none".equalsIgnoreCase(this.readers)) {
                urlList = EntityDataLoader.getUrlByComponentList(helperInfo.getHelperBaseName(), loadComponents);
            }
        } else {
            if (UtilValidate.isNotEmpty(readerNames)) {
                urlList = EntityDataLoader.getUrlList(helperInfo.getHelperBaseName(), component, readerNames);
            } else if (!"none".equalsIgnoreCase(this.readers)) {
                urlList = EntityDataLoader.getUrlList(helperInfo.getHelperBaseName(), component);
            }
        }
        // need a list if it is empty
        if (urlList == null) {
            urlList = new LinkedList<URL>();
        }

        // add in the defined extra files
        for (String fileName: this.files) {
            URL fileUrl = UtilURL.fromResource(fileName);
            if (fileUrl != null) {
                urlList.add(fileUrl);
            }
        }

        // next check for a directory of files
        if (this.directory != null) {
            File dir = new File(this.directory);
            if (dir.exists() && dir.isDirectory() && dir.canRead()) {
                File[] fileArray = dir.listFiles();
                if (fileArray != null && fileArray.length > 0) {
                    for (File file: fileArray) {
                        if (file.getName().toLowerCase().endsWith(".xml")) {
                            try {
                                urlList.add(file.toURI().toURL());
                            } catch (MalformedURLException e) {
                                Debug.logError(e, "Unable to load file (" + file.getName() + "); not a valid URL.", module);
                            }
                        }
                    }
                }
            }
        }

        // process the list of files
        NumberFormat changedFormat = NumberFormat.getIntegerInstance();
        changedFormat.setMinimumIntegerDigits(5);
        changedFormat.setGroupingUsed(false);

        List<Object> errorMessages = new LinkedList<Object>();
        List<String> infoMessages = new LinkedList<String>();
        int totalRowsChanged = 0;
        if (UtilValidate.isNotEmpty(urlList)) {
            Debug.logImportant("=-=-=-=-=-=-= Doing a data load using delegator '" + delegator.getDelegatorName() + "' with the following files:", module);
            for (URL dataUrl: urlList) {
                Debug.logImportant(dataUrl.toExternalForm(), module);
            }

            Debug.logImportant("=-=-=-=-=-=-= Starting the data load...", module);

            for (URL dataUrl: urlList) {
                try {
                    int rowsChanged = EntityDataLoader.loadData(dataUrl, helperInfo.getHelperBaseName(), delegator, errorMessages, txTimeout, useDummyFks, maintainTxs, tryInserts);
                    totalRowsChanged += rowsChanged;
                    infoMessages.add(changedFormat.format(rowsChanged) + " of " + changedFormat.format(totalRowsChanged) + " from " + dataUrl.toExternalForm());
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error loading data file: " + dataUrl.toExternalForm(), module);
                }
            }
        } else {
            Debug.logImportant("=-=-=-=-=-=-= No data load files found.", module);
        }

        if (infoMessages.size() > 0) {
            Debug.logImportant("=-=-=-=-=-=-= Here is a summary of the data load:", module);
            for (String message: infoMessages) {
              Debug.logImportant(message, module);
            }
        }

        if (errorMessages.size() > 0) {
            Debug.logImportant("The following errors occurred in the data load:", module);
            for (Object message: errorMessages) {
              Debug.logImportant(message.toString(), module);
            }
        }

        Debug.logImportant("=-=-=-=-=-=-= Finished the data load with " + totalRowsChanged + " rows changed.", module);

        // create primary keys
        if (createPks) {
            List<String> messages = new LinkedList<String>();

            Debug.logImportant("Creating primary keys...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.createPrimaryKey(modelEntity, messages);
                }
            }
            if (messages.size() > 0) {
                if (Debug.infoOn()) {
                    for (String message : messages) {
                        Debug.logInfo(message, module);
                    }
                }
            }
        }

        // create constraints
        if (createConstraints) {
            List<String> messages = new LinkedList<String>();

            Debug.logImportant("Creating foreign keys...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.createForeignKeys(modelEntity, modelEntities, messages);
                }
            }

            Debug.logImportant("Creating foreign key indcies...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.createForeignKeyIndices(modelEntity, messages);
                }
            }

            Debug.logImportant("Creating declared indices...", module);
            for (String entityName : modelEntityNames) {
                ModelEntity modelEntity = modelEntities.get(entityName);
                if (modelEntity != null) {
                    dbUtil.createDeclaredIndices(modelEntity, messages);
                }
            }

            if (messages.size() > 0) {
                if (Debug.infoOn()) {
                    for (String message : messages) {
                        Debug.logInfo(message, module);
                    }
                }
            }
        }
    }
    /**
     * @see com.hanlin.fadp.base.container.Container#stop()
     */
    @Override
    public void stop() throws ContainerException {
    }

    @Override
    public String getName() {
        return name;
    }
}