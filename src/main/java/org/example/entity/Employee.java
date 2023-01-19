package org.example.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name="EMPLOYEE_TABLE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Employee {

    @Id
    private Long e_id;

    private EmployeeName name;

    @Column(name="e_age")
    private Integer age;

    public Employee() {}

    public Employee(Long e_id, EmployeeName name, Integer age) {
        this.e_id = e_id;
        this.name = name;
        this.age = age;
    }

    public Long getE_id() {
        return e_id;
    }

    public void setE_id(Long e_id) {
        this.e_id = e_id;
    }

    public EmployeeName getName() {
        return name;
    }

    public void setName(EmployeeName name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "e_id=" + e_id +
                ", name=" + name +
                ", age=" + age +
                '}';
    }
}
