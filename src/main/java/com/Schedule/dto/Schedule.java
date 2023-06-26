package com.Schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Schedule {
	private int num;
	private String title;
	private String name;
	private String pwd;
	private String content;
	private String start_time;
	private String end_time;
	private String create_date;
}
