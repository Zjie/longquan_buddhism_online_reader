package org.longquanzs.olreader.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class MongoConfig extends AbstractMongoConfiguration {

	@Autowired
	private Environment env;
	
	@Override
	protected String getDatabaseName() {
		return "testdb";
	}

	@Override
	protected String getMappingBasePackage() {
		return "org.longquanzs.olreader.db";
	}


	@Override
	public MongoClient mongoClient() {
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();
		seeds.add(new ServerAddress(env.getProperty("ip"), Integer.parseInt(env.getProperty("port"))));
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		//credentials.add(MongoCredential.createMongoCRCredential(env.getProperty("username"), 
		//		env.getProperty("db"), env.getProperty("pwd").toCharArray()));
		MongoClient mongo = new MongoClient(seeds, credentials);
		return mongo;
	}
}