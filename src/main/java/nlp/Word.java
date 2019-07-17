package nlp;

/**
 * Created by lisa.wei on 2019/7/8.
 */
public class Word {
    public String keyword;
    public int start;
    public int end;
    public Boolean isDrug;
    public String standardCN;
    public String standardEN;
    public String productId;
    public int drugPosition;  //drug在这个字符串中属于第几个drug,从0开始

    public Word(String keyword, int start, int end, Boolean isDrug){
        this.keyword = keyword;
        this.start = start;
        this.end = end;
        this.isDrug = isDrug;
    }

    public Word(String keyword, int start, int end, Boolean isDrug, String standardCN, String standardEN, String productId){
        this.keyword = keyword;
        this.start = start;
        this.end = end;
        this.isDrug = isDrug;
        this.standardCN = standardCN;
        this.standardEN = standardEN;
        this.productId = productId;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Boolean getDrug() {
        return isDrug;
    }

    public int getDrugPosition() {
        return drugPosition;
    }
}
