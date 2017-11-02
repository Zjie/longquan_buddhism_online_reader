package org.longquanzs.olreader.data.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Scripture {
	@Id
	private String id;
	
	//部
	private String work;
	//经号
	private String sno;
	//经文名
	private String name;
	//卷数
	private int rollNum;
	private int rollBegin;
	private int rollEnd;
	//一部经所包含的所有卷
	private List<String> rollIds;
	//册号
	private String bookNo;
	//页码
	private String page;
	//朝代
	private String dynasty;
	//译者
	private String translator;
	//文件名
	private String fileName;
	//序
	private String preface;
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRollNum() {
		return rollNum;
	}
	public void setRollNum(int rollNum) {
		this.rollNum = rollNum;
	}
	public int getRollBegin() {
		return rollBegin;
	}
	public void setRollBegin(int rollBegin) {
		this.rollBegin = rollBegin;
	}
	public int getRollEnd() {
		return rollEnd;
	}
	public void setRollEnd(int rollEnd) {
		this.rollEnd = rollEnd;
	}
	public String getBookNo() {
		return bookNo;
	}
	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getDynasty() {
		return dynasty;
	}
	public void setDynasty(String dynasty) {
		this.dynasty = dynasty;
	}
	public String getTranslator() {
		return translator;
	}
	public void setTranslator(String translator) {
		this.translator = translator;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getRollIds() {
		return rollIds;
	}
	public void setRollIds(List<String> rollIds) {
		this.rollIds = rollIds;
	}
	public String getPreface() {
		return preface;
	}
	public void setPreface(String preface) {
		this.preface = preface;
	}
}
