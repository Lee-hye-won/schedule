package com.Schedule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Schedule.dao.ScheduleDao;
import com.Schedule.dto.Schedule;


@Service
public class ScheduleServiceImpl implements ScheduleService{

	@Autowired
	private ScheduleDao scheduleMapper;
	
	@Override
	public int maxNum() throws Exception {
		return scheduleMapper.maxNum();
	}
	
	@Override
	public void insertData(Schedule schedule) throws Exception {
		scheduleMapper.insertData(schedule);
		
	}

	@Override
	public int getDataCount(String searchKey, String searchValue) throws Exception {
		return scheduleMapper.getDataCount(searchKey, searchValue);
	}
	
	@Override
	public List<Schedule> getLists(String searchKey, String searchValue, int start, int end) throws Exception {
		return scheduleMapper.getLists(searchKey, searchValue, start, end);
	}
	

	@Override
	public Schedule getReadData(int num) throws Exception {
		return scheduleMapper.getReadData(num);
	}

	@Override
	public void updateData(Schedule schedule) throws Exception {
		scheduleMapper.updateData(schedule);
	}

	@Override
	public void deleteData(int num) throws Exception {
		scheduleMapper.deleteData(num);
		
	}



}
