package com.mindex.challenge.data;

public class ReportingStructure {
	private Employee employee;
	private Integer numberOfReports;

	public ReportingStructure(Employee employee, Integer numberOfReports) {
		this.employee = employee;
		this.numberOfReports = numberOfReports;
	}
	
	public Employee getEmployee() {
		return this.employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public Integer getNumberOfReports() {
		return this.numberOfReports;
	}
	
	public void setNumberOfReports(Integer numberOfReports) {
		this.numberOfReports = numberOfReports;
	}

}
