package com.gbdata.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="testSubject")
public class TestSubject {
    @XmlElement(name="person", type = Person.class)
    List<Person> person_list;

    public List<Person> getPersonList() {
        return person_list;
    }

    public void setPersonList(List<Person> personList) {
        this.person_list = personList;
    }
}
