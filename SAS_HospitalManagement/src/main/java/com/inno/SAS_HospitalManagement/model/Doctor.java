package com.inno.SAS_HospitalManagement.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "Doctor")
public class Doctor extends Employee {
	
	@Column
	private String Spacialization;
}
