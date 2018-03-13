package com.stereo.via.scan;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuj-ai on 2018/3/13.
 */
public class ScanStaticModel {

    private Reflections reflections;
    private Set<Class<?>> disconfFileClassSet;
    private Set<Method> disconfFileItemMethodSet;
    private Map<Class<?>, Set<Method>> disconfFileItemMap;
    private Set<Method> disconfItemMethodSet;
    private Set<Class<?>> disconfActiveBackupServiceClassSet;
    private Set<Class<?>> disconfUpdateService;
    private Class<IDisconfUpdatePipeline> iDisconfUpdatePipeline = null;
    private Set<String> justHostFiles;
    private Set<String> reloadableFiles;

    public ScanStaticModel() {
    }

    public Reflections getReflections() {
        return this.reflections;
    }

    public void setReflections(Reflections reflections) {
        this.reflections = reflections;
    }

    public Set<Class<?>> getDisconfFileClassSet() {
        return this.disconfFileClassSet;
    }

    public void setDisconfFileClassSet(Set<Class<?>> disconfFileClassSet) {
        this.disconfFileClassSet = disconfFileClassSet;
    }

    public Map<Class<?>, Set<Method>> getDisconfFileItemMap() {
        return this.disconfFileItemMap;
    }

    public void setDisconfFileItemMap(Map<Class<?>, Set<Method>> disconfFileItemMap) {
        this.disconfFileItemMap = disconfFileItemMap;
    }

    public Set<Method> getDisconfItemMethodSet() {
        return this.disconfItemMethodSet;
    }

    public void setDisconfItemMethodSet(Set<Method> disconfItemMethodSet) {
        this.disconfItemMethodSet = disconfItemMethodSet;
    }

    public Set<Method> getDisconfFileItemMethodSet() {
        return this.disconfFileItemMethodSet;
    }

    public void setDisconfFileItemMethodSet(Set<Method> disconfFileItemMethodSet) {
        this.disconfFileItemMethodSet = disconfFileItemMethodSet;
    }

    public Set<Class<?>> getDisconfActiveBackupServiceClassSet() {
        return this.disconfActiveBackupServiceClassSet;
    }

    public void setDisconfActiveBackupServiceClassSet(Set<Class<?>> disconfActiveBackupServiceClassSet) {
        this.disconfActiveBackupServiceClassSet = disconfActiveBackupServiceClassSet;
    }

    public Set<Class<?>> getDisconfUpdateService() {
        return this.disconfUpdateService;
    }

    public void setDisconfUpdateService(Set<Class<?>> disconfUpdateService) {
        this.disconfUpdateService = disconfUpdateService;
    }

    public Set<String> getReloadableFiles() {
        return this.reloadableFiles;
    }

    public void setReloadableFiles(Set<String> reloadableFiles) {
        this.reloadableFiles = reloadableFiles;
    }

    public Set<String> getJustHostFiles() {
        return this.justHostFiles;
    }

    public void setJustHostFiles(Set<String> justHostFiles) {
        this.justHostFiles = justHostFiles;
    }

    public Class<IDisconfUpdatePipeline> getiDisconfUpdatePipeline() {
        return this.iDisconfUpdatePipeline;
    }

    public void setiDisconfUpdatePipeline(Class<IDisconfUpdatePipeline> iDisconfUpdatePipeline) {
        this.iDisconfUpdatePipeline = iDisconfUpdatePipeline;
    }

    public String toString() {
        return "ScanStaticModel{reflections=" + this.reflections + ", disconfFileClassSet=" + this.disconfFileClassSet + ", disconfFileItemMethodSet=" + this.disconfFileItemMethodSet + ", disconfFileItemMap=" + this.disconfFileItemMap + ", disconfItemMethodSet=" + this.disconfItemMethodSet + ", iDisconfUpdatePipeline=" + this.iDisconfUpdatePipeline + ", disconfActiveBackupServiceClassSet=" + this.disconfActiveBackupServiceClassSet + ", disconfUpdateService=" + this.disconfUpdateService + ", justHostFiles=" + this.justHostFiles + ", reloadableFiles=" + this.reloadableFiles + '}';
    }
}
