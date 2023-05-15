package ru.digdes.school.model.employee;

public enum JobTitle {
    ACCOUNTANT("бухгалтер"),
    MANAGER("менеджер"),
    SOFTWARE_DEVELOPER("разработчик"),
    TESTER("тестировщик");

    private final String jobTitle;

    JobTitle(String title) {
        this.jobTitle = title;
    }

    public String getJobTitleAsString() {
        return jobTitle;
    }
}
