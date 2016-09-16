package com.nubia.filltoolset.utils.xml;

public class XMLFileBean {
	
	private String fileSort;
	private String fileName;
	private String fileType;

	public XMLFileBean(){}
	
	public XMLFileBean(String fileSort, String fileName) {
		super();
		this.fileSort = fileSort;
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSort() {
		return fileSort;
	}
	public void setFileSort(String fileSort) {
		this.fileSort = fileSort;
	}
	
	@Override
	public String toString() {
		return "XMLSceneBean [fileName=" + fileName + ", fileSort=" + fileSort + "]";
	}
}
