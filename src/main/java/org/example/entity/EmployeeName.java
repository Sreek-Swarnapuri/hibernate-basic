package org.example.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class EmployeeName {

    private String fname;

    private String lname;

    private String mname;

    public EmployeeName() {
    }

    public EmployeeName(String fname, String lname, String mname) {
        this.fname = fname;
        this.lname = lname;
        this.mname = mname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    @Override
    public String toString() {
        return "EmployeeName{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", mname='" + mname + '\'' +
                '}';
    }
}
