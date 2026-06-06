package tw.edu.fju.miniclinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該筆預約"));
        appointment.setStatus("CANCELLED"); // 將狀態改為已取消
        appointmentRepository.save(appointment);
    }
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該筆預約"));
        appointment.setStatus("COMPLETED"); // 將狀態改為已完成
        appointmentRepository.save(appointment);
    }
}