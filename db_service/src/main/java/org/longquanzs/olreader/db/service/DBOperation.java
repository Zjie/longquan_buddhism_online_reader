package org.longquanzs.olreader.db.service;

import org.longquanzs.olreader.data.model.Scripture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class DBOperation {
	@Autowired
	private MongoTemplate template;
	
	public void cleanupDB() {
		template.remove(new Query(), Scripture.class);
	}
}
