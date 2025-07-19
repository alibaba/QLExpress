package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.ClassSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class ImportManager {
    
    private final ClassSupplier classSupplier;
    
    private final List<QLImport> importedPacks;
    
    private final Map<String, Class<?>> importedClses;
    
    public ImportManager(ClassSupplier classSupplier, List<QLImport> imports) {
        this.classSupplier = classSupplier;
        this.importedPacks = new ArrayList<>();
        this.importedClses = new HashMap<>();
        imports.forEach(this::addImport);
    }
    
    public ImportManager(ClassSupplier classSupplier, List<QLImport> importedPacks,
        Map<String, Class<?>> importedClses) {
        this.classSupplier = classSupplier;
        this.importedPacks = importedPacks;
        this.importedClses = importedClses;
    }
    
    public boolean addImport(QLImport anImport) {
        switch (anImport.getScope()) {
            case PACK:
            case InnerCls:
                importedPacks.add(anImport);
                return true;
            case CLS:
                Class<?> importCls = classSupplier.loadCls(anImport.getTarget());
                if (importCls == null) {
                    return false;
                }
                String[] split = anImport.getTarget().split("\\.");
                importedClses.put(split[split.length - 1], importCls);
                return true;
            default:
                return false;
        }
    }
    
    public Class<?> loadQualified(String qualifiedCls) {
        return classSupplier.loadCls(qualifiedCls);
    }
    
    public LoadPartQualifiedResult loadPartQualified(List<String> fieldIds) {
        Class<?> qualifiedCls = null;
        List<String> qualifiedPath = null;
        String innerClsId = null;
        final byte initState = 0;
        final byte continueState = 1;
        final byte loadClsState = 2;
        final byte loadInnerClsState = 3;
        final byte preLoadInnerClsState = 4;
        byte state = initState;
        nextField: for (int i = 0; i < fieldIds.size(); i++) {
            String fieldId = fieldIds.get(i);
            switch (state) {
                case initState:
                    // load from imported class
                    Class<?> aCls = importedClses.get(fieldId);
                    if (aCls != null) {
                        qualifiedCls = aCls;
                        state = preLoadInnerClsState;
                        continue;
                    }
                    // load from imported packs
                    if (!Character.isLowerCase(fieldId.charAt(0))) {
                        for (QLImport importedPack : importedPacks) {
                            switch (importedPack.getScope()) {
                                case PACK:
                                    Class<?> packCls = classSupplier.loadCls(importedPack.getTarget() + "." + fieldId);
                                    if (packCls != null) {
                                        qualifiedCls = packCls;
                                        state = preLoadInnerClsState;
                                        continue nextField;
                                    }
                                    break;
                                case InnerCls:
                                    Class<?> innerCls = classSupplier.loadCls(importedPack.getTarget() + "$" + fieldId);
                                    if (innerCls != null) {
                                        qualifiedCls = innerCls;
                                        state = preLoadInnerClsState;
                                        continue nextField;
                                    }
                                    break;
                            }
                        }
                        return new LoadPartQualifiedResult(null, 0);
                    }
                    state = continueState;
                    qualifiedPath = new ArrayList<>();
                    qualifiedPath.add(fieldId);
                    break;
                case preLoadInnerClsState:
                    if (!Character.isLowerCase(fieldId.charAt(0))) {
                        state = loadInnerClsState;
                        innerClsId = fieldId;
                    }
                    else {
                        return new LoadPartQualifiedResult(qualifiedCls, i);
                    }
                    break;
                case continueState:
                    qualifiedPath.add(fieldId);
                    if (!Character.isLowerCase(fieldId.charAt(0))) {
                        state = loadClsState;
                    }
                    break;
                case loadClsState:
                    qualifiedCls = classSupplier.loadCls(String.join(".", qualifiedPath));
                    if (qualifiedCls == null) {
                        return new LoadPartQualifiedResult(null, 0);
                    }
                    if (!Character.isLowerCase(fieldId.charAt(0))) {
                        qualifiedPath = null;
                        innerClsId = fieldId;
                        state = loadInnerClsState;
                    }
                    else {
                        return new LoadPartQualifiedResult(qualifiedCls, i);
                    }
                    break;
                case loadInnerClsState:
                    Class<?> innerCls = classSupplier.loadCls(qualifiedCls.getName() + "$" + innerClsId);
                    if (innerCls == null) {
                        return new LoadPartQualifiedResult(qualifiedCls, i - 1);
                    }
                    if (!Character.isLowerCase(fieldId.charAt(0))) {
                        qualifiedCls = innerCls;
                        innerClsId = fieldId;
                    }
                    else {
                        return new LoadPartQualifiedResult(innerCls, i);
                    }
                    break;
            }
        }
        
        switch (state) {
            case continueState:
                return new LoadPartQualifiedResult(null, 0);
            case loadClsState:
                qualifiedCls = classSupplier.loadCls(String.join(".", qualifiedPath));
                return qualifiedCls == null ? new LoadPartQualifiedResult(null, fieldIds.size())
                    : new LoadPartQualifiedResult(qualifiedCls, fieldIds.size());
            case preLoadInnerClsState:
                return new LoadPartQualifiedResult(qualifiedCls, fieldIds.size());
            case loadInnerClsState:
                Class<?> innerCls = classSupplier.loadCls(qualifiedCls.getName() + "$" + innerClsId);
                return innerCls == null ? new LoadPartQualifiedResult(qualifiedCls, fieldIds.size() - 1)
                    : new LoadPartQualifiedResult(innerCls, fieldIds.size());
            default:
                return new LoadPartQualifiedResult(null, 0);
        }
    }
    
    public static class LoadPartQualifiedResult {
        private final Class<?> cls;
        
        /**
         * first no class path field index
         */
        private final int restIndex;
        
        public LoadPartQualifiedResult(Class<?> cls, int restIndex) {
            this.cls = cls;
            this.restIndex = restIndex;
        }
        
        public Class<?> getCls() {
            return cls;
        }
        
        public int getRestIndex() {
            return restIndex;
        }
    }
    
    enum ImportScope {
        // import java.lang.*;
        PACK,
        // import a.b.Cls.*
        InnerCls,
        // import java.lang.String;
        CLS
    }
    
    public static QLImport importInnerCls(String clsPath) {
        return new QLImport(ImportScope.InnerCls, clsPath);
    }
    
    public static QLImport importPack(String packPath) {
        return new QLImport(ImportScope.PACK, packPath);
    }
    
    public static QLImport importCls(String clsPath) {
        return new QLImport(ImportScope.CLS, clsPath);
    }
    
    public static class QLImport {
        private final ImportScope scope;
        
        private final String target;
        
        public QLImport(ImportScope scope, String target) {
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
