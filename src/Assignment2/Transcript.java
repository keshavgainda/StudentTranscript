package Assignment2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;


/* PLEASE DO NOT MODIFY A SINGLE STATEMENT IN THE TEXT BELOW.
READ THE FOLLOWING CAREFULLY AND FILL IN THE GAPS

I hereby declare that all the work that was required to 
solve the following problem including designing the algorithms
and writing the code below, is solely my own and that I received
no help in creating this solution and I have not discussed my solution 
with anybody. I affirm that I have read and understood
the Senate Policy on Academic honesty at 
https://secretariat-policies.info.yorku.ca/policies/academic-honesty-senate-policy-on/
and I am well aware of the seriousness of the matter and the penalties that I will face as a 
result of committing plagiarism in this assignment.

BY FILLING THE GAPS,YOU ARE SIGNING THE ABOVE STATEMENTS.

Full Name: Keshav Narain Gainda
Student Number: 216891319
Course Section: B
*/


/**
* This class generates a transcript for each student, whose information is in the text file.
* 
*
*/

public class Transcript {
	private ArrayList<Object> grade = new ArrayList<Object>();
	private File inputFile;
	private String outputFile;
	
	/**
	 * This the the constructor for Transcript class that 
	 * Initialises its instance variables and call readFie private
	 * method to read the file and construct this.grade.
	 * @param inFile is the name of the input file.
	 * @param outFile is the name of the output file.
	 */
	public Transcript(String inFile, String outFile) {
		inputFile = new File(inFile);	
		outputFile = outFile;	
		grade = new ArrayList<Object>();
		this.readFile();
	} // end of Transcript constructor

