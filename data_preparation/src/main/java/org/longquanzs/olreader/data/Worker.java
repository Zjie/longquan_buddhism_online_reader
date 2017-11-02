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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.longquanzs.olreader.data.model.Roll;
import org.longquanzs.olreader.data.model.Scripture;
import org.longquanzs.olreader.data.model.Tripitaka;
import org.longquanzs.olreader.data.model.Work;
import org.longquanzs.olreader.db.service.DBOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class Worker {
	private static final Logger logger = LoggerFactory.getLogger(Worker.class);
	private String baseDir = "C:\\Users\\zj125135\\Desktop\\longquan";
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
			String str;
			String lastWork = "";
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetDir + "no_file.txt"), "UTF8"));
			dbop.cleanupDB();
			Tripitaka tripitaka = new Tripitaka();
			List<Work> works = new ArrayList<Work>();
			Work work = null;
			while ((str = in.readLine()) != null) {
				Scripture s = parseLine(str, lastWork);
				try {
					if (s.getFileName() != null) {
						File file = new File(originScrptureDir + s.getFileName() + ".txt");
						if (!file.exists()) {
							out.write(s.getFileName() + "\n");
						} else {
							List<Roll> rolls = splitRoll(s);
							List<String> rollIds = new ArrayList<String>(rolls.size());
							for (Roll r : rolls) {
								template.insert(r);
								rollIds.add(r.getId());
								//logger.debug(r.toString());
							}
							s.setRollIds(rollIds);
							if (s.getWork().equals("")) {
								s.setWork(lastWork);
								template.insert(s);
								work.getScriptureIds().add(s.getId());
							} else {
								work = new Work();
								work.setName(s.getWork());
								work.setScriptureIds(new ArrayList<String>());
								template.insert(s);
								work.getScriptureIds().add(s.getId());
								works.add(work);
							}
							
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			List<String> workIds = new ArrayList<String>();
			for (Work w : works) {
				template.insert(w);
				workIds.add(w.getId());
			}
			tripitaka.setWorkIds(workIds);
			template.insert(tripitaka);
			in.close();
			out.close();
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
		s.setWork(params[0]);
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
			s.setRollBegin(1);
			s.setRollEnd(rollNum);
			s.setRollNum(rollNum);
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
	
	
	private List<Roll> splitRoll(Scripture s) {
		if (s.getRollNum() == 1) {
			return doSplitSingleRoll(s);
		} else {
			return doSplitMultiRoll(s);
		}
	}
	
	/**
	 * 拆分经文成多卷
	 * @param s
	 * @return
	 */
	private List<Roll> doSplitSingleRoll(Scripture s) {
		List<Roll> result = new ArrayList<Roll>(s.getRollNum());
		try {
			Roll roll = new Roll();
			roll.setNo(1);
			StringBuilder text = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(originScrptureDir + s.getFileName() + ".txt"), "UTF8"));
			String str;
			while ((str = in.readLine()) != null) {
				text.append(str).append("\n");
			}
			roll.setText(text.toString());
			result.add(roll);
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static int ROLL_STATUS_BEGIN = 0;
	private static int ROLL_STATUS_PREFACE = 1;
	private static int ROLL_STATUS_TEXTBEGIN = 2;
	private static int ROLL_STATUS_TEXTEND = 3;
	
	private List<Roll> doSplitMultiRoll(Scripture s) {
		List<Roll> result = new ArrayList<Roll>(s.getRollNum());
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(originScrptureDir + s.getFileName() + ".txt"), "UTF8"));
			String str;
			Pattern rollP = Pattern.compile(s.getName() + "卷(第[一二三四五六七八九十百]+|上|中|下)");
			StringBuilder text = new StringBuilder();
			int status = ROLL_STATUS_BEGIN;
			String lastRollNo = null;
			int unmatchLineCount = 0;
			while ((str = in.readLine()) != null) {
				if (str.trim().equals("")) {
					continue;
				}
				if (str.startsWith("大正新修大藏")) {
					continue;
				}
				Matcher m = rollP.matcher(str);
				if (status == ROLL_STATUS_BEGIN) {
					if (str.endsWith(s.getName() + "序")) {
						status = ROLL_STATUS_PREFACE;
					} else if (m.find()) {
						status = ROLL_STATUS_TEXTBEGIN;
						text.delete(0, text.length());
						lastRollNo = str;
					} else {
						unmatchLineCount++;
						if (unmatchLineCount > 100) {
							logger.warn("failed to parse Roll for " + s.getName() + "-" + s.getFileName());
							break;
						}
						//logger.debug(s.getFileName() + ":" + str);
					}
				} else if (status == ROLL_STATUS_PREFACE) {
					if (m.find()) {
						status = ROLL_STATUS_TEXTBEGIN;
						s.setPreface(text.toString());
						//logger.debug(text.toString());
						text.delete(0, text.length());
						lastRollNo = str;
					} else {
						text.append(str).append("\n");
					}
				} else if (status == ROLL_STATUS_TEXTBEGIN) {
					if (str.equals(lastRollNo)) {
						//每一段卷前后都会有相同的行
						Roll roll = new Roll();
						roll.setNo(parseRollNo(lastRollNo, s.getRollNum(), rollP));
						roll.setText(text.toString());
						roll.setName(lastRollNo.trim());
						result.add(roll);
						text.delete(0, text.length());
						status = ROLL_STATUS_TEXTEND;
					} else if (m.find()) {
						//如果前后没有相同的行，则看是否新起了一个卷
						//保存上一个卷
						Roll roll = new Roll();
						roll.setNo(parseRollNo(lastRollNo, s.getRollNum(), rollP));
						roll.setText(text.toString());
						roll.setName(lastRollNo);
						result.add(roll);
						text.delete(0, text.length());
						lastRollNo = str;
					} else {
						text.append(str).append("\n");
					}
				} else if (status == ROLL_STATUS_TEXTEND) {
					if (m.find()) {
						status = ROLL_STATUS_TEXTBEGIN;
						lastRollNo = str;
						text.delete(0, text.length());
					} else {
						//logger.debug(s.getFileName() + ":" + str);
					}
				} else {
					//logger.debug(s.getFileName() + ":" + str);
				}
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static final Map<String, Integer> NUM_MAP = new HashMap<String, Integer>() {
		private static final long serialVersionUID = -8807498701865576036L;
		{
			put("一", 1);
			put("二", 2);
			put("三", 3);
			put("四", 4);
			put("五", 5);
			put("六", 6);
			put("七", 7);
			put("八", 8);
			put("九", 9);
		}
	};
	
	private static int parseRollNo(String str, int rollNum, Pattern rollP) {
		Matcher m = rollP.matcher(str);
		if (!m.find()) {
			return -1;
		}
		String noStr = m.group(1);
		try {
			if (noStr.equals("上")) {
				return 1;
			} else if (noStr.equals("中")) {
				return 2;
			} else if (noStr.equals("下")) {
				return rollNum == 3 ? 3 : 2;
			} else {
				String num = noStr.substring(1, noStr.length());
				int result = 0;
				int hundreds = num.indexOf('百');
				int tens = num.indexOf('十');
				if (hundreds > 0) {
					//有百位
					result += NUM_MAP.get(num.charAt(0) + "") * 100;
					if (tens > hundreds) {
						//有十位
						result += NUM_MAP.get(num.charAt(hundreds + 1) + "") * 10;
						if (tens < num.length() - 1) {
							//有个位
							result += NUM_MAP.get(num.charAt(num.length() - 1) + "");
						}
					} else {
						//无十位
						if (hundreds < num.length() - 1) {
							//有个位
							result += NUM_MAP.get(num.charAt(num.length() - 1) + "");
						}
					}
				} else {
					//无百位
					if (tens == 0) {
						//有十位
						result += 10;
						if (tens < num.length() - 1) {
							//有个位
							result += NUM_MAP.get(num.charAt(num.length() - 1) + "");
						}
					} else if (tens > 0) {
						//有十位
						result += NUM_MAP.get(num.charAt(0) + "") * 10;
						if (tens < num.length() - 1) {
							//有个位
							result += NUM_MAP.get(num.charAt(num.length() - 1) + "");
						}
					} else {
						//无十位，直接加上个位
						result += NUM_MAP.get(num.charAt(num.length() - 1) + "");
					}
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(str);
		}
		return -1;
	}
	
	public static void main(String[] agrs) {
		String line = "大般若波罗蜜多经卷第九";
		Pattern rollP = Pattern.compile("大般若波罗蜜多经卷(第[一二三四五六七八九十百]+|上|中|下)");
		System.out.println(parseRollNo(line, 30, rollP));
	}
}
