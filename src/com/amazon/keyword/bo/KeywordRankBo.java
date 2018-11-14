package com.amazon.keyword.bo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.amazon.common.entity.Keyword;
import com.amazon.common.entity.KeywordRank;
import com.amazon.common.util.LoadDataUtil;
import com.amazon.keyword.dao.IKeywordDao;
import com.amazon.keyword.dao.IKeywordRankDao;
import com.csvreader.CsvReader;

@Service("keywordRankBo")
public class KeywordRankBo implements IKeywordRankBo{
	@Resource
	private IKeywordDao keywordDao;
	@Resource
	private IKeywordRankDao keywordRankDao;

	@Override
	public int txInsertBatchRank() {
		String rootName = "F:\\itmsw_work\\amazon";
		File dataDir = new File(rootName + "\\data\\rank");
		
		CsvReader reader = null;
		ArrayList<String[]> csvList = null;
		String[] itemArr = null;
		
		Keyword keyword = new Keyword();
        Keyword existKeyword = new Keyword();
		KeywordRank keywordRank = new KeywordRank();
		Integer rankNum = null;
		Integer keywordId = null;
		
		File[] files = dataDir.listFiles();
        for (File file : files) {
			if(file.isDirectory()) continue;
			if(!LoadDataUtil.canFileUse(file.getName())) continue;
			
	        try {
	        	csvList = new ArrayList<String[]>(); 
	        	reader = new CsvReader(file.getAbsolutePath(), ',', Charset.forName("GBK"));
	        	
				while(reader.readRecord()){
				    csvList.add(reader.getValues()); //按行读取，并把每一行的数据添加到list集合
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        reader.close();
	        
	        if("error".equals(LoadDataUtil.changeFileName(file, "OK_" + file.getName()))) continue;
	        
	        for(int row = 0;row < csvList.size();row++){
	        	itemArr = csvList.get(row);
	        	
	        	keyword = new Keyword();
	        	keyword.setName(itemArr[2]);
	        	existKeyword = keywordDao.selectKeyword(keyword);
	        	
	        	if("".equals(itemArr[5])) continue;
	        	if(existKeyword == null) continue;
	        	keywordId = existKeyword.getId();
	        	
        		rankNum = Integer.parseInt(itemArr[5]);
        		
        		keywordRank.setKeywordId(keywordId);
        		keywordRank.setKeywordRank(rankNum);
        		keywordRank.setCreateTime(LoadDataUtil.getFormatDate(itemArr[4]).getTime());
        		
        		keywordRankDao.insertSelective(keywordRank);
//        		System.out.println(itemArr[2] + "--" + rankNum + "--" + LoadDataUtil.getFormatDate(itemArr[4]));
	        	
//	        	System.out.println("-----------------");
	        }
	        
        }
		return 0;
	}
	
	
}
