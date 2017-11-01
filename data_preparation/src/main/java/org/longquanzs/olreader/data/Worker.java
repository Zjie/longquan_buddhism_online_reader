package org.longquanzs.olreader.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.longquanzs.olreader.data.model.Scripture;
import org.longquanzs.olreader.db.service.DBOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class Worker {
	private static final Logger logger = LoggerFactory.getLogger(Worker.class);
	private String baseDir = "C:\\Users\\zhoujie\\Downloads\\longquan";
	private String fileName = baseDir + "\\dzz.csv";
	private String targetDir = baseDir + "\\etl_result\\";
	private String originScrptureDir = baseDir + "\\dzz\\";
	
	@Autowired
	private DBOperation dbop;
	@Autowired
	private MongoTemplate template;
	
	public int startJob() {
		try {
			File fileDir = new File(fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
			Set<String> allWork = new HashSet<String>();
			Set<String> allDynasty = new HashSet<String>();
			String str;
			String lastWork = "";
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetDir + "no_file.txt"), "UTF8"));
			dbop.cleanupDB();
			while ((str = in.readLine()) != null) {
				Scripture s = parseLine(str, lastWork);
				lastWork = s.getWork();
				allWork.add(s.getWork());
				allDynasty.add(s.getDynasty());
				template.insert(s);
				try {
					if (s.getFileName() != null) {
						File file = new File(originScrptureDir + s.getFileName() + ".txt");
						if (!file.exists()) {
							out.write(s.getFileName() + "\n");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			in.close();
			out.close();
			allWork.forEach(new Consumer<String>() {
				@Override
				public void accept(String arg0) {
					logger.debug(arg0);
				}});
			allDynasty.forEach(new Consumer<String>() {
				@Override
				public void accept(String t) {
					logger.debug(t);
				}});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private Scripture parseLine(String line, String lastWork) {
		String[] params = line.split(",");
		Scripture s = new Scripture();
		//部
		if (params[0].equals("")) {
			s.setWork(lastWork);
		} else {
			s.setWork(params[0]);
		}
		//经号
		s.setSno(params[1]);
		//册号
		s.setBookNo(params[2]);
		//页码
		s.setPage(params[3]);
		//朝代
		s.setDynasty(params[5]);
		//译者
		if (params[7].equals("")) {
			s.setTranslator(params[6]);
		} else {
			s.setTranslator(params[6] + "-" + params[7]);
		}
		//文件名
		if (params.length >= 9) {
			s.setFileName(params[8].trim());
		}
		//卷
		Matcher m = Pattern.compile("\\(\\d+卷\\)").matcher(params[4]);
		if (m.find()) {
			String roll = m.group();
			int rollNum = Integer.parseInt(roll.substring(1, roll.length() - 2));
			s.setName(params[4].substring(0, m.start()));
			s.setRollBegin(rollNum);
			s.setRollBegin(1);
			s.setRollEnd(rollNum + 1);
		} else {
			m = Pattern.compile("\\(\\d+-\\d+卷\\)").matcher(params[4]);
			if (m.find()) {
				String roll = m.group();
				int rollBegin = Integer.parseInt(roll.substring(1, roll.indexOf('-')));
				int rollEnd = Integer.parseInt(roll.substring(roll.indexOf('-') + 1, roll.length() - 2));
				s.setName(params[4].substring(0, m.start()));
				s.setRollBegin(rollBegin);
				s.setRollEnd(rollEnd);
				s.setRollNum(rollEnd - rollBegin + 1);
			} else {
				s.setName(params[4]);
				s.setRollNum(1);
				s.setRollBegin(1);
				s.setRollEnd(1);
			}
		}
		return s;
	}
	
}
