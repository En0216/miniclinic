package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpSession;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class AppointmentApiController {
    
    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/appointments")
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {

        if (date != null && !date.isBlank()) {
            return appointmentRepo.findByApptDate(LocalDate.parse(date));
        }
        if (doctorId != null && !doctorId.isBlank()) {
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if (doctor == null) {
                return List.of();
            }
            return appointmentRepo.findByDoctor(doctor);
        }
        return appointmentRepo.findAll();
    }

    @GetMapping("/api/appointments/count")
    public Map<String, Long> getAppointmentCount() {
        return Map.of("count", appointmentRepo.count());
    }
    
    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
		@PathVariable Long apptId,
		@RequestBody Map<String, String> payload,
		HttpSession session) {

	String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

	Appointment appt = appointmentRepo.findById(apptId).orElse(null);
	if (appt == null) {
		return ResponseEntity.notFound().build();
	}

	// 檢查權限：若是從醫師後台修改，則需檢查是否為本人
    // 若是從一般頁面取消，可視需求調整此邏輯
    if (loggedInDoctorId != null) {
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }
    }

	String newStatus = payload.get("status");
	if (!List.of("BOOKED", "COMPLETED").contains(newStatus)) {
		return ResponseEntity.badRequest().build();
	}

	appt.setStatus(newStatus);
	return ResponseEntity.ok(appointmentRepo.save(appt));
    }

    @PutMapping("/api/doctor/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> payload,
            jakarta.servlet.http.HttpSession session) {

        // 1. 檢查登入狀態
        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");
        if (loggedInDoctorId == null) {
            return ResponseEntity.status(401).body("請先登入系統");
        }

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (oldPassword == null || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("密碼欄位不可為空");
        }

        // 2. 撈出醫師資料
        Doctor doctor = doctorRepo.findById(loggedInDoctorId).orElse(null);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }

        // 3. 💡 關鍵修正：改用 passwordEncoder.matches() 來比對明文與加密後的密碼
        if (!passwordEncoder.matches(oldPassword, doctor.getPasswordHash())) {
            return ResponseEntity.status(400).body("舊密碼輸入錯誤");
        }

        // 4. 💡 關鍵修正：將新密碼用 passwordEncoder.encode() 加密後再存入資料庫
        doctor.setPasswordHash(passwordEncoder.encode(newPassword));
        doctorRepo.save(doctor);

        return ResponseEntity.ok().build();
    }
}
