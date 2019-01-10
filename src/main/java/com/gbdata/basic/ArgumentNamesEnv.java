package com.gbdata.basic;

public enum ArgumentNamesEnv {
    SOLR_TEST_HOST("solr.test.host"),
    SOLR_PRE_HOST("solr.pre.host"),
    SOLR_ONLINE_HOST("solr.online.host"),
    MONGO_TEST_HOST("mongo.test.host"),
    CONSISTENCY_MONGO("consistency.mongo"),
    CONSISTENCY_RESULT_MONGO("consistency_result.mongo");
    public String name;

    ArgumentNamesEnv(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
