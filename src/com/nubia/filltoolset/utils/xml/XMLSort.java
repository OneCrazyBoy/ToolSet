package com.nubia.filltoolset.utils.xml;

import java.util.List;

public class XMLSort {
	
	private String sortId;
	private List<XMLFileBean> XMLFileBeans;
	public XMLSort(){};
	public XMLSort(String sortId,List<XMLFileBean> XMLFileBeans ){
		this.sortId = sortId;
		this.XMLFileBeans = XMLFileBeans;
	};
    public String getSortId() {  
        return sortId;  
    }  
    public void setSortId(String sortId) {  
        this.sortId = sortId;  
    }  
    public List<XMLFileBean> getXMLFileBeans() {  
        return XMLFileBeans;  
    }  
    public void setElements(List<XMLFileBean> XMLFileBeans) {  
        this.XMLFileBeans = XMLFileBeans;  
    }  
}
