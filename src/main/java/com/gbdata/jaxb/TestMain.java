package com.gbdata.jaxb;

import com.gbdata.common.util.FileUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lisa.wei on 2018/10/9.
 */
public class TestMain {

    public static void main(String[] args) {
        try {
            JAXBContext jc = JAXBContext.newInstance(Test.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            File xml = new File("E:\\GitHubXieyijiejie\\JavaLearning\\target\\classes\\config.xml");
            Test tests = (Test) unmarshaller.unmarshal(xml);
            System.out.println("sdfsdf");
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
