package com.stereo.study.buildtablesql2code.model;

import java.util.List;

public class ClassInfo {

    private String tableName;
    private String className;
	private String classComment;

	private List<FieldInfo> fieldList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassComment() {
		return classComment;
	}

	public void setClassComment(String classComment) {
		this.classComment = classComment;
	}

	public List<FieldInfo> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<FieldInfo> fieldList) {
		this.fieldList = fieldList;
	}

	@Override
	public String toString() {
		return "ClassInfo{" +
				"tableName='" + tableName + '\'' +
				", className='" + className + '\'' +
				", classComment='" + classComment + '\'' +
				", fieldList=" + fieldList +
				'}';
	}
}