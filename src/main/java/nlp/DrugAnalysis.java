package nlp;

import com.gbdata.common.json.JSONObject;
import com.gbdata.common.json.JSONValue;
import com.gbdata.common.mongo.MongoClient;
import com.gbdata.common.mongo.MongoEntityClient;
import com.gbdata.common.util.FileUtil;
import com.gbdata.common.util.StringUtil;
import com.gbdata.dbtools.export.JSONExporter;
import com.gbdata.dbtools.util.ExcelReader;
import com.gbdata.dict.client.SourceProductDictClient;
import com.gbdata.dict.client.exception.ServiceException;
import com.gbdata.dict.prodcn.SourceProductDict;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lisa.wei on 2019/7/3.
 */
public class DrugAnalysis {
    private static MongoEntityClient mongoEntityClientCT = new MongoEntityClient("192.168.0.105:27001", "", "", "clinicaltrial");
    private static MongoEntityClient mongoEntityClientDrug = new MongoEntityClient("192.168.0.41:27001", "", "", "core");
    private static Map<String, JSONObject> drugMap = new HashMap<>();
    private static Map<String, String> drug_type = new HashMap<>();
    SourceProductDictClient drugClient = new SourceProductDictClient();
    JSONExporter oneDrugExporter = JSONExporter.excelExporter(null, "标题中只含有一个药物名称", "D:\\file\\drug");
    JSONExporter manyDrugExporter = JSONExporter.excelExporter(null, "标题中只含有多个药物名称", "D:\\file\\drug");
    JSONExporter noDrugExporter = JSONExporter.excelExporter(null, "标题中没有药物名称", "D:\\file\\drug");
    JSONExporter drugExporter = JSONExporter.excelExporter(null, "ctdrug", "D:\\file\\drug");
    KeywordsIndex keywordsIndex = new KeywordsIndex();
    DrugDict drugDict = new DrugDict();
    static List<String> gram_one = new ArrayList<>();
    static List<String> gram_two = new ArrayList<>();
    static List<String> gram_all = new ArrayList<>();
    int progress = 0;
    static {
        DBCursor dbCursor = mongoEntityClientDrug.cursor("drug", (DBObject) JSON.parse("{}"));
        while (dbCursor.hasNext()) {
            JSONObject o = JSONValue.parseJSONObject(dbCursor.next().toString());
            drugMap.put(String.valueOf(o.getInt("_id")), new JSONObject()
                    .append("name_cn", o.getString("name"))
                    .append("name_en", o.getString("name_en"))
                    .append("drug_type", o.getString("drug_type")));
        }

        for (DBObject o : mongoEntityClientDrug.cursor("drug", new BasicDBObject())) {
            String type = (String) o.get("drug_type");
            if (!StringUtil.isEmpty(type)) {
                drug_type.put(o.get("_id").toString(), type.toLowerCase());
            }
        }

        {//load gram
            gram_one = loadGram("nlp/one");
            gram_two = loadGram("nlp/two");
            gram_all = loadGram("nlp/all");
        }
    }

    private static List<String> loadGram(String file){
        List<String> list = new ArrayList();
        InputStream is = FileUtil.getResourceAsStream(DrugAnalysis.class, file);
        for (String s : FileUtil.readLines(is, "utf-8")) {
            list.add(s);
        }
        return list;
    }

    private  MongoClient.EntityIterator scanMongoDB(){
//        db.getCollection('ClinicalTrial.final').find({'content.link_drug':{$size:2},'content.source':'ClinicalTrialChinaGrab'})
//        return mongoEntityClientPaper.iterator("ClinicalTrial.final", (DBObject) JSON.parse("{content.link_drug:{$size:2}}"), null,null, 0, 0);
//        return mongoEntityClientPaper.iterator("ClinicalTrial.final", (DBObject) JSON.parse("{content.link_drug:{$size:2},content.source:'ClinicalTrialChinaGrab'}"), null,null, 0, 0);
        return mongoEntityClientCT.iterator("ClinicalTrial.final", (DBObject) JSON.parse("{\"content.source\":\"ClinicalTrialChinaGrab\"}"), null,null, 0, 0);
    }

