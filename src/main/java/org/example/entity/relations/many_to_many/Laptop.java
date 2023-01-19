package org.example.entity.relations.many_to_many;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Laptop_many_to_many")
public class Laptop {

    @Id
    private Integer lid;

    private String lname;

    @ManyToMany
    private List<Student> students = new ArrayList<>();

    public Integer getLid() {
        return lid;
    }

    public void setLid(Integer lid) {
        this.lid = lid;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "Laptop{" + "lid=" + lid + ", lname='" + lname + '\'' + ", students=" + students + '}';
    }
}
