package ui.models;

import java.util.Objects;

public final class WebTableRecord {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final int age;
    private final int salary;
    private final String department;

    private WebTableRecord(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.age = builder.age;
        this.salary = builder.salary;
        this.department = builder.department;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .age(age)
                .salary(salary)
                .department(department);
    }

    public static WebTableRecord unique(int index) {
        return builder()
                .firstName("Marta" + index)
                .email("marta.nowak" + index + "@example.com")
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebTableRecord)) return false;
        WebTableRecord that = (WebTableRecord) o;
        return age == that.age
                && salary == that.salary
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(email, that.email)
                && Objects.equals(department, that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, age, salary, department);
    }

    @Override
    public String toString() {
        return "WebTableRecord{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                '}';
    }

    public static final class Builder {
        private String firstName = "Marta";
        private String lastName = "Nowak";
        private String email = "marta.nowak@example.com";
        private int age = 31;
        private int salary = 45000;
        private String department = "Quality";

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder salary(int salary) {
            this.salary = salary;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public WebTableRecord build() {
            return new WebTableRecord(this);
        }
    }
}
