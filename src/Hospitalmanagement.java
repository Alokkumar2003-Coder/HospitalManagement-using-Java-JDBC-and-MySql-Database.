import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
//import javax.naming.spi.DirStateFactory.Result;


public class Hospitalmanagement
{
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final  String password = "alok@2003";
    public static void main(String[] args)
    {
        
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try
        {
            Connection connection = DriverManager.getConnection(url, username, password);
           
            Patient Patient = new Patient(connection, scanner);
            Doctor Doctor = new Doctor(connection);
            while (true) 
            {
                System.out.println("HOSPITAL MANAGEMENT");
                System.out.println("1. Add Patients");
                System.out.println("2. View Patients");
                System.out.println("3. View Dictors");
                System.out.println("4. Book Appointments");
                System.out.println("5. Exit");
                System.out.println("Enter User choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Add Patients
                        Patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patients
                        Patient.viewPatient();
                        System.out.println();
                        break;

                    case 3:
                        // View Doctors
                        Doctor.viewDoctor();
                        System.out.println();
                        break;

                    case 4:
                        // Book Appointment
                        BookAppointment(Patient,Doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        return;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }   
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        } 
    }

    public static void BookAppointment(Patient patient, Doctor doctor, Connection connection , Scanner scanner)
    {
        System.out.println("Enter Patients Id = ");   
        int patient_id = scanner.nextInt();
        System.out.println("Enter Doctors Id = ");   
        int doctor_id = scanner.nextInt();
        System.out.println("Enter appointment_date(YYYY-MM-DD)");
        String appointment_date = scanner.next();
        if(patient.getPatientById(patient_id) && doctor.getDoctorById(doctor_id))
        {
            if(checkDoctoravailability(doctor_id , appointment_date ,connection))
            {
                String appointmentQuery = "insert into appointments(patient_id, doctor_id, appointment_date) values(?, ?, ?)";
            try 
            {
                PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                preparedStatement.setInt(1 , patient_id);
                preparedStatement.setInt(2, doctor_id);
                preparedStatement.setString(3, appointment_date); 
                int rowsAffected = preparedStatement.executeUpdate();
                if(rowsAffected>0){
                
                    System.out.println("Appointment Booked..!");
                }
                else
                {
                    System.out.println("failed to book Appointment...Sorry!");
                }
            }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            
            }
            else
            {
                System.out.println("Doctor is not Available on this Date... Sorry!..");
            }
            }
        else
        {
            System.out.println("Either doctor or patient doesn't exist...!");
        }
    }
    public static boolean checkDoctoravailability(int doctor_id , String appointment_date , Connection connection)
    {
        String query = "select count(*) from appointments where doctor_id = ? and appointment_date = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, appointment_date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count = resultSet.getInt(1);
                if(count==0)
                {
                    return true;
                }
                else 
                {
                    return false;
                }
            }
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    
}