    private JSONObject basicInfoExtract(JSONObject object){
        return new JSONObject()
                .append("title_cn", object.getByPath("title.cn"))
                .append("title_en", object.getByPath("title.en"))
                .append("matchedProductId", (ArrayList<String>)object.getByPath("link_drug"))
                .append("ct_id", object.getString("ct_id"));
    }

    private void loadDrugDict() {
        DBCursor dbCursor = mongoEntityClientDrug.cursor("drug", (DBObject) JSON.parse("{}"));
        while (dbCursor.hasNext()) {
            JSONObject o = JSONValue.parseJSONObject(dbCursor.next().toString());
            drugMap.put(String.valueOf(o.getInt("_id")), new JSONObject().append("name_cn", o.getString("name")).append("name_en", o.getString("name_en")));
        }
    }

    private String fetchField(JSONObject json, String[] arr) {
        StringBuilder buf = new StringBuilder();
        for (String x : arr) {
            buf.append(json.getString(x, " ")).append(" ; ");
        }
        return buf.toString();
    }

    private SourceProductDict.MappingResult matchDrug(JSONObject object){
        String queryCompoundDrug = fetchField(object, new String[]{ "title"});//"intervention",  "official_title"
        queryCompoundDrug = queryCompoundDrug.toLowerCase();
        return matchDrug(queryCompoundDrug);
    }

