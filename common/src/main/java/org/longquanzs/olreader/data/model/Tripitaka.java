package org.longquanzs.olreader.data.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 大藏经
 * @author zhoujie.jchou@gmail.com
 *
 */
@Document
public class Tripitaka {
	@Id
	private String id;
	//一套大藏经包含多个部
	private List<String> workIds;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getWorkIds() {
		return workIds;
	}
	public void setWorkIds(List<String> workIds) {
		this.workIds = workIds;
	}
	
}
