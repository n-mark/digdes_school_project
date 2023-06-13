package ru.digdes.school.email;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmailMessage {
    private String recipient;
    private String subject;
    private String message;
}
