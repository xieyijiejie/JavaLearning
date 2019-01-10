package com.gbdata.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Test")
public class Test {
    @XmlElement(name = "testSubject", type = TestSubject.class)
    List<TestSubject> testSubjects;

    public List<TestSubject> getTestSubject() {
        return testSubjects;
    }

    public void setTestSubject(List<TestSubject> testSubjects) {
        this.testSubjects = testSubjects;
    }
}