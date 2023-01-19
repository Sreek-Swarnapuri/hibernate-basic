package org.example;

import org.example.entity.Employee;
import org.example.entity.EmployeeName;
import org.example.entity.relations.one_to_one.Laptop;
import org.example.entity.relations.one_to_one.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Random;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        //Hibernate basic save & get
        Employee e = new Employee(1L, new EmployeeName("Sreekar", "Swarnapuri Pandadu", ""), 30);
        Employee e1 = new Employee(2L, new EmployeeName("Vandana", "Swarnapuri Pandadu", ""), 28);
        Employee e1db;

        Configuration config = new Configuration().configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Laptop.class).addAnnotatedClass(Student.class)
                .addAnnotatedClass(org.example.entity.relations.one_to_many.Laptop.class).addAnnotatedClass(org.example.entity.relations.one_to_many.Student.class)
                .addAnnotatedClass(org.example.entity.relations.many_to_many.Laptop.class).addAnnotatedClass(org.example.entity.relations.many_to_many.Student.class)
                .addAnnotatedClass(org.example.entity.Student.class);

        SessionFactory sf = config.buildSessionFactory();

        Session s = sf.openSession();

        Transaction tx = s.beginTransaction();
        s.save(e);
        s.save(e1);
        tx.commit();

        e1db = s.get(Employee.class, 1L);

        System.out.println(e1db);

        /*
        Total output will be - there would not be any select call as the data is already cached in ehcache:
        Employee{e_id=2, name=EmployeeName{fname='Vandana', lname='Swarnapuri Pandadu', mname=''}, age=28}
        Employee{e_id=2, name=EmployeeName{fname='Vandana', lname='Swarnapuri Pandadu', mname=''}, age=28}
        Employee{e_id=2, name=EmployeeName{fname='Vandana', lname='Swarnapuri Pandadu', mname=''}, age=28}
         */

        s.close();

        // Hibernate relations

        // one to one
        Laptop laptop = new Laptop();
        laptop.setLid(101);
        laptop.setLname("Samsung");

        Student student = new Student();
        student.setRollNo(1);
        student.setName("Sreek");
        student.setMarks(81);
        student.setLaptop(laptop);

        s = sf.openSession();
        s.beginTransaction();

        s.save(laptop);
        s.save(student);

        s.getTransaction().commit();

        s.close();

        // one to many
        org.example.entity.relations.one_to_many.Laptop laptop1 = new org.example.entity.relations.one_to_many.Laptop();
        laptop1.setLid(101);
        laptop1.setLname("MacBook Pro");

        org.example.entity.relations.one_to_many.Student student1 = new org.example.entity.relations.one_to_many.Student();
        student1.setRollNo(1);
        student1.setName("Vandana");
        student1.setMarks(90);
        student1.getLaptops().add(laptop1);

        laptop1.setStudent(student1);


        org.example.entity.relations.one_to_many.Laptop laptop_otm_1 = new org.example.entity.relations.one_to_many.Laptop();
        laptop_otm_1.setLid(102);
        laptop_otm_1.setLname("Lenovo Legion");

        org.example.entity.relations.one_to_many.Student student_otm_1 = new org.example.entity.relations.one_to_many.Student();
        student_otm_1.setRollNo(2);
        student_otm_1.setName("Sreekar");
        student_otm_1.setMarks(90);
        student_otm_1.getLaptops().add(laptop_otm_1);

        laptop_otm_1.setStudent(student_otm_1);

        s = sf.openSession();
        s.beginTransaction();

        s.save(laptop1);
        s.save(student1);
        s.save(laptop_otm_1);
        s.save(student_otm_1);

        s.getTransaction().commit();

        s.close();

        //many to many
        org.example.entity.relations.many_to_many.Laptop laptop2 = new org.example.entity.relations.many_to_many.Laptop();
        laptop2.setLid(101);
        laptop2.setLname("Alienware");

        org.example.entity.relations.many_to_many.Student student2 = new org.example.entity.relations.many_to_many.Student();
        student2.setRollNo(1);
        student2.setName("Sai");
        student2.setMarks(99);
        student2.getLaptops().add(laptop2);

        laptop2.getStudents().add(student2);

        s = sf.openSession();
        s.beginTransaction();

        s.save(laptop2);
        s.save(student2);

        s.getTransaction().commit();

        s.close();


        // fetch types - EAGER and LAZY (by default hibernate is lazy)
        // using the already existing one to many relationships

        s = sf.openSession();
        s.beginTransaction();

        org.example.entity.relations.one_to_many.Student student_otm_2 = s.get(org.example.entity.relations.one_to_many.Student.class, 2);
        System.out.println(student_otm_2);
        System.out.println(student_otm_2.getLaptops());

        s.getTransaction().commit();

        s.close();


        // Hibernate Caching - Level 1

        // If we try to get the same value again using the same session, hibernate will use the already fetched data but not trigger another select query
        System.out.println("-----------Hibernate Caching Level 1 -----------");
        s = sf.openSession();
        s.beginTransaction();

        Student student3 = s.get(Student.class, 1);
        System.out.println(student3);

        Student student4 = s.get(Student.class, 1);
        System.out.println(student4);

        /*

        Output for above print statments will be

        Hibernate: select student0_.rollNo as rollno1_5_0_, student0_.laptop_lid as laptop_l4_5_0_, student0_.marks as marks2_5_0_, student0_.name as name3_5_0_, laptop1_.lid as lid1_1_1_, laptop1_.lname as lname2_1_1_ from Student student0_ left outer join Laptop laptop1_ on student0_.laptop_lid=laptop1_.lid where student0_.rollNo=?
        Student{rollNo=1, name='Sreek', marks=81}
        Student{rollNo=1, name='Sreek', marks=81}

         */

        s.getTransaction().commit();
        s.close();

        // If we try to get the same value using a different session, it will trigger a select query. First level cache is only for the same session, provided by Hibernate.

        s = sf.openSession();
        s.beginTransaction();

        Student student5 = s.get(Student.class, 1);
        System.out.println(student5);

        /*
        Total output with the above session will be:
        Hibernate: select student0_.rollNo as rollno1_5_0_, student0_.laptop_lid as laptop_l4_5_0_, student0_.marks as marks2_5_0_, student0_.name as name3_5_0_, laptop1_.lid as lid1_1_1_, laptop1_.lname as lname2_1_1_ from Student student0_ left outer join Laptop laptop1_ on student0_.laptop_lid=laptop1_.lid where student0_.rollNo=?
        Student{rollNo=1, name='Sreek', marks=81}
        Student{rollNo=1, name='Sreek', marks=81}
        Hibernate: select student0_.rollNo as rollno1_5_0_, student0_.laptop_lid as laptop_l4_5_0_, student0_.marks as marks2_5_0_, student0_.name as name3_5_0_, laptop1_.lid as lid1_1_1_, laptop1_.lname as lname2_1_1_ from Student student0_ left outer join Laptop laptop1_ on student0_.laptop_lid=laptop1_.lid where student0_.rollNo=?
        Student{rollNo=1, name='Sreek', marks=81}
         */

        s.getTransaction().commit();
        s.close();

        // Hibernate - Using second level cache
        // 1. Get the two dependencies -  cache library ( ehcache ) and cache - hibernate link library ( hibernate-ehcache )
        // 2. Set the configuration in hibernate.cfg.xml to enable 2nd level cache and specify the hibernate.cache.region.factory_class class to be used from the cache - hibernate link library
        // 3. Enable Caching annotations in the Entity class @Cacheable and @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)

        s = sf.openSession();
        s.beginTransaction();

        Employee e2 = s.get(Employee.class, 2L);
        System.out.println(e2);

        Employee e3 = s.get(Employee.class, 2L);
        System.out.println(e3);

        s.getTransaction().commit();
        s.close();

        s = sf.openSession();
        s.beginTransaction();

        Employee e4 = s.get(Employee.class, 2L);
        System.out.println(e4);

        // Total output will be as follows:
        /*
        Employee{e_id=2, name=EmployeeName{fname='Vandana', lname='Swarnapuri Pandadu', mname=''}, age=28}
        Employee{e_id=2, name=EmployeeName{fname='Vandana', lname='Swarnapuri Pandadu', mname=''}, age=28}
        Employee{e_id=2, name=EmployeeName{fname='Vandana', lname='Swarnapuri Pandadu', mname=''}, age=28}
         */

        s.getTransaction().commit();
        s.close();

        // Using query level cache for 2nd level ehcache library
        // - we need to enable a property in hibernate.cfg.xml for query cache.

        s = sf.openSession();
        s.beginTransaction();

        Query q = s.createQuery("from Employee where e_id=1");
        q.setCacheable(true);
        Employee e5 = (Employee) q.uniqueResult();
        System.out.println(e5);

        s.getTransaction().commit();
        s.close();

        s = sf.openSession();
        s.beginTransaction();

        Query q1 = s.createQuery("from Employee where e_id=1");
        q1.setCacheable(true);
        Employee e6 = (Employee) q1.uniqueResult();
        System.out.println(e6);
        /*
        Total output is as follows, with query triggered only once -
        Hibernate: select employee0_.e_id as e_id1_0_, employee0_.e_age as e_age2_0_, employee0_.fname as fname3_0_, employee0_.lname as lname4_0_, employee0_.mname as mname5_0_ from EMPLOYEE_TABLE employee0_ where employee0_.e_id=1
        Employee{e_id=1, name=EmployeeName{fname='Sreekar', lname='Swarnapuri Pandadu', mname=''}, age=30}
        Employee{e_id=1, name=EmployeeName{fname='Sreekar', lname='Swarnapuri Pandadu', mname=''}, age=30}
         */

        s.getTransaction().commit();
        s.close();



        // *************************************** Hibernate Query Language ***************************************

        s = sf.openSession();
        s.beginTransaction();
        Random r = new Random();

        for(int i = 0; i <= 50 ; i++) {
            org.example.entity.Student student_single = new org.example.entity.Student();
            student_single.setRollno(i);
            student_single.setName("Name" + i);
            student_single.setMarks(r.nextInt(100));
            s.save(student_single);
        }

        // Get All students
        Query<org.example.entity.Student> qr = s.createQuery("from student_single");
        List<org.example.entity.Student> students = qr.list();

        System.out.println("------- ALL Students -------");
        for(org.example.entity.Student st: students){
            System.out.println(st);
        }

        // Get Students who have marks more than 35

        Query<org.example.entity.Student> qr1 = s.createQuery("from student_single where marks>35");
        List<org.example.entity.Student> students1 = qr1.list();

        System.out.println("------- Students with more than 35 marks -------");
        for(org.example.entity.Student st: students1) {
            System.out.println(st);
        }

        // Get a particular student
        Query<org.example.entity.Student> qr2 = s.createQuery("from student_single where rollno=9");
        org.example.entity.Student st1 = qr2.uniqueResult();
        System.out.println("------- Student with roll no: 9 -------");
        System.out.println(st1);

        // Querying for specific columns
        Query qr3 = s.createQuery("select rollno, name, marks from student_single where rollno=21");
        Object[] students2 = (Object[]) qr3.uniqueResult();
        System.out.println("------- Student with roll no: 21 -------");
        System.out.println(students2[0] + ":" + students2[1] + ":" + students2[2]);

        Query qr4 = s.createQuery("select rollno, name, marks from student_single");
        List<Object[]> studentlist2 = (List<Object[]>) qr4.list();
        System.out.println("------- Students with query using specific columns -------");
        for(Object[] st: studentlist2)
            System.out.println(st[0] + ":" + st[1] + ":" + st[2]);

        Query qr5 = s.createQuery("select rollno, name, marks from student_single s where s.marks>60");
        List<Object[]> studentlist3 = (List<Object[]>) qr5.list();
        System.out.println("------- Students with query using specific columns where marks > 60 -------");
        for(Object[] st: studentlist3)
            System.out.println(st[0] + ":" + st[1] + ":" + st[2]);

        Query qr6 = s.createQuery("select sum(marks) from student_single s where s.marks>60");
        Long totalStudentMarks = (Long) qr6.uniqueResult();
        System.out.println("------- sum of marks of Students with query using specific columns where marks > 60 -------");
        System.out.println("Total marks: " + totalStudentMarks);

        //injecting values in where clause using HQL
        int b = 90;
        Query qr7 = s.createQuery("select sum(marks) from student_single s where s.marks> :b");
        qr7.setParameter("b", b);
        Long totalStudentMarks1 = (Long) qr7.uniqueResult();
        System.out.println("------- sum of marks of Students with query using specific columns where marks > 90 -------");
        System.out.println("Total marks: " + totalStudentMarks1);

        s.getTransaction().commit();
        s.close();
        
    }
}
