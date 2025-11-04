package research.paper4.textmatch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import research.paper4.textmatch.impl.service.ExcelService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class TextmatchApplicationTests {

	private ExcelService excelService;
	@Autowired
	void setExcelService(ExcelService excelService){
		this.excelService = excelService;
	}

	@Test
	void contextLoads() {
	}

	@Test
	void convertWithStream() {

		Map<String, String> map = new LinkedHashMap<>();
		map.put("TITLE", "titleText");
		map.put("DESC","descText");
		map.put("RESP", "respText");

		String vals =  map.keySet().stream()
				.map(key -> key + ":" + map.get(key))
				.collect(Collectors.joining(", ", "{", "}"));
		log.info("{}", vals);
	}

	//@Test
	void createResultExcel(){

		Map<String, String> e1 = new LinkedHashMap<>();
		e1.put("Match action", "Job-To-Res");
		e1.put("ResumeId","1234");
		e1.put("Resume title","res title");
		e1.put("JobPostId", "768");
		e1.put("JobPost Title", "job post title");
		e1.put("Gemma overall", "0.98");
		e1.put("Nomic overall", "0.96");
		e1.put("Gemma title Score", "0.76");
		e1.put("Nomic title Score", "0.98");

		Map<String, String> e2 = new LinkedHashMap<>();
		e2.put("Match action", "Job-To-Res");
		e2.put("ResumeId","6789");
		e2.put("Resume title","res title2");
		e2.put("JobPostId", "768");
		e2.put("JobPost Title", "job post title");
		e2.put("Gemma overall", "0.48");
		e2.put("Nomic overall", "0.66");
		e2.put("Gemma title Score", "0.46");
		e2.put("Nomic title Score", "0.88");

		Map<String, String> e3 = new LinkedHashMap<>();
		e3.put("Match action", "Job-To-Res");
		e3.put("ResumeId","1111");
		e3.put("Resume title","res title3");
		e3.put("JobPostId", "768");
		e3.put("JobPost Title", "job post title");
		e3.put("Gemma overall", "0.68");
		e3.put("Nomic overall", "0.86");
		e3.put("Gemma title Score", "0.16");
		e3.put("Nomic title Score", "0.18");


		List<Map<String, String>> list = List.of(e1, e2, e3);

		excelService.createResultsExcel("results.xlsx", list);
	}

}
