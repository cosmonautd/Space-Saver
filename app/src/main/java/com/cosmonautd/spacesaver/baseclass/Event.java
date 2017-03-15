package com.cosmonautd.spacesaver.baseclass;

import java.util.Calendar;
import java.util.Comparator;

public abstract class Event implements Comparator<Event>{

    protected int id;
    protected String description;
    protected Calendar date;
    protected String dateString;
    protected int sort;

    public Event(){}

    public int generateSortByDate(Calendar date) {
        return (int) (date.getTimeInMillis()/1000);
    }

    public int getId(){ return id;}
    public String getDescription(){ return description;}
    public Calendar getDate(){ return date;}
    public String getDateString(){return dateString;}
    public int getSort(){ return sort;}

    public void setId(int id){ this.id = id;}
    public void setDescription(String description){ this.description = description;}
    public void setDate(Calendar date){
        this.date = date;
        this.dateString = String.format("%02d/%02d/%04d %02d:%02d",
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.YEAR),
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE));
    }

    public int compare(Event one, Event another){
        int returnVal = 0;

        if(one.getSort() < another.getSort()){
            returnVal =  -1;
        }else if(one.getSort() > another.getSort()){
            returnVal =  1;
        }else if(one.getSort() == another.getSort()){
            returnVal =  0;
        }
        return returnVal;
    }
}
