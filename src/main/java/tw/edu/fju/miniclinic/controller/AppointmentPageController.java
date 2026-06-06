package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class AppointmentPageController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    // 支援單數與複數
    @GetMapping({"/appointments", "/appointment"})
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepo.findAll());
        return "appointments";
    }

    @GetMapping("/stats")
    public String getStats(Model model) {
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());

        // 取得科別分組統計
        List<Object[]> deptData = appointmentRepo.countAppointmentsByDepartment();
        Map<String, Long> deptStats = deptData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
        model.addAttribute("deptStats", deptStats);

        return "stats";
    }
}