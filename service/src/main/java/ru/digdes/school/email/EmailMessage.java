package ru.digdes.school.email;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmailMessage {
    private String employeeName;
    private String email;
    private String subject;
    private String message;
    private String taskName;
    private String projectName;
    private String deadlineDate;
    private String deadlineTime;
}
