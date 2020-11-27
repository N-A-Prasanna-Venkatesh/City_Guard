package com.Minimise.Crimes;

public class Details {
    private String name="";
    private  String subname="";

    public Details(String n,String s){
        this.name=n;
        this.subname=s;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }
}
