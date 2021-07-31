package com.inno.SAS_HospitalManagement.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inno.SAS_HospitalManagement.repository.AdminRepository;
import com.inno.SAS_HospitalManagement.repository.RoomRepository;
import com.inno.SAS_HospitalManagement.model.PatientAdmitInfo;
import com.inno.SAS_HospitalManagement.model.Room;

@Service
public class AdminService {

	private AdminRepository adminRepository;
	private RoomRepository roomRepository;

	public AdminService(AdminRepository adminRepository, RoomRepository roomRepository) {
		this.adminRepository = adminRepository;
		this.roomRepository = roomRepository;
	}

	public void admit(PatientAdmitInfo patientAdmitInfo)
	{
		adminRepository.save(patientAdmitInfo);
	}
		
	@Transactional
	public void discharge(PatientAdmitInfo patientAdmitInfo) {
		adminRepository.updateDischarge(patientAdmitInfo.getDateOfDischarge(),
				patientAdmitInfo.getTimeOfDischarge(), patientAdmitInfo.getBillAmt(), patientAdmitInfo.getPatientId());
		System.out.println(patientAdmitInfo.getDateOfDischarge() + " " + patientAdmitInfo.getTimeOfDischarge() + " "
				+ patientAdmitInfo.getBillAmt() + " " + patientAdmitInfo.getPatientId());
	}
	
	public void assignRoom(Room room) 
	{
		roomRepository.updateRoom(room.getAssignedTo(), room.getStatus(), room.getRoomNo());
	}
	public int getRoomNo(String assignedTo)
	{
		return roomRepository.getRoomNo(assignedTo);
	}
	
	public Optional<Room> getRoomStatus(int roomNo)
	{
		return roomRepository.getRoomStatus(roomNo);		
	}
}