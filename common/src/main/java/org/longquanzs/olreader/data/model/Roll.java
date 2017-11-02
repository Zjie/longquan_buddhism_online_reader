package org.longquanzs.olreader.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 卷
 * @author zj125135
 *
 */
@Document
public class Roll {
	@Id
	private String id;
	//第几卷
	//格式有两种：1.xxx卷第x  2.xxx卷(上/中/下)
	private int no;
	//内容
	private String text;
	//卷名
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return id + ":" + no + "-" + name;
	}
}
