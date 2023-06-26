package com.Schedule.service;

import java.util.List;

import com.Schedule.dto.Schedule;

public interface ScheduleService {
	
	public int maxNum() throws Exception;
	
	public void insertData(Schedule schedule) throws Exception; 
	
	public int getDataCount(String searchKey, String searchValue) throws Exception;
	
	public List<Schedule> getLists(String searchKey, String searchValue, int start, int end) throws Exception;
	
	public Schedule getReadData(int num) throws Exception;
	
	public void updateData(Schedule schedule) throws Exception;

	public void deleteData(int num) throws Exception;
}
