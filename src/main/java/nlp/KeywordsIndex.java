package nlp;

import com.gbdata.dict.prodcn.SourceProductDict;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lisa.wei on 2019/7/5.
 */
public class KeywordsIndex {

    public List<Word> keywordList = new ArrayList<>();
    public List<Word> drugList = new ArrayList<>();

    public void rankByStartASC(){
        keywordList.sort(Comparator.comparing(Word::getStart));
        drugList.sort(Comparator.comparing(Word::getStart));
    }

    public void rankByStartDESC(){
        keywordList.sort(Comparator.comparing(Word::getStart).reversed());
        drugList.sort(Comparator.comparing(Word::getStart).reversed());
    }

    public void rankBydrugPositionASC(){
        drugList.sort(Comparator.comparing(Word::getDrugPosition));
    }

    public void rankBydrugPositionDESC(){
        drugList.sort(Comparator.comparing(Word::getDrugPosition).reversed());
    }

    public void add(List<Word> kwList){
        keywordList.addAll(kwList);
        keywordList.stream().distinct();
    }

    public void add(Word index){
        keywordList.add(index);
        if(index.isDrug){
            drugList.add(index);
        }
    }

    public void addKeyword(Word word){
        keywordList.add(word);
    }

    public void addDrug(SourceProductDict.ProductItem item){
        String tagEN = item.tagEN;
        String tagCN = item.tagCN;
        String keyword = tagEN.isEmpty()?tagCN:tagEN;
        Word word = new Word(keyword, item.matched_interval.getInt("start"), item.matched_interval.getInt("end"), true, item.standardCN, item.standardEN, item.productId);
        keywordList.add(word);
        drugList.add(word);
    }

    public void addDrug(Word word){
        keywordList.add(word);
        if(word.isDrug){
            drugList.add(word);
        }
    }

    public void addDrug(List<Word> kwList){
        keywordList.addAll(kwList);
        drugList.addAll(kwList);
        keywordList.stream().distinct();
        drugList.stream().distinct();
    }

    public int keywordSize(){
        return keywordList.size();
    }

    public int drugSize(){
        return drugList.size();
    }

    public void clean(){
        keywordList.clear();
        drugList.clear();
    }

    public List<String> getDrugText(){
        rankByStartASC();
        List<String> list = new ArrayList<>();
        drugList.forEach(x->list.add(x.getKeyword()));
        return list;
//        return drugList.stream().map(x -> x.getKeyword()).collect(Collectors.toList());   打乱了排序
    }
}
