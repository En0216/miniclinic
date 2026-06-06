package tw.edu.fju.miniclinic.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class StatsController {
    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/api/stats")
    public Map<String, Object> getNestedStats() {
        // 使用 Map.of() 建立雙層巢狀結構
        // 內層 Map 包含各項統計數據，外層 Map 則定義資料分類 (如 "statistics")
        return Map.of(
            "totalDoctors", doctorRepo.count(),
            "totalPatients", patientRepo.count(),
            "totalAppointments", appointmentRepo.count(),
            "byStatus", Map.of(
                "BOOKED", appointmentRepo.countByStatus("BOOKED"),
                "COMPLETED", appointmentRepo.countByStatus("COMPLETED"),
                "CANCELLED", appointmentRepo.countByStatus("CANCELLED")
            )
        );
    }
}