    private SourceProductDict.MappingResult matchDrug(String queryCompoundDrug){
        SourceProductDict.MappingResult result = null;
        try {
            result = drugClient.mapping(queryCompoundDrug, true);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        List<String> drugIds = new ArrayList<>();
        return result;
    }

    /*
    对于title不为空，但是没匹配上drug字典的，需要发现是否有新的drug
   */
    private void newDrugFind(JSONObject object){
        JSONObject objectExp = basicInfoExtract(object);
        drugExporter.buffer(objectExp);
    }

    /*
     对于title不为空，只匹配到了一个drug的，可以认为该drug就是实验drug
     */
    private void oneDrugExtract(JSONObject object){
        JSONObject objectExp = basicInfoExtract(object);
        String drug_id = ((ArrayList<String>) objectExp.get("matchedProductId")).get(0);
        objectExp.append("matchedProductName", drugMap.get(drug_id).getString("name"));
        objectExp.append("matchedProductNameEN", drugMap.get(drug_id).getString("name_en"));
        objectExp.append("实验药", drugMap.get(drug_id).getString("name"));
        objectExp.append("实验药id", drug_id);
        drugExporter.buffer(objectExp);
    }

    /*
      对于匹配到多个drug的ct，需要根据规则去提取实验药物
     */
    private void manyDrugExtract(JSONObject object, String title){
        JSONObject objectExp = basicInfoExtract(object);
        SourceProductDict.MappingResult result = matchDrug(title);
        if (result != null) {
            for (SourceProductDict.ProductItem item : result.results.values()) {
                if(drugMap.containsKey(item.productId) && !drugMap.get(item.productId).getString("drug_type","").equals("tcm")) {
                    keywordsIndex.addDrug(item);
                }
            }
        }
        keywordsIndex.add(drugDict.mapping(title));

        List<Word> list = new ArrayList<>();
        for(Word word:keywordsIndex.drugList){
            String keyword = word.getKeyword();
            int i = 0;
            while(title.indexOf(keyword,i) > -1){
                int start = title.indexOf(keyword, i);
                if(word.getStart() != start){
                    Word word1 = new Word(keyword, start, start+ keyword.length(), true, word.standardCN, word.standardEN, word.productId);
                    list.add(word1);
                    i = start + keyword.length() -1;
                }else{
                    break;
                }
            }
        }
        keywordsIndex.addDrug(list);

        keywordsIndex.rankByStartASC();
        List<String> drugTextList = keywordsIndex.getDrugText();
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        for(Word word:keywordsIndex.keywordList){
            String keyword = word.getKeyword();
            stringBuffer.append(drugTextList.contains(keyword) ? "*" : keyword);
            stringBuffer2.append(keyword);
        }
        System.out.println(title);
        System.out.println(stringBuffer);
        System.out.println();

        objectExp = matchGram(gram_one, keywordsIndex.drugList, stringBuffer.toString(), objectExp, "one");
        objectExp = matchGram(gram_two, keywordsIndex.drugList, stringBuffer.toString(), objectExp, "two");
        objectExp = matchGram(gram_all, keywordsIndex.drugList, stringBuffer.toString(), objectExp, "all");
        drugExporter.buffer(objectExp);
    }

    /*
     对于匹配到多个drug的ct，需要根据规则去提取实验药物
    */
    private void manyDrugExtract(JSONObject object){
        String title = object.getByPath("title.cn") == null||object.getByPath("title.cn").toString().isEmpty()?object.getByPath("title.en"):object.getByPath("title.cn");
        manyDrugExtract(object, title);
    }

    private void drugProcess(String ct_id){
//        MongoClient.EntityIterator iterator = scanMongoDB();
        if (mongoEntityClientCT.exist("ClinicalTrial.final", ct_id)) {
            JSONObject object = JSONValue.parseJSONObject(mongoEntityClientCT.findOne("ClinicalTrial.final", (DBObject) JSON.parse("{\"_id\":\"" + ct_id + "\"}"), null, null).toString()).getJSONObject("content").append("ct_id", ct_id);
            ArrayList<String> link_drug = object.getByPath("link_drug");
            String title = object.getByPath("title.cn") == null||object.getByPath("title.cn").toString().isEmpty()?object.getByPath("title.en"):object.getByPath("title.cn");
            title = title.toLowerCase();
            keywordsIndex.clean();
            if(!title.isEmpty() && (link_drug == null || link_drug.isEmpty())){
                newDrugFind(object);
            }
            if(!title.isEmpty() && link_drug != null && link_drug.size() == 1){
                oneDrugExtract(object);
            }
            if(link_drug != null && link_drug.size() > 1){
                manyDrugExtract(object, title);
            }
        }

    }

    private void run(){
        MongoClient.EntityIterator iterator = mongoEntityClientCT.iterator("ClinicalTrialChinaGrab", (DBObject) JSON.parse("{}"), (DBObject) JSON.parse("{\"_id\":1}"), null, 0, 1000);
//        MongoClient.EntityIterator iterator = mongoEntityClientCT.iterator("ClinicalTrialChinaGrab", (DBObject) JSON.parse("{\"_id\":\"ChiCTR-INR-16009235\"}"), (DBObject) JSON.parse("{\"_id\":1}"), null, 0, 1000);
        int i=0;
        while (iterator.hasNext()){
            i++;
            String id = iterator.next().toJSONObject().getString("_id");
            if(i%100 == 0){
                System.out.println(i + " " + id);
            }
            drugProcess(id);
        }
        drugExporter.export();
    }




    private void manyDrugExtractTest(JSONObject object, String title){
        SourceProductDict.MappingResult result = matchDrug(title);
        if (result != null) {
            for (SourceProductDict.ProductItem item : result.results.values()) {
                if(drugMap.containsKey(item.productId) && !drugMap.get(item.productId).getString("drug_type","").equals("tcm")) {
                    keywordsIndex.addDrug(item);
                }
            }
        }
        keywordsIndex.add(drugDict.mapping(title));

        List<Word> list = new ArrayList<>();
        for(Word word:keywordsIndex.drugList){
            String keyword = word.getKeyword();
            int i = 0;
            while(title.indexOf(keyword,i) > -1){
                int start = title.indexOf(keyword, i);
                if(word.getStart() != start){
                    Word word1 = new Word(keyword, start, start+ keyword.length(), true, word.standardCN, word.standardEN, word.productId);
                    list.add(word1);
                    i = start + keyword.length() -1;
                }else{
                    break;
                }
            }
        }
        keywordsIndex.addDrug(list);

        keywordsIndex.rankByStartASC();
        List<String> drugTextList = keywordsIndex.getDrugText();
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        for(Word word:keywordsIndex.keywordList){
            String keyword = word.getKeyword();
            stringBuffer.append(drugTextList.contains(keyword) ? "*" : keyword);
            stringBuffer2.append(keyword);
        }
        System.out.println(title);
        System.out.println(stringBuffer);
        System.out.println();

        object = matchGram(gram_one, keywordsIndex.drugList, stringBuffer.toString(), object, "one");
        object = matchGram(gram_two, keywordsIndex.drugList, stringBuffer.toString(), object, "two");
        object = matchGram(gram_all, keywordsIndex.drugList, stringBuffer.toString(), object, "all");
        drugExporter.buffer(object);
    }

    private JSONObject matchGram(List<String> gramList, List<Word> drugList, String gramText, JSONObject object, String type){
        int gramLength = 0;
        for(String gram:gramList){
            if(gramText.indexOf(gram) > -1){
                if(gramLength < gram.length()) {
                    gramLength = gram.length();
                    switch (type) {
                        case "one":
                            object.append("实验药id", drugList.get(0).productId);
                            object.append("实验药keyword", drugList.get(0).keyword);
                            object.append("实验药standardCN", drugList.get(0).standardCN);
                            object.append("实验药standardEN", drugList.get(0).standardEN);
                            break;
                        case "two":
                            object.append("实验药id", drugList.get(0).productId + "," + drugList.get(1).productId);
                            object.append("实验药keyword", drugList.get(0).keyword + "," + drugList.get(1).keyword);
                            object.append("实验药standardCN", drugList.get(0).standardCN + "," + drugList.get(1).standardCN);
                            object.append("实验药standardEN", drugList.get(0).standardEN + "," + drugList.get(1).standardEN);
                            break;
                        case "all":
                            object.append("实验药id", String.join(",", drugList.stream().map(x -> x.productId).distinct().collect(Collectors.toList())));
                            object.append("实验药keyword", String.join(",", drugList.stream().map(x -> x.keyword).distinct().collect(Collectors.toList())));
                            object.append("实验药standardCN", String.join(",", drugList.stream().map(x -> x.standardCN).distinct().collect(Collectors.toList())));
                            object.append("实验药standardEN", String.join(",", drugList.stream().map(x -> x.standardEN).distinct().collect(Collectors.toList())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return object;
    }

    private void test(){
        ExcelReader reader = new ExcelReader() {
            @Override
            protected void work(int sheetIndex, int rowIndex, JSONObject object) {
                object.put("rowIndex", rowIndex);
                String title = object.getString("注册题目");
                List<String> link_drug = new ArrayList<>();
                if(!object.getString("product","").isEmpty()) {
                    Arrays.stream(object.getString("product").split(",")).forEach(id -> {
                        link_drug.add(id.replaceAll(" ", ""));
                    });
                }
//                List<String> link_drug = Arrays.asList(object.getString("product").replaceAll(" ",""));
                title = title.toLowerCase();
                keywordsIndex.clean();
                if(!title.isEmpty() && link_drug != null && link_drug.isEmpty()){
                    newDrugFind(object);
                    object.append("试验药id", "");
                    drugExporter.buffer(object);
                }
                if(!title.isEmpty() && link_drug != null && link_drug.size() == 1){
                    object.append("试验药id", object.getString("product"));
                    object.append("试验药", object.getString("product"));
                    drugExporter.buffer(object);
                }
                if(link_drug != null && link_drug.size() > 1){
                    manyDrugExtractTest(object, title);
                }
            }
        };
        reader.readFile2007("C:\\Users\\lisa.wei\\Desktop\\metrix\\ct drug\\CT数据整理的第一批.xlsx", "Sheet2");
        drugExporter.export();
    }

    public static void main(String[] args) {

        DrugAnalysis drugAnalysis = new DrugAnalysis();
        drugAnalysis.run();


//        drugAnalysis.test();
//        drugAnalysis.matchDrug(new JSONObject());

//        Queue<String> queue = new LinkedList<String>();
//        //添加元素
//        queue.offer("a");
//        queue.offer("b");
//        queue.offer("c");
//        queue.offer("d");
//        queue.offer("e");
//        for(String q : queue){
//            System.out.println(q);
//        }
    }
}