	/** 
	 * This method reads a text file and add each line as 
	 * an entry of grade ArrayList.
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	private void readFile() {
		Scanner sc = null; 
		try {
			sc = new Scanner(inputFile);	
			while(sc.hasNextLine()){
				grade.add(sc.nextLine());
	        }      
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			sc.close();
		}		
	} // end of readFile
	
	/**
	 * This method creates and returns an ArrayList, whose element is an object of class Student.
	 * The object at each element is created by aggregating ALL the information, that is found for one student
	 * in the grade Arraylist of class Transcript, i.e. if the text file contains information about 
	 * 9 students, then the array list will have 9 elements.
	 * @return returns an arraylist 
	 * @throws InvalidTotalException  
	 */
	public ArrayList<Student> buildStudentArray() throws InvalidTotalException { 
		ArrayList<String> namesOfStudents = new ArrayList<>();
		ArrayList<Student> students = new ArrayList<Student>(); 
        for (Object record : grade) {  										//loop for creating an ArrayList with unique names. 
            ArrayList<String> allInfo = new ArrayList<String>(Arrays.asList(((String) record).split(","))); 
            int lastIndex = allInfo.size() - 1;
            String name = allInfo.get(lastIndex).trim();
            if(!namesOfStudents.contains(name)) { 							//an ArrayList is created containing non - repetitive names
                namesOfStudents.add(name);
            }
        }

        ArrayList<ArrayList<ArrayList<Double>>> allGrades = new ArrayList<ArrayList<ArrayList<Double>>>(); 		//a triple ArrayList for grades of all students 
        ArrayList<ArrayList<ArrayList<Integer>>> allWeights = new ArrayList<ArrayList<ArrayList<Integer>>>(); 	//a triple ArrayList containing the weights of all exams/practicals.
        for (String studName : namesOfStudents) {
            ArrayList<Course> courses = new ArrayList<>();
            ArrayList<ArrayList<Double>> studentGrades = new ArrayList<ArrayList<Double>>(); 					
            ArrayList<ArrayList<Integer>> gradeWeights = new ArrayList<ArrayList<Integer>>(); 					
            String studentID = "";
            for (Object record : grade) {
                ArrayList<String> allInfo = new ArrayList<String>(Arrays.asList(((String) record).split(",")));
                // get variables to instantiate `Student` object
                int lastIndex = allInfo.size() - 1;
                String sName = allInfo.get(lastIndex).trim(); 													//trim removes the extra unwanted spaces in the file. 
                if(sName.equals(studName)) {
                    ArrayList<Double> gradeOfCourse = new ArrayList<>(); 										
                    ArrayList<Integer> weightOfGrade = new ArrayList<>(); 										
                    double courseCredit = Double.parseDouble(allInfo.get(1));
                    String courseCode = allInfo.get(0); 
                    ArrayList<Assessment> courseAssignment = new ArrayList<>();
                    ArrayList<String> assessments = new ArrayList<String>(allInfo.subList(3, lastIndex)); 		//this ArrayList contains all the elements like P10(60), E25(60) etc.
                    for (String a : assessments) { 
                        char assessmentType = a.charAt(0);       												// for the type of an assessment ( exam or practical )
                        int assessmentWeight = Integer.parseInt(a.substring(1, a.indexOf('(')));    			// for the weight of an assessment
                        weightOfGrade.add(assessmentWeight);
                        double assessmentGrade = Double.parseDouble(a.substring(a.indexOf('(') + 1, a.indexOf(')')));
                        courseAssignment.add(Assessment.getInstance(assessmentType, assessmentWeight)); 		//an instance of Assessment is created
                        gradeOfCourse.add(assessmentGrade); 
                         
                    }
                    gradeWeights.add(weightOfGrade);
                    studentGrades.add(gradeOfCourse);
                    studentID = allInfo.get(2); 
                    courses.add(new Course(courseCode, courseAssignment, courseCredit));
                }
            }
            students.add(new Student(studentID, studName, courses)); 
            allGrades.add(studentGrades);
            allWeights.add(gradeWeights);
        }
        int nummberOfStudents = students.size();
        for (int i = 0; i < nummberOfStudents; i++) {
            int nummberOfCourses = allGrades.get(i).size(); //getting the number of grades for one student using object 's' and size(). 
            for (int j = 0; j < nummberOfCourses; j++) {
            	try {
            		students.get(i).addGrade(allGrades.get(i).get(j), allWeights.get(i).get(j));
            	}
            	catch(InvalidTotalException e) {
            		e.printStackTrace();
            	}
            }
        }
        return students; 																						//ArrayList is returned 
	}// end of buildStudentArray

    
	/**
	 * This method prints the transcripts for students that are there in the input file.
	 * @param students is the ArrayList for the students whose transcript(s) is/are to be printed
	 */
    public void printTranscript(ArrayList<Student> students) throws IOException {
    	FileWriter outputWriter = new FileWriter(outputFile);
    	try {
	        String dash = "--------------------";
	        for (Student stud : students) {
	        	String firstLine = stud.getName() + "\t" + stud.getStudentID();
	            System.out.println(firstLine);
	            outputWriter.write(firstLine);
	            outputWriter.write("\n");
	            System.out.println(dash);
	            outputWriter.write(dash);
	            outputWriter.write("\n");
	            int size = stud.getCourseTaken().size();
	            for (int i = 0; i < size; i++) { 
	            	String secondLine = stud.getCourseTaken().get(i).getCode() + "\t" + stud.getFinalGrade().get(i);
	            	outputWriter.write(secondLine);
	            	outputWriter.write("\n");
	            	System.out.println(secondLine);
	            }
	            System.out.println(dash);
	            outputWriter.write(dash);
	            outputWriter.write("\n");
	            String line3 = "GPA: " + String.valueOf(stud.weightedGPA()) + "\n";
	            System.out.println(line3);
	            outputWriter.write(line3);
	            outputWriter.write("\n");
	        }
    	}
    	catch (Exception e) {
    		System.out.println("The transcript cannot be printed!");
    		e.printStackTrace();
    	}
    	finally {
    		outputWriter.close();
    	}
    }
} // end of Transcript
       
class InvalidTotalException extends Exception{ 
	public InvalidTotalException() {
		System.out.println("The total of grades is not 100!"); 
	}
} //end of InvalidTotalException

class Student { 
	private String studentID;
	private String name;
	private ArrayList<Course> courseTaken;
	private ArrayList<Double> finalGrade;
	
