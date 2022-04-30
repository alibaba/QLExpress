package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.ClassSupplier;

import java.util.*;

/**
 * Author: DQinYuan
 */
public class ImportManager {

    private final ImportManager parent;

    private final List<Import> importedPacks;

    private final Map<String, Import> importedClses;

    private ImportManager(ImportManager parent, List<Import> importedPacks, Map<String, Import> importedClses) {
        this.parent = parent;
        this.importedPacks = importedPacks;
        this.importedClses = importedClses;
    }

    public void addImport(Import anImport) {
        if (ImportScope.PACK.equals(anImport.getScope())) {
            importedPacks.add(anImport);
        } else if (ImportScope.CLS.equals(anImport.getScope())) {
            String[] split = anImport.getTarget().split("\\.");
            importedClses.put(split[split.length-1], anImport);
        }
    }

    public Class<?> loadFromImport(String clsSimpleName, ClassSupplier classSupplier) {
        Import clsImport = importedClses.get(clsSimpleName);
        if (clsImport != null) {
            return classSupplier.loadCls(clsImport.getTarget());
        }

        for (Import importedPack : importedPacks) {
            Class<?> loadRes = classSupplier.loadCls(importedPack.getTarget() + "." + clsSimpleName);
            if (loadRes != null) {
                return loadRes;
            }
        }
        if (parent != null) {
            return parent.loadFromImport(clsSimpleName, classSupplier);
        }
        return null;
    }

    public ImportManager scriptImportManager() {
        return new ImportManager(this, new ArrayList<>(), new HashMap<>());
    }

    public static ImportManager buildGlobalImportManager(List<Import> imports) {
        ImportManager importManager = new ImportManager(null, new ArrayList<>(), new HashMap<>());
        for (Import anImport : imports) {
            importManager.addImport(anImport);
        }
        return importManager;
    }

    public static Import importPack(String pack) {
        return new Import(ImportScope.PACK, pack);
    }

    public static Import importCls(String cls) {
        return new Import(ImportScope.CLS, cls);
    }

    enum ImportScope {
        // import java.lang.*;
        PACK,
        // import java.lang.String;
        CLS
    }

    public static class Import {
        private final ImportScope scope;
        private final String target;

        public Import(ImportScope scope, String target) {
            this.scope = scope;
            this.target = target;
        }

        public ImportScope getScope() {
            return scope;
        }

        public String getTarget() {
            return target;
        }
    }

}
