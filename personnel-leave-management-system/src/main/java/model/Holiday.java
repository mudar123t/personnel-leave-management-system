/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


import java.util.Date;

public class Holiday {

    private int holidayId;
    private Date date;
    private String name;
    private boolean isRecurring;

    public Holiday() {}

    public Holiday(int holidayId, Date date, String name, boolean isRecurring) {
        this.holidayId = holidayId;
        this.date = date;
        this.name = name;
        this.isRecurring = isRecurring;
    }

    public int getHolidayId() { return holidayId; }
    public void setHolidayId(int holidayId) { this.holidayId = holidayId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
}
