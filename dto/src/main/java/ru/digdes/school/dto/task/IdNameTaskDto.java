package ru.digdes.school.dto.task;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdNameTaskDto {
    private Long id;
    private String taskName;
}
