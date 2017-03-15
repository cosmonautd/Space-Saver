package com.cosmonautd.spacesaver.baseclass;

import java.util.Calendar;

public class Directory extends Event {

    protected String path;
    protected int period;
    protected int cycles;

    public Directory() {
        super();
        setId(-1);
        setPath("");
        setDate(Calendar.getInstance());
        setPeriod(-1);
        setCycles(-1);
        this.sort = generateSortByDate(getDate());
    }

    public Directory(int id, String path, Calendar startDate, int period, int cycles){
        super();
        setId(id);
        setPath(path);
        setDate(startDate);
        setPeriod(period);
        setCycles(cycles);
        this.sort = generateSortByDate(getDate());
    };

    public String getPath() { return path; }
    public int getPeriod() { return period; }
    public int getCycles() { return cycles; }

    public void setPath(String path) { this.path = path; }
    public void setPeriod(int period) { this.period = period; }
    public void setCycles(int cycles) { this.cycles = cycles; }
}