	/**
	 * returns the name of the student
	 * @return the name of the student
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * returns the student ID of a student
	 * @return student ID of a student
	 */
	public String getStudentID() { 
		return studentID;
	}
	
	/**
	 * returns the ArrayList containing final grades of a student
	 * @return the ArrayList containing final grades of a student
	 */
	public ArrayList<Double> getFinalGrade() {
		return finalGrade;
	}
	
	/**
	 * returns the ArrayList containing the courses that a student has taken
	 * @return the ArrayList containing the courses that a student has taken
	 */
	public ArrayList<Course> getCourseTaken() { 
		return courseTaken; 
	}
	
	
	/**
	 * this is the default constructor for Student class
	 * that initialises the instance variables.
	 */
	public Student() {
		this.studentID = "";
		this.name = "";
		this.finalGrade = new ArrayList<Double>();
		this.courseTaken = new ArrayList<Course>();
	}
	
	/**
	 * This is the parametric constructor for the Student class
	 * that initialises the instance variables with the passed arguments / parameters
	 * @param studID is the student ID for a student
	 * @param name is the name of a student
	 * @param takenCourse is the ArrayList containing courses that a student has taken
	 */
	public Student(String studID, String name, ArrayList<Course> takenCourse) {
		this.studentID = studID; 
		this.name = name;
		this.finalGrade = new ArrayList<Double>();
		this.courseTaken = takenCourse; 
	}
	
	/**
	 * this method adds the course passed as an 
	 * argument / parameter in the corseTaken ArrayList.
	 * @param course is the course to be added, i.e the course the stduent has taken
	 */
	 public void addCourse(Course course) {

        courseTaken.add(course);														        // add given course to courseTaken
     }
	 
	 /**
	  * This method gets an array list of the grades and their weights, computes the true value
      * of the grade based on its weight and add it to finalGrade attribute. In case the sum of the weight was
      * not 100, or the sum of grade was greater 100, it throws InvalidTotalException
	  * @param studGrades an ArrayList containing the grades of a student
	  * @param weights is an ArrayList containing the weights of the assessments for in a course
	  * @throws InvalidTotalException if the total of the weights is not 100
	  */
	 public void addGrade(ArrayList<Double> studGrades, ArrayList<Integer> weights) throws InvalidTotalException  {
        int weightSum = 0;
        for (int w : weights) {
            weightSum += w;
        }
        boolean checked = false;
        if(weightSum != 100) { 
            throw new InvalidTotalException(); 
        }
 
        // calculates the final grade for the course and adds it to the ArrayList finalGrade
        else {
            for( double i : studGrades) {
            	if( i< 0.0 || i > 100) {
            		checked = true;
            		break;
            	}
            }
            if( checked == true) {
            	throw new InvalidTotalException();
            }
            else {
            	int size = studGrades.size();
            	double totGrade = 0.0;
            	for( int i = 0; i < size; i++) {
            		totGrade += (studGrades.get(i) * weights.get(i)) / 100;
            				
            	}
            	totGrade = Math.round(totGrade * 10) / 10.0;
            	finalGrade.add(totGrade);
            }
        }
	 }
	
	/**
	 * this method computes the GPA for a student
	 * @return the computed GPA rounded off to one decimal place
	 */
	public double weightedGPA() {
		double totCredits = 0.0;
        int totalGP = 0;
        int nummberOfCourses = courseTaken.size();
        for (int i = 0; i < nummberOfCourses; i++) {
            double credit = courseTaken.get(i).getCredit();  				//NEW
            double grade = finalGrade.get(i);
            int gp  = 0;
            if (grade >= 90.0) {
                gp = 9;
            }
            else if (grade >= 80.0) {
                gp = 8;
            }
            else if (grade >= 75.0) {
                gp = 7;
            }
            else if (grade >= 70.0) {
                gp = 6;
            }
            else if (grade >= 65.0) {
                gp = 5;
            }
            else if (grade >= 60.0) {
                gp = 4;
            }
            else if (grade >= 55.0) {
                gp = 3;
            }
            else if (grade >= 50.0) {
                gp = 2;
            }
            else if (grade >= 47.0) {
                gp = 1;
            }
            totalGP += (gp * credit);
            totCredits += credit;
        }
        double finalGPA = Math.round((totalGP / totCredits) * 10) / 10.0;
        return finalGPA;
	}
} //end of Student
	
