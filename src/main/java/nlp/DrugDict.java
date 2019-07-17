package nlp;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lisa.wei on 2019/7/5.
 */
public class DrugDict {
    Trie keywordDict = new Trie();
    public void build(){
        Trie trie = new Trie().onlyWholeWords();
            for (String key : Arrays.asList("与","联合","单药","或","联合", "给药", "比较","及", "对比","和",
                    "标准治疗")) {//"化疗", "放疗" "安慰剂",
                    trie.addKeyword(key, key);
            }
        trie.checkBuild();
        keywordDict = trie;
    }

    public List<Word> mapping(String query) {
        build();
        List<Word> keywordList = new ArrayList<>();
        for (Emit emit : keywordDict.parseText(query)) {
            String keyword = (String) emit.getData();
            Word word = new Word(keyword, emit.start, emit.end, false);
            keywordList.add(word);
        }
        return keywordList;
    }

    public static void main(String[] args) {
//        DrugDict drugDict = new DrugDict();
//        drugDict.build();
//        KeywordsIndex keywordsIndex= new KeywordsIndex();
//        keywordsIndex.keywordList = drugDict.mapping(keywordsIndex.keywordList, "评估早期或局部晚期HER2阳性乳腺癌患者术前接受帕妥珠单抗联合多西他赛、曲妥珠单抗（新辅助疗法）以及术后化疗后接受帕妥珠单抗联合曲妥珠单抗（辅助治疗）的随机、多中心、双盲、安慰剂对照Ⅲ期研究");
//        keywordsIndex.rankByStartASC();
//        System.out.println();
    }
}
