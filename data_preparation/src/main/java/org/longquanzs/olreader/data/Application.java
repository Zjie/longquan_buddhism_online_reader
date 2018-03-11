package org.longquanzs.olreader.data;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages="org.longquanzs.olreader")
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	@Value("${indexPath}")
	private String indexPath;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = 
		          new AnnotationConfigApplicationContext(Application.class);
		//IndexWorker w = context.getBean(IndexWorker.class);
		//w.startJob();
		IndexSearcher searcher = context.getBean(IndexSearcher.class);
		try {
			QueryParser qp = new QueryParser("roll_text", new SmartChineseAnalyzer());
	        Query firstNameQuery = qp.parse("标题");
	        TopDocs hits = searcher.search(firstNameQuery, 10);
	        for (ScoreDoc sd : hits.scoreDocs) {
	        	Document d = searcher.doc(sd.doc);
	        	logger.info(d.get("roll_text"));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Bean
	public IndexWriter createWriter() throws IOException
	{
	    FSDirectory dir = FSDirectory.open(Paths.get(indexPath));
	    IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer());
	    IndexWriter writer = new IndexWriter(dir, config);
	    return writer;
	}
	@Bean
	public IndexSearcher createSearcher() throws IOException
	{
	    Directory dir = FSDirectory.open(Paths.get(indexPath));
	    IndexReader reader = DirectoryReader.open(dir);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    return searcher;
	}
}