class Course {
	private String code;
	private ArrayList<Assessment> assignment;
	private double credit; 
	
	/**
	 * this is the default constructor for Course class
	 * that initialises the instance variables
	 */
	public Course() {
		this.code = "";	
		this.assignment = new ArrayList<Assessment>();
		this.credit = 0.0 ; 
	}
	
	/**
	 * returns the course code 
	 * @return the course code 
	 */
	public String getCode() { 
		return code;
	}
	
	/**
	 * returns the ArrayList of assignments
	 * @return the ArrayList of assignments
	 */
	public ArrayList<Assessment> getAssignment() {
		return assignment;
	}
	
	/**
	 * returns the number of credits for a course
	 * @return the number of credits for a course
	 */
	public double getCredit() {
		return credit;
	}
	
	/**
	 * this is the parametric constructor for the Course class
	 * that initialises the instance variables with the parameters / arguments passed
	 * @param code is the course code
	 * @param assignment is an ArrayList containing the assignments in the course
	 * @param credit is the number of credits of the course
	 */
	public Course(String code, ArrayList<Assessment> assignment, double credit) {
		this.code = code;
		this.assignment = assignment;
		this.credit = credit;
	} 
	
	/**
	 * this is the copy constructor for Course class that initialises the instance variables 
	 * with the instance variables of the Course object passed as argument/ parameter
	 * 
	 * @param c is the Course object which is to be copied
	 */
	public Course(Course c) { 
		this.code = c.code;
	    this.assignment = c.assignment;
	    this.credit = c.credit;
	}
	
	/**
	 * Override the equals method.It returns true if the courses are equal and false otherwise
	 * Two courses are equal if
	 * 1) they have the same code 2) they have the number of credits 3) they have the same assignments
	 * @return true if the two courses are equal and false otherwise
	 */
	@Override
	public boolean equals(Object o) {
	    if(o == null) {
	        return false;
	    }
	    else if(this == o) {
	        return true;
	    }
	    else if(this.getClass() != o.getClass()) {
	        return false;
	    }
	    else {
	        Course c = (Course) o;
	        if((this.code == c.code) && 
	                (this.credit == c.credit) &&
	                (this.assignment.equals(c.assignment)))
	            return true;
	        else
	            return false;
	    }
	}
}//end of Course
		

class Assessment {
	private char type;
	private int weight;
	
	/**
	 * this is the default constructor for the Assessment class
	 * that initialises its instance variables
	 */
	private Assessment() {
		this.type = '\0'; 
		this.weight = 0; 
	}
	
	/**
	 * this is the parametric constructor for the Assessment class
	 * that initialises its instance variables with the parameters / arguments passed
	 * @param type is the type of the assessment ( practical / exam )
	 * @param weight is the weight for the final grade of the assessment 
	 */
	private Assessment(char type, int weight) {
		this.type = type;
		this.weight = weight; 
	}
	
	/**
	 * this is a static factory method for Assessment class that returns a new instance of the class
	 * using the parametric constructor for the Assessment class
	 * @param type is the type of the assessment
	 * @param weight is the weight in the final grade for the assessment
	 * @return a new instance of the Assessment class using the parametric constructor
	 */
	public static Assessment getInstance(char type, int weight) { 							
		return new Assessment(type, weight);
	}
	
	/**
	 * Override the equals method.It returns true if the assessments are equal and false otherwise
	 * Two courses are equal if
	 * 1) they have the same type 2) they have the same weight
	 * @return true if the two assessments are equal and false otherwise
	 */
	@Override 
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}

		if(o == null) {
			return false;
		}

		if(this.getClass() != o.getClass()) {
			return false;
		}
		else {
			Assessment a = (Assessment) o;
			if((this.type == a.type) && (this.weight == a.weight)) {
				return true;
			}
			else
				return false;
		}
	}
}//end of Assessment

