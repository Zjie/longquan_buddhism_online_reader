package org.longquanzs.olreader.db.service;

import org.longquanzs.olreader.data.model.Roll;
import org.longquanzs.olreader.data.model.Scripture;
import org.longquanzs.olreader.data.model.Tripitaka;
import org.longquanzs.olreader.data.model.Work;
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
		template.remove(new Query(), Roll.class);
		template.remove(new Query(), Work.class);
		template.remove(new Query(), Tripitaka.class);
	}
}
