package tw.edu.fju.miniclinic.model;


import jakarta.persistence.*;
import java.sql.Types;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "patient")
public class Patient {

    @Id  //(JPA的註解，表示這個欄位是主鍵)
    @Column(name = "chart_no", length = 10)
    private String chartNo;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "gender", length = 10, nullable = false)
    private String gender;

    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = LocalDateConverter.class)
    @Column(name = "birth_date", columnDefinition = "TEXT")
    private LocalDate birthDate;

    @Column(name = "phone", nullable = false)
    private String phone; 

    // JPA 需要無參數的建構子
    public Patient() {}

    public Patient(String chartNo, String name, String gender, String phone) {
        this.chartNo = chartNo;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
    }

    // Getters
    public String getChartNo() { return chartNo; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }

    // Setters
    public void setChartNo(String chartNo) { this.chartNo = chartNo; }
    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
}