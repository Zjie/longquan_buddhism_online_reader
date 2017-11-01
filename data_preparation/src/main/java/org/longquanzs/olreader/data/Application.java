package org.longquanzs.olreader.data;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages="org.longquanzs.olreader")
public class Application {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = 
		          new AnnotationConfigApplicationContext(Application.class);
		Worker w = context.getBean(Worker.class);
		w.startJob();
	}
}
