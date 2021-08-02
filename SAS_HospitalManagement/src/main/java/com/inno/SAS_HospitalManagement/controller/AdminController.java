package com.inno.SAS_HospitalManagement.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inno.SAS_HospitalManagement.model.Doctor;
import com.inno.SAS_HospitalManagement.model.Patient;
import com.inno.SAS_HospitalManagement.model.PatientAdmitInfo;
import com.inno.SAS_HospitalManagement.model.Room;
import com.inno.SAS_HospitalManagement.service.AdminService;
import com.inno.SAS_HospitalManagement.service.PatientService;

@RestController
@RequestMapping("hospital/admin")
public class AdminController
{

	private final AdminService adminService;

	private final PatientService patientService;

	public AdminController(AdminService adminService, PatientService patientService) 
	{
		this.adminService = adminService;

		this.patientService = patientService;
	}

	// Controller method to admit the patient
	@PostMapping(path = "admit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> admit(@RequestBody PatientAdmitInfo patientAdmitInfo, Room room) 
	{
		Date date = new Date(System.currentTimeMillis());
		Time time = new Time(System.currentTimeMillis());
		room.setStatus("Not Available");
		room.setAssignedTo(patientAdmitInfo.getPatientId());
		room.setRoomNo(patientAdmitInfo.getAssignedRoom());
		patientAdmitInfo.setDateOfAdmit(date);
		patientAdmitInfo.setTimeOfAdmit(time);
		try 
		{
			Optional<Patient> id = patientService.findPatient(patientAdmitInfo.getPatientId());
			if (id.isEmpty()) 
			{
//				patientService.register(patient);
				return ResponseEntity.ok("Register the patient details");
			} else 
			{
				adminService.assignRoom(room);
				adminService.admit(patientAdmitInfo);
				return ResponseEntity.ok("Patient Admited");
			}
		} 
		catch (RuntimeException re) 
		{
			System.out.println(re.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
		}
	}

	@Transactional
	@PostMapping(path = "discharge", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> discharge(@RequestBody PatientAdmitInfo patientAdmitInfo, Room room) 
	{
		Time time = new Time(System.currentTimeMillis());
		Date date = new Date(System.currentTimeMillis());
		patientAdmitInfo.setDateOfDischarge(date);
		patientAdmitInfo.setTimeOfDischarge(time);

//		room.setAssignedTo(patientAdmitInfo.getPatientId());
		room.setStatus("Available");
		int room_num = adminService.getRoomNo(patientAdmitInfo.getPatientId());
		room.setAssignedTo(null);
//		System.out.println(room_num + " " + room.getAssignedTo());
		room.setRoomNo(room_num);
		try 
		{
			adminService.assignRoom(room);
			adminService.discharge(patientAdmitInfo);
			return ResponseEntity.ok("Discharged");
		} 
		catch (RuntimeException rex) 
		{
			System.out.println(rex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("database connection lossed");
		}
	}

	@PostMapping(path = "assignroom", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> assignRoom(@RequestBody Room room) 
	{
		try 
		{
			adminService.assignRoom(room);
			room.setStatus("Not Available");
			return ResponseEntity.ok("Room Assigned");
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PostMapping(path = "getpatientroom", consumes = MediaType.APPLICATION_NDJSON_VALUE)
	public int getRoomNo(@RequestBody Room room)
	{
		return adminService.getRoomNo(room.getAssignedTo());
	}
	
	@GetMapping(path = "roomstatus")
	public Optional<Room> getRoomStatus(@RequestParam int roomNo)
	{
		return adminService.getRoomStatus(roomNo);
	}
	@PostMapping(path="doctorDetails",consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doctorDetails(@RequestBody Doctor doctor)
	{
		System.out.println(doctor.getSpacialization());
		System.out.println(doctor.getDoc_id());
		System.out.println(doctor.getConsultingPatientId());
		adminService.doctorDetails(doctor);
		
		return ResponseEntity.ok("Doctor details updated");
	}

}