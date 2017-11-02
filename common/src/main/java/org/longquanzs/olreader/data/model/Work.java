package org.longquanzs.olreader.data.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 部
 * @author zhoujie.jchou@gmail.com
 *
 */
@Document
public class Work {
	@Id
	private String id;
	//一个部包含多个经
	private List<String> scriptureIds;
	
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getScriptureIds() {
		return scriptureIds;
	}

	public void setScriptureIds(List<String> scriptureIds) {
		this.scriptureIds = scriptureIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
